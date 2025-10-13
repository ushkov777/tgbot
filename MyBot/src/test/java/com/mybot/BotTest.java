package com.mybot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.mockito.Mockito.*;

class BotTest {

    private Bot bot;
    
    @Mock
    private Update updateMock;
    
    @Mock
    private Message messageMock;
    
    @Mock
    private User userMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bot = new Bot();
        
        // Настраиваем моки для стандартного сценария
        when(updateMock.hasMessage()).thenReturn(true);
        when(updateMock.getMessage()).thenReturn(messageMock);
        when(messageMock.hasText()).thenReturn(true);
        when(messageMock.getFrom()).thenReturn(userMock);
        when(userMock.getFirstName()).thenReturn("TestUser");
    }

    @Test
    void testCommandMessage() {
        // Подготовка
        when(messageMock.getText()).thenReturn("/start");
        when(messageMock.getChatId()).thenReturn(123456789L);

        // Выполнение
        bot.onUpdateReceived(updateMock);

        // Проверка - не падает с исключением
    }

    @Test
    void testTextMessage() {
        // Подготовка
        when(messageMock.getText()).thenReturn("Привет, бот!");
        when(messageMock.getChatId()).thenReturn(123456789L);

        // Выполнение
        bot.onUpdateReceived(updateMock);

        // Проверка - не падает с исключением
    }

    @Test
    void testMessageWithoutText() {
        // Подготовка
        when(messageMock.hasText()).thenReturn(false);

        // Выполнение
        bot.onUpdateReceived(updateMock);

        // Проверка - не должно быть исключений
    }
}
