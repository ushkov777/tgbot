package com.mybot.weather;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WeatherDataTest {
    
    @Test
    void testWeatherDataCreation() {
        WeatherData weatherData = new WeatherData(
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
        
        assertEquals("Moscow", weatherData.getCity());
        assertEquals("Russia", weatherData.getCountry());
        assertEquals(20.0, weatherData.getTemperature());
        assertEquals(19.5, weatherData.getFeelsLike());
        assertEquals(65, weatherData.getHumidity());
        assertEquals(760.0, weatherData.getPressure());
        assertEquals("Clear sky", weatherData.getDescription());
        assertEquals(10.0, weatherData.getWindSpeed());
        assertEquals("N", weatherData.getWindDirection());
        assertEquals("2023-10-01 12:00", weatherData.getObservationTime());
    }
    
    @Test
    void testToString() {
        WeatherData weatherData = new WeatherData(
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
        
        String result = weatherData.toString();
        
        assertNotNull(result);
        assertTrue(result.contains("Moscow"));
        assertTrue(result.contains("Russia"));
        assertTrue(result.contains("20.0"));
        assertTrue(result.contains("19.5"));
        assertTrue(result.contains("65"));
        assertTrue(result.contains("760.0"));
        assertTrue(result.contains("Clear sky"));
        assertTrue(result.contains("10.0"));
        assertTrue(result.contains("N"));
        assertTrue(result.contains("2023-10-01 12:00"));
    }
    
    @Test
    void testGetWeatherEmoji_Sunny() {
        WeatherData weatherData = new WeatherData(
            "Moscow", "Russia", 20.0, 19.5, 65, 760.0,
            "Sunny", 10.0, "N", "2023-10-01 12:00"
        );
        
        String result = weatherData.toString();
        assertTrue(result.contains("☀️"));
    }
    
    @Test
    void testGetWeatherEmoji_PartlyCloudy() {
        WeatherData weatherData = new WeatherData(
            "Moscow", "Russia", 20.0, 19.5, 65, 760.0,
            "Partly cloudy", 10.0, "N", "2023-10-01 12:00"
        );
        
        String result = weatherData.toString();
        assertTrue(result.contains("⛅️"));
    }
    
    @Test
    void testGetWeatherEmoji_Cloudy() {
        WeatherData weatherData = new WeatherData(
            "Moscow", "Russia", 20.0, 19.5, 65, 760.0,
            "Cloudy", 10.0, "N", "2023-10-01 12:00"
        );
        
        String result = weatherData.toString();
        assertTrue(result.contains("☁️"));
    }
    
    @Test
    void testGetWeatherEmoji_Rain() {
        WeatherData weatherData = new WeatherData(
            "Moscow", "Russia", 20.0, 19.5, 65, 760.0,
            "Rain", 10.0, "N", "2023-10-01 12:00"
        );
        
        String result = weatherData.toString();
        assertTrue(result.contains("🌧️"));
    }
    
    @Test
    void testGetWeatherEmoji_Snow() {
        WeatherData weatherData = new WeatherData(
            "Moscow", "Russia", 20.0, 19.5, 65, 760.0,
            "Snow", 10.0, "N", "2023-10-01 12:00"
        );
        
        String result = weatherData.toString();
        assertTrue(result.contains("❄️"));
    }
    
    @Test
    void testGetWeatherEmoji_Thunder() {
        WeatherData weatherData = new WeatherData(
            "Moscow", "Russia", 20.0, 19.5, 65, 760.0,
            "Thunderstorm", 10.0, "N", "2023-10-01 12:00"
        );
        
        String result = weatherData.toString();
        assertTrue(result.contains("⛈️"));
    }
    
    @Test
    void testGetWeatherEmoji_Default() {
        WeatherData weatherData = new WeatherData(
            "Moscow", "Russia", 20.0, 19.5, 65, 760.0,
            "Unknown condition", 10.0, "N", "2023-10-01 12:00"
        );
        
        String result = weatherData.toString();
        assertTrue(result.contains("🌤️"));
    }
}