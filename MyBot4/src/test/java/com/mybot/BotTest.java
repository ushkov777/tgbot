package com.mybot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.mybot.handlers.CommandHandler;
import com.mybot.handlers.MessageHandler;
import com.mybot.weather.WeatherAPI;
import com.mybot.weather.WeatherData;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BotTest {
    
    @Mock
    private WeatherAPI weatherAPI;
    
    @Mock
    private CommandHandler commandHandler;
    
    @Mock
    private MessageHandler messageHandler;
    
    private Bot bot;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Создаем spy для бота, чтобы тестировать реальные методы, но мокать зависимости
        bot = Mockito.spy(new Bot());
        
        // Используем рефлексию для установки моков в приватные поля
        try {
            var weatherField = Bot.class.getDeclaredField("weatherAPI");
            weatherField.setAccessible(true);
            weatherField.set(bot, weatherAPI);
            
            var commandField = Bot.class.getDeclaredField("commandHandler");
            commandField.setAccessible(true);
            commandField.set(bot, commandHandler);
            
            var messageField = Bot.class.getDeclaredField("messageHandler");
            messageField.setAccessible(true);
            messageField.set(bot, messageHandler);
        } catch (Exception e) {
            fail("Failed to set private fields: " + e.getMessage());
        }
    }
    
    @Test
    void testGetBotUsername() {
        String username = bot.getBotUsername();
        assertNotNull(username);
    }
    
    @Test
    void testGetBotToken() {
        String token = bot.getBotToken();
        assertNotNull(token);
    }
    
    @Test
    void testOnUpdateReceived_WithTextMessage() {
        Update update = new Update();
        Message message = new Message();
        User user = new User();
        user.setFirstName("TestUser");
        Chat chat = new Chat();
        chat.setId(12345L);
        
        message.setFrom(user);
        message.setChat(chat);
        message.setText("/start");
        
        update.setMessage(message);
        
        // Мокаем выполнение
        doNothing().when(commandHandler).handleCommand(anyString(), anyLong(), anyString(), any());
        
        // Не должно быть исключений
        assertDoesNotThrow(() -> bot.onUpdateReceived(update));
    }
    
    @Test
    void testOnUpdateReceived_WithLocation() {
        Update update = new Update();
        Message message = new Message();
        User user = new User();
        user.setFirstName("TestUser");
        Chat chat = new Chat();
        chat.setId(12345L);
        
        Location location = new Location();
        location.setLatitude(55.7558);
        location.setLongitude(37.6173);
        
        message.setFrom(user);
        message.setChat(chat);
        message.setLocation(location);
        
        update.setMessage(message);
        
        // Мокаем выполнение - возвращаем null или заглушку для SendMessage
        try {
            when(bot.execute(any(SendMessage.class))).thenReturn(null);
        } catch (TelegramApiException e) {
            fail("Failed to mock execute: " + e.getMessage());
        }
        
        // Не должно быть исключений
        assertDoesNotThrow(() -> bot.onUpdateReceived(update));
    }
    
    @Test
    void testOnUpdateReceived_WithInlineQuery() {
        Update update = new Update();
        InlineQuery inlineQuery = new InlineQuery();
        User user = new User();
        user.setUserName("testuser");
        
        inlineQuery.setId("test_query_id");
        inlineQuery.setFrom(user);
        inlineQuery.setQuery("Moscow");
        
        update.setInlineQuery(inlineQuery);
        
        // Мокаем выполнение инлайн-запроса - возвращаем true (успешное выполнение)
        try {
            when(bot.execute(any(AnswerInlineQuery.class))).thenReturn(true);
        } catch (TelegramApiException e) {
            fail("Failed to mock execute: " + e.getMessage());
        }
        
        // Мокаем получение погоды
        try {
            when(weatherAPI.getWeather(anyString())).thenReturn(createTestWeatherData());
        } catch (Exception e) {
            fail("Failed to mock getWeather: " + e.getMessage());
        }
        
        // Не должно быть исключений
        assertDoesNotThrow(() -> bot.onUpdateReceived(update));
    }
    
    @Test
    void testOnUpdateReceived_WithEmptyInlineQuery() {
        Update update = new Update();
        InlineQuery inlineQuery = new InlineQuery();
        User user = new User();
        user.setUserName("testuser");
        
        inlineQuery.setId("test_query_id");
        inlineQuery.setFrom(user);
        inlineQuery.setQuery("");
        
        update.setInlineQuery(inlineQuery);
        
        // Мокаем выполнение - возвращаем true
        try {
            when(bot.execute(any(AnswerInlineQuery.class))).thenReturn(true);
        } catch (TelegramApiException e) {
            fail("Failed to mock execute: " + e.getMessage());
        }
        
        // Не должно быть исключений
        assertDoesNotThrow(() -> bot.onUpdateReceived(update));
    }
    
    @Test
    void testOnUpdateReceived_WithException() {
        Update update = new Update();
        Message message = new Message();
        User user = new User();
        Chat chat = new Chat();
        chat.setId(12345L);
        
        message.setFrom(user);
        message.setChat(chat);
        message.setText("/start");
        
        update.setMessage(message);
        
        // Создаем исключение при обработке
        doThrow(new RuntimeException("Test exception"))
            .when(commandHandler).handleCommand(anyString(), anyLong(), anyString(), any());
        
        // Не должно быть исключений (должно быть обработано внутри)
        assertDoesNotThrow(() -> bot.onUpdateReceived(update));
    }
    
    @Test
    void testHandleInlineQuery_EmptyQuery() {
        InlineQuery inlineQuery = new InlineQuery();
        User user = new User();
        user.setUserName("testuser");
        
        inlineQuery.setId("test_query_id");
        inlineQuery.setFrom(user);
        inlineQuery.setQuery("");
        
        // Мокаем выполнение - возвращаем true
        try {
            when(bot.execute(any(AnswerInlineQuery.class))).thenReturn(true);
        } catch (TelegramApiException e) {
            fail("Failed to mock execute: " + e.getMessage());
        }
        
        // Вызываем приватный метод через рефлексию
        assertDoesNotThrow(() -> {
            var method = Bot.class.getDeclaredMethod("handleInlineQuery", InlineQuery.class);
            method.setAccessible(true);
            method.invoke(bot, inlineQuery);
        });
    }
    
    @Test
    void testHandleInlineQuery_WithWeatherData() {
        InlineQuery inlineQuery = new InlineQuery();
        User user = new User();
        user.setUserName("testuser");
        
        inlineQuery.setId("test_query_id");
        inlineQuery.setFrom(user);
        inlineQuery.setQuery("Moscow");
        
        // Мокаем выполнение - возвращаем true
        try {
            when(bot.execute(any(AnswerInlineQuery.class))).thenReturn(true);
        } catch (TelegramApiException e) {
            fail("Failed to mock execute: " + e.getMessage());
        }
        
        // Мокаем получение погоды
        try {
            when(weatherAPI.getWeather(anyString())).thenReturn(createTestWeatherData());
        } catch (Exception e) {
            fail("Failed to mock getWeather: " + e.getMessage());
        }
        
        // Вызываем приватный метод через рефлексию
        assertDoesNotThrow(() -> {
            var method = Bot.class.getDeclaredMethod("handleInlineQuery", InlineQuery.class);
            method.setAccessible(true);
            method.invoke(bot, inlineQuery);
        });
    }
    
    @Test
    void testHandleInlineQuery_WeatherAPIException() {
        InlineQuery inlineQuery = new InlineQuery();
        User user = new User();
        user.setUserName("testuser");
        
        inlineQuery.setId("test_query_id");
        inlineQuery.setFrom(user);
        inlineQuery.setQuery("InvalidCity");
        
        // Мокаем выполнение - возвращаем true
        try {
            when(bot.execute(any(AnswerInlineQuery.class))).thenReturn(true);
        } catch (TelegramApiException e) {
            fail("Failed to mock execute: " + e.getMessage());
        }
        
        // Мокаем исключение при получении погоды
        try {
            when(weatherAPI.getWeather(anyString()))
                .thenThrow(new Exception("City not found"));
        } catch (Exception e) {
            fail("Failed to mock getWeather: " + e.getMessage());
        }
        
        // Вызываем приватный метод через рефлексию
        assertDoesNotThrow(() -> {
            var method = Bot.class.getDeclaredMethod("handleInlineQuery", InlineQuery.class);
            method.setAccessible(true);
            method.invoke(bot, inlineQuery);
        });
    }
    
    @Test
    void testCreateWeatherInlineResult() throws Exception {
        WeatherData weatherData = createTestWeatherData();
        
        var method = Bot.class.getDeclaredMethod("createWeatherInlineResult", 
            String.class, WeatherData.class);
        method.setAccessible(true);
        
        var result = (org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle) 
            method.invoke(bot, "Moscow", weatherData);
        
        assertNotNull(result);
        assertNotNull(result.getTitle());
        assertTrue(result.getTitle().contains("Moscow"));
    }
    
    @Test
    void testSendInlineExamples() throws Exception {
        // Мокаем выполнение - возвращаем true
        try {
            when(bot.execute(any(AnswerInlineQuery.class))).thenReturn(true);
        } catch (TelegramApiException e) {
            fail("Failed to mock execute: " + e.getMessage());
        }
        
        var method = Bot.class.getDeclaredMethod("sendInlineExamples", String.class);
        method.setAccessible(true);
        
        assertDoesNotThrow(() -> method.invoke(bot, "test_query_id"));
    }
    
    @Test
    void testSendInlineError() throws Exception {
        // Мокаем выполнение - возвращаем true
        try {
            when(bot.execute(any(AnswerInlineQuery.class))).thenReturn(true);
        } catch (TelegramApiException e) {
            fail("Failed to mock execute: " + e.getMessage());
        }
        
        var method = Bot.class.getDeclaredMethod("sendInlineError", 
            String.class, String.class, String.class);
        method.setAccessible(true);
        
        assertDoesNotThrow(() -> 
            method.invoke(bot, "test_query_id", "Moscow", "Test error"));
    }
    
    private WeatherData createTestWeatherData() {
        return new WeatherData(
            "Moscow",
            "Russia",
            20.0,
            19.5,
            65,
            760.0,
            "Clear sky",
            10.0,
            "N",
            "2023-10-01 12:00"
        );
    }
}