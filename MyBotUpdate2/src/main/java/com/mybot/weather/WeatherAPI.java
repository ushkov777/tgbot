package com.mybot.weather;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mybot.config.BotConfig;
import com.mybot.cache.InMemoryWeatherCache;
import com.mybot.cache.WeatherCache;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class WeatherAPI {
    private final String apiKey;
    private final String baseUrl;
    private final int maxRetries;
    private final long retryDelayMs;
    private final WeatherCache cache;
    private final boolean useCache;
    
    public WeatherAPI() {
        this.apiKey = BotConfig.WEATHER_API_KEY;
        this.baseUrl = BotConfig.WEATHER_API_URL;
        this.maxRetries = BotConfig.API_RETRY_COUNT;
        this.retryDelayMs = BotConfig.API_RETRY_DELAY_MS;
        this.useCache = true; // Включаем кэширование
        this.cache = new InMemoryWeatherCache(10, 100); // TTL 10 мин, макс 100 городов
    }
    
    public WeatherData getWeather(String cityName) throws Exception {
        // 1. Проверяем кэш
        if (useCache) {
            WeatherData cached = cache.get(cityName);
            if (cached != null) {
                System.out.println("⚡ Используем данные из кэша для: " + cityName);
                return cached;
            }
        }
        
        System.out.println("🌤 Запрашиваем свежую погоду для: " + cityName);
        
        // 2. Если нет в кэше, делаем запрос к API
        WeatherData weather = fetchWeatherFromAPI(cityName);
        
        // 3. Сохраняем в кэш
        if (useCache && weather != null) {
            cache.put(cityName, weather);
        }
        
        return weather;
    }
    
    private WeatherData fetchWeatherFromAPI(String cityName) throws Exception {
        String encodedCity = URLEncoder.encode(cityName, StandardCharsets.UTF_8.toString());
        String urlString = String.format("%s?access_key=%s&query=%s&units=m", baseUrl, apiKey, encodedCity);
        
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            HttpURLConnection connection = null;
            try {
                System.out.println("   🔄 Попытка " + attempt + " получить погоду для: " + cityName);
                
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                    );
                    
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    System.out.println("   ✅ Успешно получены данные для: " + cityName);
                    return parseWeatherData(response.toString(), cityName);
                    
                } else if (responseCode == 504 || responseCode == 503 || responseCode == 502) {
                    lastException = new Exception("Временная ошибка сервера (код: " + responseCode + ")");
                    System.out.println("   ⚠️ " + lastException.getMessage());
                    
                } else {
                    throw new Exception("Ошибка при подключении к сервису погоды. Код: " + responseCode);
                }
                
            } catch (Exception e) {
                lastException = e;
                
                if (!isRetryableException(e) || attempt == maxRetries) {
                    break;
                }
                
                System.out.println("   ⏸ Повтор через " + retryDelayMs + "мс");
                
                try {
                    Thread.sleep(retryDelayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
        
        String errorMessage = "Не удалось получить данные о погоде после " + maxRetries + " попыток.";
        if (lastException != null) {
            errorMessage += "\nПричина: " + lastException.getMessage();
        }
        throw new Exception(errorMessage);
    }
    
    /**
     * Определяет, является ли исключение временным и стоит ли повторять запрос
     */
    private boolean isRetryableException(Exception e) {
        String message = e.getMessage();
        if (message == null) return false;
        
        // Повторяем для временных сетевых ошибок и ошибок сервера
        return message.contains("timeout") ||
               message.contains("Timeout") ||
               message.contains("Временная ошибка") ||
               message.contains("502") ||
               message.contains("503") ||
               message.contains("504") ||
               e instanceof java.net.SocketTimeoutException ||
               e instanceof java.net.ConnectException;
    }

    private WeatherData parseWeatherData(String jsonResponse, String requestedCity) throws Exception {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        
        // Проверяем наличие ошибки
        if (jsonObject.has("error")) {
            JsonObject error = jsonObject.getAsJsonObject("error");
            String errorInfo = error.get("info").getAsString();
            throw new Exception("Ошибка API: " + errorInfo);
        }
        
        JsonObject location = jsonObject.getAsJsonObject("location");
        JsonObject current = jsonObject.getAsJsonObject("current");
        
        String city = location.get("name").getAsString();
        String country = location.get("country").getAsString();
        double temperature = current.get("temperature").getAsDouble();
        double feelsLike = current.get("feelslike").getAsDouble();
        int humidity = current.get("humidity").getAsInt();
        double pressureHpa = current.get("pressure").getAsDouble();
        double pressureMmHg = pressureHpa * 0.750062; // Конвертация в мм рт.ст.
        
        String description = current.getAsJsonArray("weather_descriptions").get(0).getAsString();
        double windSpeed = current.get("wind_speed").getAsDouble();
        String windDirection = current.get("wind_dir").getAsString();
        String observationTime = location.get("localtime").getAsString();
        
        return new WeatherData(city, country, temperature, feelsLike, 
                             humidity, pressureMmHg, description, 
                             windSpeed, windDirection, observationTime);
    }
    
    // Добавляем метод для получения статистики кэша
    public String getCacheStats() {
        return cache.getStats();  // <-- ПРОСТО ВЫЗЫВАЕМ МЕТОД ИЗ ИНТЕРФЕЙСА
    }
    
    // Добавляем метод для очистки кэша
    public void clearCache() {
        cache.clear();  // <-- ТОЖЕ ПРОСТО ВЫЗЫВАЕМ МЕТОД ИЗ ИНТЕРФЕЙСА
    }
}