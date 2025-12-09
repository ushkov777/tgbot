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

class MessageHandlerTest {
    
    @Mock
    private WeatherAPI weatherAPI;
    
    @Mock
    private org.telegram.telegrambots.bots.TelegramLongPollingBot bot;
    
    private MessageHandler messageHandler;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        messageHandler = new MessageHandler(weatherAPI);
    }
    
    @Test
    void testHandleMessage_Greeting() throws TelegramApiException {
        messageHandler.handleMessage("Привет", 12345L, "TestUser", bot);
        
        verify(bot, times(1)).execute(any(SendMessage.class));
    }
    
    @Test
    void testHandleMessage_Weather() throws Exception {
        WeatherData testWeather = new WeatherData(
            "Moscow", "Russia", 20.0, 19.0, 65, 760.0,
            "Clear", 10.0, "N", "2023-10-01 12:00"
        );
        
        when(weatherAPI.getWeather(anyString())).thenReturn(testWeather);
        
        messageHandler.handleMessage("погода Москва", 12345L, "TestUser", bot);
        
        verify(weatherAPI, times(1)).getWeather("Москва");
        verify(bot, times(2)).execute(any(SendMessage.class));
    }
    
    @Test
    void testHandleMessage_WeatherWithoutCity() throws TelegramApiException {
        messageHandler.handleMessage("погода", 12345L, "TestUser", bot);
        
        verify(bot, times(1)).execute(any(SendMessage.class));
    }
    
    @Test
    void testHandleMessage_CityLikeMessage() throws TelegramApiException {
        messageHandler.handleMessage("Москва", 12345L, "TestUser", bot);
        
        verify(bot, times(1)).execute(any(SendMessage.class));
    }
    
    @Test
    void testHandleMessage_Cache() throws TelegramApiException {
        messageHandler.handleMessage("кэш", 12345L, "TestUser", bot);
        
        verify(bot, times(1)).execute(any(SendMessage.class));
    }
    
    @Test
    void testHandleMessage_Location() throws TelegramApiException {
        messageHandler.handleMessage("геолокация", 12345L, "TestUser", bot);
        
        verify(bot, times(1)).execute(any(SendMessage.class));
    }
    
    @Test
    void testHandleMessage_Inline() throws TelegramApiException {
        messageHandler.handleMessage("инлайн", 12345L, "TestUser", bot);
        
        verify(bot, times(1)).execute(any(SendMessage.class));
    }
    
    @Test
    void testHandleMessage_OtherText() throws TelegramApiException {
        messageHandler.handleMessage("какая-то фраза", 12345L, "TestUser", bot);
        
        verify(bot, times(1)).execute(any(SendMessage.class));
    }
    
    @Test
    void testIsPotentialCityName() throws Exception {
        var method = MessageHandler.class.getDeclaredMethod("isPotentialCityName", String.class);
        method.setAccessible(true);
        
        // Потенциальные названия городов
        assertTrue((Boolean) method.invoke(messageHandler, "Москва"));
        assertTrue((Boolean) method.invoke(messageHandler, "London"));
        assertTrue((Boolean) method.invoke(messageHandler, "New-York"));
        
        // Не названия городов
        assertFalse((Boolean) method.invoke(messageHandler, ""));
        assertFalse((Boolean) method.invoke(messageHandler, "   "));
        assertFalse((Boolean) method.invoke(messageHandler, "оченьдлинноеназваниегородакотороенепомещается"));
        assertFalse((Boolean) method.invoke(messageHandler, "город123"));
        assertFalse((Boolean) method.invoke(messageHandler, "привет как дела"));
        assertFalse((Boolean) method.invoke(messageHandler, "сегодня"));
        assertFalse((Boolean) method.invoke(messageHandler, "что"));
    }
    
    @Test
    void testHandleWeatherInMessage_NoCityFound() throws Exception {
        messageHandler.handleMessage("какая погода", 12345L, "TestUser", bot);
        
        verify(bot, times(1)).execute(any(SendMessage.class));
    }
    
    @Test
    void testHandleWeatherInMessage_APIException() throws Exception {
        when(weatherAPI.getWeather(anyString()))
            .thenThrow(new Exception("City not found"));
        
        messageHandler.handleMessage("погода InvalidCity", 12345L, "TestUser", bot);
        
        verify(bot, times(1)).execute(any(SendMessage.class));
    }
    
    @Test
    void testHandleCityLikeMessage() throws Exception {
        var method = MessageHandler.class.getDeclaredMethod("handleCityLikeMessage", 
            String.class, Long.class, String.class, 
            org.telegram.telegrambots.bots.TelegramLongPollingBot.class);
        method.setAccessible(true);
        
        method.invoke(messageHandler, "Москва", 12345L, "TestUser", bot);
        
        verify(bot, times(1)).execute(any(SendMessage.class));
    }
}