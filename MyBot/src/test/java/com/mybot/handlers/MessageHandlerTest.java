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
        // Подготовка
        Long chatId = 123456789L;
        String userName = "TestUser";

        // Выполнение
        messageHandler.handleMessage(message, chatId, userName, botMock);

        // Проверка
        verify(botMock).execute(any(SendMessage.class));
    }

    @Test
    void testRegularMessage() throws TelegramApiException {
        // Подготовка
        String message = "Сегодня хорошая погода";
        Long chatId = 123456789L;
        String userName = "TestUser";

        // Выполнение
        messageHandler.handleMessage(message, chatId, userName, botMock);

        // Проверка
        verify(botMock).execute(any(SendMessage.class));
    }

    @Test
    void testEmptyMessage() throws TelegramApiException {
        // Подготовка
        String message = "";
        Long chatId = 123456789L;
        String userName = "TestUser";

        // Выполнение
        messageHandler.handleMessage(message, chatId, userName, botMock);

        // Проверка
        verify(botMock).execute(any(SendMessage.class));
    }
}
