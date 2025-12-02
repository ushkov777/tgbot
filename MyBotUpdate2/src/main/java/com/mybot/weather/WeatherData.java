package com.mybot.weather;

public class WeatherData {
    private String city;
    private String country;
    private double temperature;
    private double feelsLike;
    private int humidity;
    private double pressure;
    private String description;
    private double windSpeed;
    private String windDirection;
    private String observationTime;

    public WeatherData(String city, String country, double temperature, double feelsLike, 
                      int humidity, double pressure, String description, 
                      double windSpeed, String windDirection, String observationTime) {
        this.city = city;
        this.country = country;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.pressure = pressure;
        this.description = description;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.observationTime = observationTime;
    }

    @Override
    public String toString() {
        return String.format(
            "%s Погода в %s, %s\n\n" +
            "🌡 Температура: %.1f°C\n" +
            "🤔 Ощущается как: %.1f°C\n" +
            "💧 Влажность: %d%%\n" +
            "📊 Давление: %.0f мм рт.ст.\n" +
            "💨 Ветер: %.1f км/ч, %s\n" +
            "📝 Описание: %s\n\n" +
            "🕐 Время данных: %s",
            getWeatherEmoji(), city, country, temperature, feelsLike, 
            humidity, pressure, windSpeed, windDirection, description, observationTime
        );
    }

    private String getWeatherEmoji() {
        if (description == null) return "🌤";
        
        String desc = description.toLowerCase();
        
        if (desc.contains("sun") || desc.contains("clear")) return "☀️";
        if (desc.contains("partly cloudy")) return "⛅️";
        if (desc.contains("cloud") || desc.contains("overcast")) return "☁️";
        if (desc.contains("rain")) return "🌧️";
        if (desc.contains("snow")) return "❄️";
        if (desc.contains("fog") || desc.contains("mist")) return "🌫️";
        if (desc.contains("thunder")) return "⛈️";
        if (desc.contains("drizzle")) return "🌦️";
        
        return "🌤️";
    }

    // Геттеры
    public String getCity() { return city; }
    public String getCountry() { return country; }
    public double getTemperature() { return temperature; }
    public double getFeelsLike() { return feelsLike; }
    public int getHumidity() { return humidity; }
    public double getPressure() { return pressure; }
    public String getDescription() { return description; }
    public double getWindSpeed() { return windSpeed; }
    public String getWindDirection() { return windDirection; }
    public String getObservationTime() { return observationTime; }
}