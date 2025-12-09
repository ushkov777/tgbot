package com.mybot.cache;

import com.mybot.weather.WeatherData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryWeatherCacheTest {
    
    private InMemoryWeatherCache cache;
    private WeatherData testWeatherData;
    
    @BeforeEach
    void setUp() {
        cache = new InMemoryWeatherCache(1, 10); // TTL 1 минута, максимум 10 записей
        testWeatherData = new WeatherData(
            "Moscow",
            "Russia",
            20.0,
            19.5,
            65,
            760.0,
            "Clear sky",
            10.0,
            "N",
            "2023-10-01 12:00"
        );
    }
    
    @Test
    void testPutAndGet() {
        cache.put("Moscow", testWeatherData);
        WeatherData retrieved = cache.get("Moscow");
        
        assertNotNull(retrieved);
        assertEquals("Moscow", retrieved.getCity());
        assertEquals("Russia", retrieved.getCountry());
        assertEquals(20.0, retrieved.getTemperature());
    }
    
    @Test
    void testPutAndGet_CaseInsensitive() {
        cache.put("MOSCOW", testWeatherData);
        WeatherData retrieved = cache.get("moscow");
        
        assertNotNull(retrieved);
        assertEquals("Moscow", retrieved.getCity());
    }
    
    @Test
    void testContains() {
        assertFalse(cache.contains("Moscow"));
        
        cache.put("Moscow", testWeatherData);
        assertTrue(cache.contains("Moscow"));
    }
    
    @Test
    void testRemove() {
        cache.put("Moscow", testWeatherData);
        assertTrue(cache.contains("Moscow"));
        
        cache.remove("Moscow");
        assertFalse(cache.contains("Moscow"));
        assertNull(cache.get("Moscow"));
    }
    
    @Test
    void testClear() {
        cache.put("Moscow", testWeatherData);
        cache.put("London", testWeatherData);
        
        assertEquals(2, cache.size());
        
        cache.clear();
        assertEquals(0, cache.size());
        assertFalse(cache.contains("Moscow"));
        assertFalse(cache.contains("London"));
    }
    
    @Test
    void testSize() {
        assertEquals(0, cache.size());
        
        cache.put("Moscow", testWeatherData);
        assertEquals(1, cache.size());
        
        cache.put("London", testWeatherData);
        assertEquals(2, cache.size());
        
        cache.remove("Moscow");
        assertEquals(1, cache.size());
    }
    
    @Test
    void testGetStats() {
        cache.put("Moscow", testWeatherData);
        cache.put("London", testWeatherData);
        
        String stats = cache.getStats();
        assertNotNull(stats);
        assertTrue(stats.contains("Элементов в кэше: 2"));
        assertTrue(stats.contains("Максимальный размер: 10"));
        assertTrue(stats.contains("TTL (минуты): 1"));
    }
    
    @Test
    void testMaxSizeEnforcement() {
        // Создаем кэш с максимальным размером 2
        cache = new InMemoryWeatherCache(10, 2);
        
        cache.put("City1", testWeatherData);
        cache.put("City2", testWeatherData);
        assertEquals(2, cache.size());
        
        // Добавление третьего города должно удалить самый старый
        cache.put("City3", testWeatherData);
        assertEquals(2, cache.size());
        
        // City1 должен быть удален
        assertFalse(cache.contains("City1"));
        assertTrue(cache.contains("City2"));
        assertTrue(cache.contains("City3"));
    }
    
    @Test
    void testDefaultConstructor() {
        InMemoryWeatherCache defaultCache = new InMemoryWeatherCache();
        assertNotNull(defaultCache);
        
        // Проверяем, что можем добавить и получить данные
        defaultCache.put("Moscow", testWeatherData);
        assertNotNull(defaultCache.get("Moscow"));
    }
}