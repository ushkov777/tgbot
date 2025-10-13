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
        // Подготовка
        String command = "/start";
        Long chatId = 123456789L;
        String userName = "TestUser";

        // Выполнение
        commandHandler.handleCommand(command, chatId, userName, botMock);

        // Проверка
        verify(botMock).execute(any(SendMessage.class));
    }

    @Test
    void testHelpCommand() throws TelegramApiException {
        // Подготовка
        String command = "/help";
        Long chatId = 123456789L;
        String userName = "TestUser";

        // Выполнение
        commandHandler.handleCommand(command, chatId, userName, botMock);

        // Проверка
        verify(botMock).execute(any(SendMessage.class));
    }

    @Test
    void testUnknownCommand() throws TelegramApiException {
        // Подготовка
        String command = "/unknown";
        Long chatId = 123456789L;
        String userName = "TestUser";

        // Выполнение
        commandHandler.handleCommand(command, chatId, userName, botMock);

        // Проверка
        verify(botMock).execute(any(SendMessage.class));
    }
}
