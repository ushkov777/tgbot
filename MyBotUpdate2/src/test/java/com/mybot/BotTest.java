package com.mybot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import com.mybot.config.BotConfig;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

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
    }

    @Test
    void testCommandMessage() {
        when(updateMock.hasMessage()).thenReturn(true);
        when(updateMock.getMessage()).thenReturn(messageMock);
        when(messageMock.hasText()).thenReturn(true);
        when(messageMock.getText()).thenReturn("/start");
        when(messageMock.getChatId()).thenReturn(123456789L);
        when(messageMock.getFrom()).thenReturn(userMock);
        when(userMock.getFirstName()).thenReturn("TestUser");

        assertDoesNotThrow(() -> bot.onUpdateReceived(updateMock));
    }

    @Test
    void testTextMessage() {
        when(updateMock.hasMessage()).thenReturn(true);
        when(updateMock.getMessage()).thenReturn(messageMock);
        when(messageMock.hasText()).thenReturn(true);
        when(messageMock.getText()).thenReturn("Привет");
        when(messageMock.getChatId()).thenReturn(123456789L);
        when(messageMock.getFrom()).thenReturn(userMock);
        when(userMock.getFirstName()).thenReturn("TestUser");

        assertDoesNotThrow(() -> bot.onUpdateReceived(updateMock));
    }

    @Test
    void testMessageWithoutText() {
        when(updateMock.hasMessage()).thenReturn(true);
        when(updateMock.getMessage()).thenReturn(messageMock);
        when(messageMock.hasText()).thenReturn(false);

        assertDoesNotThrow(() -> bot.onUpdateReceived(updateMock));
    }

    @Test
    void testUpdateWithoutMessage() {
        when(updateMock.hasMessage()).thenReturn(false);

        assertDoesNotThrow(() -> bot.onUpdateReceived(updateMock));
    }

    @Test
    void testGetBotUsername() {
        assertEquals(BotConfig.BOT_USERNAME, bot.getBotUsername());
    }

    @Test
    void testGetBotToken() {
        assertEquals(BotConfig.BOT_TOKEN, bot.getBotToken());
    }
}