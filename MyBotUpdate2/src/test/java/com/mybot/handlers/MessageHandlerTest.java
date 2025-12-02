package com.mybot.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MessageHandlerTest {

    private MessageHandler messageHandler;
    
    @Mock
    private TelegramLongPollingBot botMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        messageHandler = new MessageHandler();
    }

    @ParameterizedTest
    @ValueSource(strings = {"привет", "ПРИВЕТ", "Привет", "привет как дела"})
    void testGreetingMessages(String message) throws TelegramApiException {
        messageHandler.handleMessage(message, 123456789L, "TestUser", botMock);
        verify(botMock).execute(any(SendMessage.class));
    }

    @Test
    void testWeatherMessage() throws TelegramApiException {
        messageHandler.handleMessage("погода", 123456789L, "TestUser", botMock);
        verify(botMock).execute(any(SendMessage.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Москва", "Лондон", "Париж", "Берлин"})
    void testCityNames(String cityName) throws TelegramApiException {
        messageHandler.handleMessage(cityName, 123456789L, "TestUser", botMock);
        verify(botMock).execute(any(SendMessage.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "какой", "сегодня"})
    void testNonCityNames(String text) throws TelegramApiException {
        messageHandler.handleMessage(text, 123456789L, "TestUser", botMock);
        verify(botMock).execute(any(SendMessage.class));
    }

    @Test
    void testRegularMessage() throws TelegramApiException {
        messageHandler.handleMessage("сегодня хорошая погода", 123456789L, "TestUser", botMock);
        verify(botMock).execute(any(SendMessage.class));
    }

    
    @Test
    void testEmptyMessage() throws TelegramApiException {
        messageHandler.handleMessage("", 123456789L, "TestUser", botMock);
        verify(botMock).execute(any(SendMessage.class));
    }

    @Test
    void testTelegramApiException() throws TelegramApiException {
        doThrow(new TelegramApiException("API error")).when(botMock).execute(any(SendMessage.class));
        
        assertDoesNotThrow(() -> 
            messageHandler.handleMessage("привет", 123456789L, "TestUser", botMock)
        );
    }
}