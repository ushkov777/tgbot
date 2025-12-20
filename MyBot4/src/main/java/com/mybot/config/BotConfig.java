package com.mybot.config;

public class BotConfig {
    // Telegram Bot
    public static final String BOT_TOKEN;
    public static final String BOT_USERNAME;
    
    // Weatherstack API
    public static final String WEATHER_API_KEY;
    public static final String WEATHER_API_URL;
    
    // Настройки приложения
    public static final int API_RETRY_COUNT;
    public static final long API_RETRY_DELAY_MS;
    
    // Настройки кэширования
    public static final int CACHE_TTL_MINUTES;
    public static final int CACHE_MAX_SIZE;
    public static final boolean USE_CACHE;
    
    // Статический блок инициализации
    static {
        // Загружаем значения из конфигурации
        BOT_TOKEN = ConfigLoader.getProperty("bot.token");
        BOT_USERNAME = ConfigLoader.getProperty("bot.username");
        
        WEATHER_API_KEY = ConfigLoader.getProperty("weather.api.key");
        WEATHER_API_URL = ConfigLoader.getProperty("weather.api.url", 
            "http://api.weatherstack.com/current");
        
        API_RETRY_COUNT = ConfigLoader.getInt("api.retry.count", 3);
        API_RETRY_DELAY_MS = ConfigLoader.getInt("api.retry.delay.ms", 1000);
        
        // Настройки кэширования
        CACHE_TTL_MINUTES = ConfigLoader.getInt("cache.ttl.minutes", 10);
        CACHE_MAX_SIZE = ConfigLoader.getInt("cache.max.size", 100);
        USE_CACHE = true;
        
        // Выводим информацию о загруженной конфигурации
        System.out.println("\n📋 Загружена конфигурация:");
        System.out.println("   🤖 Бот: @" + BOT_USERNAME);
        System.out.println("   🌤 API: " + WEATHER_API_URL);
        System.out.println("   🔄 Повторных попыток: " + API_RETRY_COUNT);
        System.out.println("   ⚡ Кэширование: " + (USE_CACHE ? "ВКЛ" : "ВЫКЛ"));
        System.out.println("   💾 TTL кэша: " + CACHE_TTL_MINUTES + " мин");
        System.out.println("   📊 Размер кэша: " + CACHE_MAX_SIZE + " городов");
        System.out.println();
    }
    
    // Метод для проверки конфигурации
    public static void validateConfig() {
        if (BOT_TOKEN == null || BOT_TOKEN.isEmpty() || BOT_TOKEN.contains("ВАШ_")) {
            throw new IllegalStateException("BOT_TOKEN не настроен в bot-config.properties");
        }
        if (WEATHER_API_KEY == null || WEATHER_API_KEY.isEmpty() || WEATHER_API_KEY.contains("ВАШ_")) {
            throw new IllegalStateException("WEATHER_API_KEY не настроен в bot-config.properties");
        }
    }
}