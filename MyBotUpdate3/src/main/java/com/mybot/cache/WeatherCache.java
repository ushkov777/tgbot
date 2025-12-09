package com.mybot.cache;

import com.mybot.weather.WeatherData;

public interface WeatherCache {
    void put(String city, WeatherData weatherData);
    WeatherData get(String city);
    boolean contains(String city);
    void remove(String city);
    void clear();
    int size();
    String getStats();  
}