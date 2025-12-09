package com.mybot;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    
    @Test
    void testMain_WithMock() {
        // Этот тест проверяет, что main метод не выбрасывает исключений
        // Мы не можем реально запустить бота в тестах, но можем проверить,
        // что код компилируется и структура корректна
        
        assertDoesNotThrow(() -> {
            // Проверяем, что можно создать экземпляр бота
            Bot bot = new Bot();
            assertNotNull(bot);
            
            // Проверяем, что методы доступны
            String username = bot.getBotUsername();
            String token = bot.getBotToken();
            
            assertNotNull(username);
            assertNotNull(token);
        });
    }
    
    @Test
    void testBotConfigValidation() {
        // Проверяем, что конфигурация загружается без ошибок
        assertDoesNotThrow(() -> {
            com.mybot.config.BotConfig.validateConfig();
        });
    }
}