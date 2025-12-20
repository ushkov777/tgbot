package com.mybot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.mybot.config.BotConfig;
import com.mybot.handlers.CommandHandler;
import com.mybot.handlers.MessageHandler;
import com.mybot.weather.WeatherAPI;
import com.mybot.weather.WeatherData;

import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    private final CommandHandler commandHandler;
    private final MessageHandler messageHandler;
    private final WeatherAPI weatherAPI;
    
    public Bot() {
        this.weatherAPI = new WeatherAPI();
        this.commandHandler = new CommandHandler(this.weatherAPI);
        this.messageHandler = new MessageHandler(this.weatherAPI);
    }
    
    @Override
    public String getBotUsername() {
        return BotConfig.BOT_USERNAME;
    }
    
    @Override
    public String getBotToken() {
        return BotConfig.BOT_TOKEN;
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        try {
            // 1. Обработка инлайн-запросов
            if (update.hasInlineQuery()) {
                handleInlineQuery(update.getInlineQuery());
                return;
            }
            
            // 2. Обработка сообщений с текстом
            if (update.hasMessage() && update.getMessage().hasText()) {
                handleTextMessage(update);
            } 
            // 3. Обработка геолокации
            else if (update.hasMessage() && update.getMessage().hasLocation()) {
                handleLocationMessage(update);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // УПРОЩЕННЫЙ МЕТОД: Обработка инлайн-запросов
    private void handleInlineQuery(InlineQuery inlineQuery) {
        String query = inlineQuery.getQuery();
        String queryId = inlineQuery.getId();
        
        System.out.println("🔍 Получен инлайн-запрос: '" + query + "' от @" + 
                          inlineQuery.getFrom().getUserName());
        
        // Если запрос пустой, показываем примеры
        if (query == null || query.trim().isEmpty()) {
            try {
                sendInlineExamples(queryId);
            } catch (TelegramApiException e) {
                System.err.println("Ошибка при отправке примеров: " + e.getMessage());
            }
            return;
        }
        
        String city = query.trim();
        
        try {
            // СНАЧАЛА ПРОВЕРЯЕМ КЭШ
            WeatherData cachedWeather = weatherAPI.getCachedWeather(city);
            if (cachedWeather != null) {
                System.out.println("⚡ Используем кэшированные данные для инлайн: " + city);
                
                // Формируем результат из кэша
                InlineQueryResultArticle article = createWeatherInlineResult(city, cachedWeather);
                List<InlineQueryResult> results = new ArrayList<>();
                results.add(article);
                
                // Отправляем результат
                AnswerInlineQuery answer = new AnswerInlineQuery();
                answer.setInlineQueryId(queryId);
                answer.setResults(results);
                answer.setCacheTime(300); // Кэшируем на 5 минут в Telegram
                
                execute(answer);
                System.out.println("✅ Инлайн-ответ из кэша отправлен для: " + city);
                return;
            }
            
            // Если нет в кэше, отправляем пустой результат и делаем запрос
            List<InlineQueryResult> results = new ArrayList<>();
            
            AnswerInlineQuery quickAnswer = new AnswerInlineQuery();
            quickAnswer.setInlineQueryId(queryId);
            quickAnswer.setResults(results);
            quickAnswer.setCacheTime(1);
            
            execute(quickAnswer);
            
            // Теперь в фоне получаем погоду
            new Thread(() -> {
                try {
                    // Получаем погоду (метод getWeather сам сохранит в кэш)
                    WeatherData weatherData = weatherAPI.getWeather(city);
                    
                    // Формируем результат
                    InlineQueryResultArticle article = createWeatherInlineResult(city, weatherData);
                    List<InlineQueryResult> finalResults = new ArrayList<>();
                    finalResults.add(article);
                    
                    // Отправляем обновленный результат
                    AnswerInlineQuery finalAnswer = new AnswerInlineQuery();
                    finalAnswer.setInlineQueryId(queryId);
                    finalAnswer.setResults(finalResults);
                    finalAnswer.setCacheTime(300); // Кэшируем на 5 минут в Telegram
                    
                    execute(finalAnswer);
                    System.out.println("✅ Инлайн-ответ отправлен для: " + city);
                    
                } catch (Exception e) {
                    System.err.println("❌ Ошибка получения погоды для инлайн: " + e.getMessage());
                    try {
                        sendInlineError(queryId, city, e.getMessage());
                    } catch (TelegramApiException ex) {
                        System.err.println("Не удалось отправить ошибку: " + ex.getMessage());
                    }
                }
            }).start();
            
        } catch (Exception e) {
            System.err.println("❌ Критическая ошибка инлайн: " + e.getMessage());
        }
    }
    
    // ПРОСТОЙ метод для создания результата с погодой
    private InlineQueryResultArticle createWeatherInlineResult(String city, WeatherData weatherData) {
        // Простой текст без Markdown
        String messageText = String.format(
            "🌤 Погода в %s, %s\n\n" +
            "🌡 Температура: %.1f°C\n" +
            "🤔 Ощущается как: %.1f°C\n" +
            "💧 Влажность: %d%%\n" +
            "📊 Давление: %.0f мм рт.ст.\n" +
            "💨 Ветер: %.1f км/ч, %s\n" +
            "📝 %s\n\n" +
            "🕐 Данные на: %s\n\n" +
            "🤖 @%s",
            weatherData.getCity(), weatherData.getCountry(),
            weatherData.getTemperature(), weatherData.getFeelsLike(),
            weatherData.getHumidity(), weatherData.getPressure(),
            weatherData.getWindSpeed(), weatherData.getWindDirection(),
            weatherData.getDescription(),
            weatherData.getObservationTime(),
            BotConfig.BOT_USERNAME
        );
        
        // Простой текст, без форматирования
        InputTextMessageContent messageContent = new InputTextMessageContent();
        messageContent.setMessageText(messageText);
        // Не используем parseMode - простой текст
        
        InlineQueryResultArticle article = new InlineQueryResultArticle();
        article.setId("weather_" + city.toLowerCase().hashCode());
        article.setTitle("Погода в " + city);
        article.setDescription(String.format("%.1f°C, %s", 
            weatherData.getTemperature(), 
            weatherData.getDescription()));
        article.setInputMessageContent(messageContent);
        
        return article;
    }
    
    // Простой метод для примеров
    private void sendInlineExamples(String queryId) throws TelegramApiException {
        List<InlineQueryResult> results = new ArrayList<>();
        
        results.add(createExampleResult("Москва", "1"));
        results.add(createExampleResult("London", "2"));
        results.add(createExampleResult("Tokyo", "3"));
        
        AnswerInlineQuery answer = new AnswerInlineQuery();
        answer.setInlineQueryId(queryId);
        answer.setResults(results);
        answer.setCacheTime(3600);
        
        execute(answer);
    }
    
    // Простой пример
    private InlineQueryResultArticle createExampleResult(String city, String id) {
        String messageText = String.format(
            "✨ Пример: погода в %s\n\n" +
            "Наберите: @%s %s\n\n" +
            "Или команда: /weather %s",
            city, BotConfig.BOT_USERNAME, city, city
        );
        
        InputTextMessageContent messageContent = new InputTextMessageContent();
        messageContent.setMessageText(messageText);
        
        InlineQueryResultArticle article = new InlineQueryResultArticle();
        article.setId("example_" + id);
        article.setTitle("Пример: " + city);
        article.setDescription("Нажмите, чтобы увидеть пример");
        article.setInputMessageContent(messageContent);
        
        return article;
    }
    
    // Простая ошибка
    private void sendInlineError(String queryId, String city, String error) throws TelegramApiException {
        String messageText = String.format(
            "❌ Не удалось получить погоду для %s\n\n" +
            "Причина: %s\n\n" +
            "Попробуйте команду: /weather %s\n\n" +
            "🤖 @%s",
            city, error, city, BotConfig.BOT_USERNAME
        );
        
        InputTextMessageContent messageContent = new InputTextMessageContent();
        messageContent.setMessageText(messageText);
        
        InlineQueryResultArticle article = new InlineQueryResultArticle();
        article.setId("error_" + System.currentTimeMillis());
        article.setTitle("❌ Ошибка для " + city);
        article.setDescription("Город не найден");
        article.setInputMessageContent(messageContent);
        
        AnswerInlineQuery answer = new AnswerInlineQuery();
        answer.setInlineQueryId(queryId);
        answer.setResults(List.of(article));
        answer.setCacheTime(1);
        
        execute(answer);
    }
    
    // Существующие методы без изменений
    private void handleTextMessage(Update update) {
        String messageText = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        String userName = update.getMessage().getFrom().getFirstName();
        
        if (messageText.startsWith("/")) {
            commandHandler.handleCommand(messageText, chatId, userName, this);
        } else {
            messageHandler.handleMessage(messageText, chatId, userName, this);
        }
    }
    
    private void handleLocationMessage(Update update) {
        Location location = update.getMessage().getLocation();
        Long chatId = update.getMessage().getChatId();
        String userName = update.getMessage().getFrom().getFirstName();
        
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            
            // Показываем сообщение о загрузке
            message.setText("📍 Получил вашу геолокацию...\nОпределяю погоду в этой точке...");
            execute(message);
            
            // Создаем сервис для работы с геолокацией
            com.mybot.location.LocationService locationService = new com.mybot.location.LocationService();
            locationService.setWeatherAPI(weatherAPI);
            
            // Получаем погоду по координатам
            String response = locationService.processLocation(
                location.getLatitude(), 
                location.getLongitude()
            );
            
            // Отправляем результат
            SendMessage weatherMessage = new SendMessage();
            weatherMessage.setChatId(chatId.toString());
            weatherMessage.setText(response);
            weatherMessage.setParseMode("Markdown");
            execute(weatherMessage);
            
        } catch (Exception e) {
            try {
                SendMessage errorMessage = new SendMessage();
                errorMessage.setChatId(chatId.toString());
                errorMessage.setText(
                    "❌ Не удалось получить погоду по вашей геолокации.\n" +
                    "Причина: " + e.getMessage() + "\n\n" +
                    "Попробуйте отправить название города командой:\n" +
                    "`/weather Москва`"
                );
                errorMessage.setParseMode("Markdown");
                execute(errorMessage);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        }
    }
}