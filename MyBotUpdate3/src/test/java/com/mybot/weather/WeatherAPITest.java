package com.mybot.weather;

import com.mybot.cache.InMemoryWeatherCache;
import com.mybot.config.BotConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WeatherAPITest {
    
    @Mock
    private InMemoryWeatherCache mockCache;
    
    private WeatherAPI weatherAPI;
    
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        weatherAPI = new WeatherAPI();
        
        // Используем рефлексию для замены кэша на мок
        Field cacheField = WeatherAPI.class.getDeclaredField("cache");
        cacheField.setAccessible(true);
        cacheField.set(weatherAPI, mockCache);
        
        Field useCacheField = WeatherAPI.class.getDeclaredField("useCache");
        useCacheField.setAccessible(true);
        useCacheField.set(weatherAPI, true);
    }
    
    @Test
    void testGetWeather_FromCache() throws Exception {
        WeatherData testWeather = new WeatherData(
            "Moscow", "Russia", 20.0, 19.0, 65, 760.0,
            "Clear", 10.0, "N", "2023-10-01 12:00"
        );
        
        when(mockCache.get("Moscow")).thenReturn(testWeather);
        
        WeatherData result = weatherAPI.getWeather("Moscow");
        
        assertNotNull(result);
        assertEquals("Moscow", result.getCity());
        verify(mockCache, times(1)).get("Moscow");
        verify(mockCache, times(0)).put(anyString(), any(WeatherData.class));
    }
    
    @Test
    void testGetWeather_NotInCache() throws Exception {
        when(mockCache.get("Moscow")).thenReturn(null);
        
        // Мокаем HTTP соединение
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        
        // Используем PowerMock или переопределяем метод создания соединения
        // В данном случае, мы просто проверяем, что выбрасывается исключение
        // так как API ключ тестовый
        
        Exception exception = assertThrows(Exception.class, () -> {
            weatherAPI.getWeather("Moscow");
        });
        
        // Должно быть исключение из-за неверного API ключа
        assertNotNull(exception);
        verify(mockCache, times(1)).get("Moscow");
    }
    
    @Test
    void testIsCached() {
        when(mockCache.contains("Moscow")).thenReturn(true);
        when(mockCache.contains("London")).thenReturn(false);
        
        assertTrue(weatherAPI.isCached("Moscow"));
        assertFalse(weatherAPI.isCached("London"));
    }
    
    @Test
    void testIsCached_CacheDisabled() throws Exception {
        Field useCacheField = WeatherAPI.class.getDeclaredField("useCache");
        useCacheField.setAccessible(true);
        useCacheField.set(weatherAPI, false);
        
        assertFalse(weatherAPI.isCached("Moscow"));
        verify(mockCache, times(0)).contains(anyString());
    }
    
    @Test
    void testGetCachedWeather() {
        WeatherData testWeather = new WeatherData(
            "Moscow", "Russia", 20.0, 19.0, 65, 760.0,
            "Clear", 10.0, "N", "2023-10-01 12:00"
        );
        
        when(mockCache.get("Moscow")).thenReturn(testWeather);
        
        WeatherData result = weatherAPI.getCachedWeather("Moscow");
        
        assertNotNull(result);
        assertEquals("Moscow", result.getCity());
        verify(mockCache, times(1)).get("Moscow");
    }
    
    @Test
    void testGetCacheStats() {
        when(mockCache.getStats()).thenReturn("Cache stats: 5/100");
        
        String stats = weatherAPI.getCacheStats();
        
        assertNotNull(stats);
        assertEquals("Cache stats: 5/100", stats);
        verify(mockCache, times(1)).getStats();
    }
    
    @Test
    void testClearCache() {
        weatherAPI.clearCache();
        
        verify(mockCache, times(1)).clear();
    }
    
    @Test
    void testParseWeatherData() throws Exception {
        String jsonResponse = "{\"location\":{\"name\":\"Moscow\",\"country\":\"Russia\",\"localtime\":\"2023-10-01 12:00\"},\"current\":{\"temperature\":20.0,\"feelslike\":19.0,\"humidity\":65,\"pressure\":1013.0,\"weather_descriptions\":[\"Clear\"],\"wind_speed\":10.0,\"wind_dir\":\"N\"}}";
        
        var method = WeatherAPI.class.getDeclaredMethod("parseWeatherData", 
            String.class, String.class);
        method.setAccessible(true);
        
        WeatherData result = (WeatherData) method.invoke(weatherAPI, jsonResponse, "Moscow");
        
        assertNotNull(result);
        assertEquals("Moscow", result.getCity());
        assertEquals("Russia", result.getCountry());
        assertEquals(20.0, result.getTemperature());
        assertEquals(19.0, result.getFeelsLike());
        assertEquals(65, result.getHumidity());
        assertEquals(1013.0 * 0.750062, result.getPressure(), 0.001);
        assertEquals("Clear", result.getDescription());
        assertEquals(10.0, result.getWindSpeed());
        assertEquals("N", result.getWindDirection());
        assertEquals("2023-10-01 12:00", result.getObservationTime());
    }
    
    
    @Test
    void testIsRetryableException() throws Exception {
        var method = WeatherAPI.class.getDeclaredMethod("isRetryableException", Exception.class);
        method.setAccessible(true);
        
        Exception timeoutEx = new Exception("timeout");
        Exception connectEx = new Exception("ConnectException");
        Exception serverErrorEx = new Exception("Временная ошибка сервера (код: 502)");
        Exception otherEx = new Exception("Other error");
        
        assertTrue((Boolean) method.invoke(weatherAPI, timeoutEx));
        assertTrue((Boolean) method.invoke(weatherAPI, connectEx));
        assertTrue((Boolean) method.invoke(weatherAPI, serverErrorEx));
        assertFalse((Boolean) method.invoke(weatherAPI, otherEx));
    }
}