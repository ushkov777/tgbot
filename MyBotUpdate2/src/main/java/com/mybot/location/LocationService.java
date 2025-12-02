package com.mybot.location;

import com.mybot.weather.WeatherAPI;
import com.mybot.weather.WeatherData;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LocationService {
    private WeatherAPI weatherAPI;
    
    // Конструктор БЕЗ параметров (пока что)
    public LocationService() {
        // Конструктор будет пустым, weatherAPI установим позже
    }
    
    // Конструктор с параметром WeatherAPI
    public LocationService(WeatherAPI weatherAPI) {
        this.weatherAPI = weatherAPI;
    }
    
    // Метод для установки WeatherAPI (если используем конструктор без параметров)
    public void setWeatherAPI(WeatherAPI weatherAPI) {
        this.weatherAPI = weatherAPI;
    }
    
    /**
     * Получает погоду по координатам
     */
    public WeatherData getWeatherByCoordinates(double latitude, double longitude) throws Exception {
        if (weatherAPI == null) {
            throw new IllegalStateException("WeatherAPI не инициализирован");
        }
        
        // Weatherstack API поддерживает запросы по координатам
        String query = latitude + "," + longitude;
        return weatherAPI.getWeather(query);
    }
    
    /**
     * Получает название города по координатам (обратное геокодирование)
     */
    public String getCityNameByCoordinates(double latitude, double longitude) {
        try {
            // Используем OpenStreetMap Nominatim API для получения названия города
            String url = String.format(
                "https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f",
                latitude, longitude
            );
            
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "TelegramWeatherBot/1.0");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
                );
                
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                // Парсим JSON ответ
                JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
                
                if (json.has("address")) {
                    JsonObject address = json.getAsJsonObject("address");
                    
                    // Пробуем получить название города, поселка или деревни
                    if (address.has("city")) {
                        return address.get("city").getAsString();
                    } else if (address.has("town")) {
                        return address.get("town").getAsString();
                    } else if (address.has("village")) {
                        return address.get("village").getAsString();
                    } else if (address.has("municipality")) {
                        return address.get("municipality").getAsString();
                    } else if (address.has("county")) {
                        return address.get("county").getAsString();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("[ОШИБКА] Не удалось получить название города: " + e.getMessage());
        }
        
        // Если не удалось получить название, возвращаем координаты
        return String.format("%.4f, %.4f", latitude, longitude);
    }
    
    /**
     * Форматирует координаты для красивого отображения
     */
    public String formatCoordinates(double latitude, double longitude) {
        String latDirection = latitude >= 0 ? "с.ш." : "ю.ш.";
        String lonDirection = longitude >= 0 ? "в.д." : "з.д.";
        
        return String.format("%.4f° %s, %.4f° %s", 
            Math.abs(latitude), latDirection, 
            Math.abs(longitude), lonDirection);
    }
    
    /**
     * Проверяет валидность координат
     */
    public boolean isValidCoordinates(double latitude, double longitude) {
        return latitude >= -90 && latitude <= 90 && 
               longitude >= -180 && longitude <= 180;
    }
    
    /**
     * Получает погоду по геолокационному сообщению Telegram
     */
    public String processLocation(double latitude, double longitude) throws Exception {
        String cityName = getCityNameByCoordinates(latitude, longitude);
        WeatherData weather = getWeatherByCoordinates(latitude, longitude);
        String locationInfo = formatCoordinates(latitude, longitude);
        
        return String.format(
            "📍 Погода по вашей геолокации%n%n" +
            "📌 Местоположение: %s%n" +
            "🏙️ Ближайший населенный пункт: %s%n%n" +
            "%s",
            locationInfo, cityName, weather.toString()
        );
    }
}