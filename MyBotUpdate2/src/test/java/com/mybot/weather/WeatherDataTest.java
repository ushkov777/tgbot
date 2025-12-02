package com.mybot.weather;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class WeatherDataTest {

    @Test
    void testWeatherDataCreation() {
        WeatherData weatherData = new WeatherData(
            "Москва", "Russia", 20.0, 19.0, 65, 760.0,
            "Sunny", 10.0, "N", "2023-10-01 12:00"
        );

        assertNotNull(weatherData);
        assertEquals("Москва", weatherData.getCity());
        assertEquals("Russia", weatherData.getCountry());
        assertEquals(20.0, weatherData.getTemperature());
        assertEquals(19.0, weatherData.getFeelsLike());
        assertEquals(65, weatherData.getHumidity());
        assertEquals(760.0, weatherData.getPressure());
        assertEquals("Sunny", weatherData.getDescription());
        assertEquals(10.0, weatherData.getWindSpeed());
        assertEquals("N", weatherData.getWindDirection());
        assertEquals("2023-10-01 12:00", weatherData.getObservationTime());
    }

    @Test
    void testToString() {
        WeatherData weatherData = new WeatherData(
            "Москва", "Russia", 20.5, 19.8, 65, 760.5,
            "Sunny", 10.5, "N", "2023-10-01 12:00"
        );

        String result = weatherData.toString();
        assertNotNull(result);
        assertTrue(result.contains("Москва"));
        assertTrue(result.contains("20.5"));
        assertTrue(result.contains("65"));
    }

    @ParameterizedTest
    @CsvSource({
        "Sunny, ☀️",
        "Clear, ☀️", 
        "Cloudy, ☁️",
        "Rain, 🌧️",
        "Snow, ❄️",
        "Fog, 🌫️",
        "Thunderstorm, ⛈️",
        "Drizzle, 🌦️",
        "Unknown, 🌤️"
    })
    void testWeatherEmoji(String description, String expectedEmoji) {
        WeatherData weatherData = new WeatherData(
            "Test", "Country", 20.0, 19.0, 65, 760.0,
            description, 10.0, "N", "2023-10-01 12:00"
        );

        String result = weatherData.toString();
        assertTrue(result.contains(expectedEmoji));
    }
}