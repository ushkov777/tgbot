package com.mybot.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.mybot.weather.WeatherAPI;
import com.mybot.weather.WeatherData;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommandHandlerTest {
    
    @Mock
    private WeatherAPI weatherAPI;
    
    @Mock
    private org.telegram.telegrambots.bots.TelegramLongPollingBot bot;
    
    private CommandHandler commandHandler;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commandHandler = new CommandHandler(weatherAPI);
    }
    
    @Test
    void testHandleCommand_Start() throws TelegramApiException {
        commandHandler.handleCommand("/start", 12345L, "TestUser", bot);
        
        verify(bot, atLeastOnce()).execute(any(SendMessage.class));
    }
    
    @Test
    void testHandleCommand_Help() throws TelegramApiException {
        commandHandler.handleCommand("/help", 12345L, "TestUser", bot);
        
        verify(bot, atLeastOnce()).execute(any(SendMessage.class));
    }
    
    @Test
    void testHandleCommand_HelpWithArgument() throws TelegramApiException {
        commandHandler.handleCommand("/help weather", 12345L, "TestUser", bot);
        
        verify(bot, atLeastOnce()).execute(any(SendMessage.class));
    }
    
    @Test
    void testHandleCommand_CacheStats() throws TelegramApiException {
        when(weatherAPI.getCacheStats()).thenReturn("Cache stats: 5/100");
        
        commandHandler.handleCommand("/cachestats", 12345L, "TestUser", bot);
        
        verify(weatherAPI, times(1)).getCacheStats();
        verify(bot, atLeastOnce()).execute(any(SendMessage.class));
    }
    
    @Test
    void testHandleCommand_ClearCache() throws TelegramApiException {
        commandHandler.handleCommand("/clearcache", 12345L, "TestUser", bot);
        
        verify(weatherAPI, times(1)).clearCache();
        verify(bot, atLeastOnce()).execute(any(SendMessage.class));
    }
    
    @Test
    void testHandleCommand_Location() throws TelegramApiException {
        commandHandler.handleCommand("/location", 12345L, "TestUser", bot);
        
        verify(bot, atLeastOnce()).execute(any(SendMessage.class));
    }
    
    @Test
    void testHandleCommand_Weather_NoCity() throws TelegramApiException {
        commandHandler.handleCommand("/weather", 12345L, "TestUser", bot);
        
        verify(bot, atLeastOnce()).execute(any(SendMessage.class));
    }
    
    @Test
    void testHandleCommand_Weather_WithCity() throws Exception {
        WeatherData testWeather = new WeatherData(
            "Moscow", "Russia", 20.0, 19.0, 65, 760.0,
            "Clear", 10.0, "N", "2023-10-01 12:00"
        );
        
        when(weatherAPI.getWeather(anyString())).thenReturn(testWeather);
        
        commandHandler.handleCommand("/weather Moscow", 12345L, "TestUser", bot);
        
        verify(weatherAPI, times(1)).getWeather("Moscow");
        verify(bot, atLeast(2)).execute(any(SendMessage.class));
    }
    
    @Test
    void testHandleCommand_Weather_WithCoordinates() throws TelegramApiException {
        commandHandler.handleCommand("/weather 55.7558,37.6173", 12345L, "TestUser", bot);
        
        verify(bot, atLeastOnce()).execute(any(SendMessage.class));
    }
    
    @Test
    void testHandleCommand_Weather_API_Exception() throws Exception {
        when(weatherAPI.getWeather(anyString()))
            .thenThrow(new Exception("City not found"));
        
        commandHandler.handleCommand("/weather InvalidCity", 12345L, "TestUser", bot);
        
        verify(weatherAPI, times(1)).getWeather("InvalidCity");
        verify(bot, atLeastOnce()).execute(any(SendMessage.class));
    }
    
    @Test
    void testHandleCommand_About() throws TelegramApiException {
        commandHandler.handleCommand("/about", 12345L, "TestUser", bot);
        
        verify(bot, atLeastOnce()).execute(any(SendMessage.class));
    }
    
    @Test
    void testHandleCommand_Authors() throws TelegramApiException {
        commandHandler.handleCommand("/authors", 12345L, "TestUser", bot);
        
        verify(bot, atLeastOnce()).execute(any(SendMessage.class));
    }
    
    @Test
    void testHandleCommand_Inline() throws TelegramApiException {
        commandHandler.handleCommand("/inline", 12345L, "TestUser", bot);
        
        verify(bot, atLeastOnce()).execute(any(SendMessage.class));
    }
    
    @Test
    void testHandleCommand_Unknown() throws TelegramApiException {
        commandHandler.handleCommand("/unknowncommand", 12345L, "TestUser", bot);
        
        verify(bot, atLeastOnce()).execute(any(SendMessage.class));
    }
    
    @Test
    void testGetWeatherAPI() {
        WeatherAPI api = commandHandler.getWeatherAPI();
        assertSame(weatherAPI, api);
    }
}