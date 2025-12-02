package com.mybot.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*; // Добавлен этот импорт

class CommandHandlerTest {

    private CommandHandler commandHandler;
    
    @Mock
    private TelegramLongPollingBot botMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commandHandler = new CommandHandler();
    }

    @Test
    void testStartCommand() throws TelegramApiException {
        commandHandler.handleCommand("/start", 123456789L, "TestUser", botMock);
        verify(botMock).execute(any(SendMessage.class));
    }

    @Test
    void testHelpCommand() throws TelegramApiException {
        commandHandler.handleCommand("/help", 123456789L, "TestUser", botMock);
        verify(botMock).execute(any(SendMessage.class));
    }

    @Test
    void testAboutCommand() throws TelegramApiException {
        commandHandler.handleCommand("/about", 123456789L, "TestUser", botMock);
        verify(botMock).execute(any(SendMessage.class));
    }

    @Test
    void testAuthorsCommand() throws TelegramApiException {
        commandHandler.handleCommand("/authors", 123456789L, "TestUser", botMock);
        verify(botMock).execute(any(SendMessage.class));
    }

    @Test
    void testUnknownCommand() throws TelegramApiException {
        commandHandler.handleCommand("/unknown", 123456789L, "TestUser", botMock);
        verify(botMock).execute(any(SendMessage.class));
    }

    @Test
    void testHelpWithWeather() throws TelegramApiException {
        commandHandler.handleCommand("/help weather", 123456789L, "TestUser", botMock);
        verify(botMock).execute(any(SendMessage.class));
    }

    @Test
    void testWeatherCommandWithoutCity() throws TelegramApiException {
        commandHandler.handleCommand("/weather", 123456789L, "TestUser", botMock);
        verify(botMock).execute(any(SendMessage.class));
    }

    @Test
    void testWeatherCommandWithEmptyCity() throws TelegramApiException {
        commandHandler.handleCommand("/weather ", 123456789L, "TestUser", botMock);
        verify(botMock).execute(any(SendMessage.class));
    }

    @Test
    void testTelegramApiException() throws TelegramApiException {
        doThrow(new TelegramApiException("API error")).when(botMock).execute(any(SendMessage.class));
        
        
        assertDoesNotThrow(() -> 
            commandHandler.handleCommand("/start", 123456789L, "TestUser", botMock)
        );
    }
}