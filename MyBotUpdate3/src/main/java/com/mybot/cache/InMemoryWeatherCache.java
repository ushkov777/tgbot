package com.mybot.cache;

import com.mybot.weather.WeatherData;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryWeatherCache implements WeatherCache { 
    // Внутренний класс для хранения данных с временной меткой
    private static class CacheEntry {
        private final WeatherData weatherData;
        private final LocalDateTime timestamp;
        
        public CacheEntry(WeatherData weatherData) {
            this.weatherData = weatherData;
            this.timestamp = LocalDateTime.now();
        }
        
        public WeatherData getWeatherData() {
            return weatherData;
        }
        
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
        
        public boolean isExpired(long ttlMinutes) {
            return ChronoUnit.MINUTES.between(timestamp, LocalDateTime.now()) >= ttlMinutes;
        }
    }
    
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final long ttlMinutes; // Время жизни кэша в минутах
    private final int maxSize;     // Максимальный размер кэша
    
    public InMemoryWeatherCache() {
        this.ttlMinutes = 10; // 10 минут по умолчанию
        this.maxSize = 100;   // 100 городов максимум
    }
    
    public InMemoryWeatherCache(long ttlMinutes, int maxSize) {
        this.ttlMinutes = ttlMinutes;
        this.maxSize = maxSize;
    }
    
    @Override
    public void put(String city, WeatherData weatherData) {
        // Очищаем устаревшие записи перед добавлением
        cleanupExpired();
        
        // Проверяем размер кэша
        if (cache.size() >= maxSize) {
            // Удаляем самую старую запись
            removeOldest();
        }
        
        String normalizedCity = city.toLowerCase().trim();
        cache.put(normalizedCity, new CacheEntry(weatherData));
        
        System.out.println("💾 Данные о погоде для " + city + " сохранены в кэш");
    }
    
    @Override
    public WeatherData get(String city) {
        String normalizedCity = city.toLowerCase().trim();
        CacheEntry entry = cache.get(normalizedCity);
        
        if (entry == null) {
            System.out.println("🔍 " + city + " не найден в кэше");
            return null;
        }
        
        if (entry.isExpired(ttlMinutes)) {
            System.out.println("⏰ Данные для " + city + " устарели");
            cache.remove(normalizedCity);
            return null;
        }
        
        System.out.println("⚡ Данные для " + city + " загружены из кэша");
        return entry.getWeatherData();
    }
    
    @Override
    public boolean contains(String city) {
        String normalizedCity = city.toLowerCase().trim();
        CacheEntry entry = cache.get(normalizedCity);
        
        if (entry == null) return false;
        
        if (entry.isExpired(ttlMinutes)) {
            cache.remove(normalizedCity);
            return false;
        }
        
        return true;
    }
    
    @Override
    public void remove(String city) {
        String normalizedCity = city.toLowerCase().trim();
        cache.remove(normalizedCity);
        System.out.println("🗑️ " + city + " удален из кэша");
    }
    
    @Override
    public void clear() {
        cache.clear();
        System.out.println("🧹 Кэш полностью очищен");
    }
    
    @Override
    public int size() {
        cleanupExpired(); // Очищаем устаревшие перед подсчетом
        return cache.size();
    }
    
    @Override  
    public String getStats() {
        cleanupExpired();
        return String.format(
            "📊 Статистика кэша:\n" +
            "   • Элементов в кэше: %d\n" +
            "   • Максимальный размер: %d\n" +
            "   • TTL (минуты): %d\n" +
            "   • Использование: %.1f%%",
            cache.size(), maxSize, ttlMinutes,
            ((double) cache.size() / maxSize) * 100
        );
    }
    
    /**
     * Удаляет устаревшие записи
     */
    private void cleanupExpired() {
        int removedCount = 0;
        
        for (Map.Entry<String, CacheEntry> entry : cache.entrySet()) {
            if (entry.getValue().isExpired(ttlMinutes)) {
                cache.remove(entry.getKey());
                removedCount++;
            }
        }
        
        if (removedCount > 0) {
            System.out.println("🧹 Удалено " + removedCount + " устаревших записей из кэша");
        }
    }
    
    /**
     * Удаляет самую старую запись
     */
    private void removeOldest() {
        String oldestKey = null;
        LocalDateTime oldestTime = LocalDateTime.now();
        
        for (Map.Entry<String, CacheEntry> entry : cache.entrySet()) {
            if (entry.getValue().getTimestamp().isBefore(oldestTime)) {
                oldestTime = entry.getValue().getTimestamp();
                oldestKey = entry.getKey();
            }
        }
        
        if (oldestKey != null) {
            cache.remove(oldestKey);
            System.out.println("🗑️ Удалена самая старая запись: " + oldestKey);
        }
    }
}