package com.mybot.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final String CONFIG_FILE = "bot-config.properties";
    private static Properties properties;
    
    static {
        loadProperties();
    }
    
    private static void loadProperties() {
        properties = new Properties();
        
        try {
            // 1. Пробуем загрузить из текущей директории
            File configFile = new File(CONFIG_FILE);
            if (configFile.exists()) {
                System.out.println("✅ Загружаю конфигурацию из: " + configFile.getAbsolutePath());
                properties.load(new FileInputStream(configFile));
            } 
            // 2. Пробуем загрузить из classpath (для IDE)
            else {
                InputStream input = ConfigLoader.class.getClassLoader()
                    .getResourceAsStream(CONFIG_FILE);
                if (input != null) {
                    System.out.println("✅ Загружаю конфигурацию из classpath");
                    properties.load(input);
                } else {
                    throw new RuntimeException(
                        "❌ Файл конфигурации не найден!\n" +
                        "Создайте файл 'bot-config.properties' в корне проекта.\n" +
                        "Содержимое файла:\n" +
                        "bot.token=ваш_токен_бота\n" +
                        "bot.username=имя_бота\n" +
                        "weather.api.key=ваш_ключ_api"
                    );
                }
            }
            
            // Проверяем, что все обязательные поля заполнены
            checkRequiredFields();
            
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки конфигурации: " + e.getMessage(), e);
        }
    }
    
    private static void checkRequiredFields() {
        String[] required = {"bot.token", "bot.username", "weather.api.key"};
        
        for (String field : required) {
            String value = properties.getProperty(field);
            if (value == null || value.trim().isEmpty() || value.contains("ВАШ_")) {
                System.out.println("\n⚠️  ⚠️  ⚠️  ВНИМАНИЕ! ⚠️  ⚠️  ⚠️");
                System.out.println("Поле '" + field + "' не заполнено!");
                System.out.println("Откройте файл 'bot-config.properties' и заполните его:");
                System.out.println("1. bot.token - токен от @BotFather");
                System.out.println("2. bot.username - имя бота");
                System.out.println("3. weather.api.key - ключ от weatherstack.com");
                System.out.println("\nБот НЕ будет работать пока вы не заполните эти поля!");
                System.out.println("⚠️  ⚠️  ⚠️  ⚠️  ⚠️  ⚠️  ⚠️  ⚠️  ⚠️  ⚠️\n");
                
                // Даем возможность продолжить, но предупреждаем
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {}
            }
        }
    }
    
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    // Новый метод для boolean значений
    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.toLowerCase());
    }
}