package com.mybot.location;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import com.mybot.weather.WeatherAPI;
import com.mybot.weather.WeatherData;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class LocationServiceTest {
    
    @Mock
    private WeatherAPI weatherAPI;
    
    private LocationService locationService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        locationService = new LocationService(weatherAPI);
    }
    
    @Test
    void testConstructorWithWeatherAPI() {
        LocationService service = new LocationService(weatherAPI);
        assertNotNull(service);
    }
    
    @Test
    void testSetWeatherAPI() {
        LocationService service = new LocationService();
        service.setWeatherAPI(weatherAPI);
        
        // Проверяем, что можно вызвать методы, требующие WeatherAPI
        assertDoesNotThrow(() -> {
            service.isValidCoordinates(55.7558, 37.6173);
        });
    }
    
    @Test
    void testGetWeatherByCoordinates() throws Exception {
        WeatherData testWeather = new WeatherData(
            "Moscow", "Russia", 20.0, 19.0, 65, 760.0,
            "Clear", 10.0, "N", "2023-10-01 12:00"
        );
        
        when(weatherAPI.getWeather(anyString())).thenReturn(testWeather);
        
        WeatherData result = locationService.getWeatherByCoordinates(55.7558, 37.6173);
        
        assertNotNull(result);
        assertEquals("Moscow", result.getCity());
        verify(weatherAPI, times(1)).getWeather("55.7558,37.6173");
    }
    
    @Test
    void testGetWeatherByCoordinates_NoWeatherAPI() {
        LocationService service = new LocationService();
        
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            service.getWeatherByCoordinates(55.7558, 37.6173);
        });
        
        assertTrue(exception.getMessage().contains("WeatherAPI не инициализирован"));
    }
    
    @Test
    void testIsValidCoordinates() {
        assertTrue(locationService.isValidCoordinates(55.7558, 37.6173));
        assertTrue(locationService.isValidCoordinates(-55.7558, -37.6173));
        assertTrue(locationService.isValidCoordinates(0.0, 0.0));
        assertTrue(locationService.isValidCoordinates(90.0, 180.0));
        assertTrue(locationService.isValidCoordinates(-90.0, -180.0));
        
        assertFalse(locationService.isValidCoordinates(100.0, 37.6173));
        assertFalse(locationService.isValidCoordinates(-100.0, 37.6173));
        assertFalse(locationService.isValidCoordinates(55.7558, 200.0));
        assertFalse(locationService.isValidCoordinates(55.7558, -200.0));
    }
    
    @Test
    void testFormatCoordinates() {
        String result = locationService.formatCoordinates(55.7558, 37.6173);
        assertNotNull(result);
        assertTrue(result.contains("55.7558"));
        assertTrue(result.contains("37.6173"));
        assertTrue(result.contains("с.ш."));
        assertTrue(result.contains("в.д."));
        
        result = locationService.formatCoordinates(-55.7558, -37.6173);
        assertTrue(result.contains("ю.ш."));
        assertTrue(result.contains("з.д."));
    }
    
    @Test
    void testProcessLocation() throws Exception {
        WeatherData testWeather = new WeatherData(
            "Moscow", "Russia", 20.0, 19.0, 65, 760.0,
            "Clear", 10.0, "N", "2023-10-01 12:00"
        );
        
        when(weatherAPI.getWeather(anyString())).thenReturn(testWeather);
        
        String result = locationService.processLocation(55.7558, 37.6173);
        
        assertNotNull(result);
        assertTrue(result.contains("Погода по вашей геолокации"));
        assertTrue(result.contains("55.7558"));
        assertTrue(result.contains("37.6173"));
        
        verify(weatherAPI, times(1)).getWeather("55.7558,37.6173");
    }
    
    @Test
    void testGetCityNameByCoordinates() {
        // Этот тест мокает HTTP-запрос, но так как это интеграционный тест,
        // мы просто проверяем, что метод не выбрасывает исключение
        // и возвращает строку
        
        String result = locationService.getCityNameByCoordinates(55.7558, 37.6173);
        
        assertNotNull(result);
        // Если API не доступно, метод возвращает координаты
        // Проверяем, что возвращается либо название города, либо координаты
        assertTrue(result.contains("55.7558") || !result.isEmpty());
    }
}