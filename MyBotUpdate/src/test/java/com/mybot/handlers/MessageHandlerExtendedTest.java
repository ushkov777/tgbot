package com.mybot.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.mybot.weather.WeatherAPI;
import com.mybot.weather.WeatherData;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MessageHandlerExtendedTest {

    private MessageHandler messageHandler;
    
    @Mock
    private TelegramLongPollingBot botMock;
    
    @Mock
    private WeatherAPI weatherAPIMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Создаем MessageHandler с переопределенным методом создания WeatherAPI
        messageHandler = new MessageHandler() {
            @Override
            protected WeatherAPI createWeatherAPI() {
                return weatherAPIMock;
            }
        };
    }

    @ParameterizedTest
    @DisplayName("Тест сообщений о погоде")
    @CsvSource({
        "погода Москва, Москва",
        "какая погода в Лондоне, Лондоне", 
        "погоду в Париже, Париже",
        "скажи погода Берлин, Берлин"
    })
    void testWeatherMessages(String message, String expectedCity) throws Exception {
        Long chatId = 123456789L;
        String userName = "TestUser";

        // Настраиваем мок
        when(weatherAPIMock.getWeather(expectedCity)).thenReturn(
            new WeatherData(expectedCity, "Country", 20.0, 19.0, 65, 760.0, 
                          "Sunny", 10.0, "N", "2023-10-01 12:00")
        );

        messageHandler.handleMessage(message, chatId, userName, botMock);

        verify(botMock, times(2)).execute(any(SendMessage.class));
    }

    @Test
    @DisplayName("Тест сообщения о погоде с ошибкой API")
    void testWeatherMessageWithAPIError() throws Exception {
        String message = "погода НесуществующийГород";
        Long chatId = 123456789L;
        String userName = "TestUser";

        // Настраиваем мок на выброс исключения 
        when(weatherAPIMock.getWeather("НесуществующийГород"))
            .thenThrow(new Exception("Город не найден"));

        messageHandler.handleMessage(message, chatId, userName, botMock);

        verify(botMock, times(2)).execute(any(SendMessage.class));
    }

    @ParameterizedTest
    @DisplayName("Тест потенциальных названий городов")
    @ValueSource(strings = {"Москва", "Лондон", "Париж", "Берлин", "Токио"})
    void testPotentialCityNames(String cityName) throws TelegramApiException {
        Long chatId = 123456789L;
        String userName = "TestUser";

        messageHandler.handleMessage(cityName, chatId, userName, botMock);

        verify(botMock).execute(any(SendMessage.class));
    }

    @ParameterizedTest
    @DisplayName("Тест НЕ потенциальных названий городов")
    @ValueSource(strings = {"", "   ", "какой", "сегодня", "test123", "оченьдлинноеназваниекотороеточнонегород"})
    void testNonPotentialCityNames(String text) throws TelegramApiException {
        Long chatId = 123456789L;
        String userName = "TestUser";

        messageHandler.handleMessage(text, chatId, userName, botMock);

        verify(botMock).execute(any(SendMessage.class));
    }

    @Test
    @DisplayName("Тест обработки исключения при отправке сообщения")
    void testTelegramApiException() throws TelegramApiException {
        String message = "привет";
        Long chatId = 123456789L;
        String userName = "TestUser";

        doThrow(new TelegramApiException("API error")).when(botMock).execute(any(SendMessage.class));

        assertDoesNotThrow(() -> 
            messageHandler.handleMessage(message, chatId, userName, botMock)
        );
    }
}