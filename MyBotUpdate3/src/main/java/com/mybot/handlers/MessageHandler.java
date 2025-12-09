package com.mybot.handlers;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.mybot.weather.WeatherAPI;
import com.mybot.weather.WeatherData;

public class MessageHandler {
    private final WeatherAPI weatherAPI;
    
    // ИЗМЕНЕННЫЙ КОНСТРУКТОР
    public MessageHandler(WeatherAPI weatherAPI) {
        this.weatherAPI = weatherAPI;
    }
    
    public void handleMessage(String text, Long chatId, String userName, TelegramLongPollingBot bot) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        
        if (text.toLowerCase().contains("привет")) {
            message.setText("Привет, " + userName + "! 😊\n" +
                           "Хочешь узнать погоду?\n" +
                           "📌 Напиши название города\n" +
                           "📍 Или отправь геолокацию (скрепка 📎 → Location)\n\n" +
                           "💡 *Новое:* Инлайн-режим! Наберите `@" + com.mybot.config.BotConfig.BOT_USERNAME + " Москва` в любом чате!");
            message.setParseMode("Markdown");
        } else if (text.toLowerCase().contains("погода")) {
            handleWeatherInMessage(text, chatId, userName, bot);
            return;
        } else if (isPotentialCityName(text)) {
            handleCityLikeMessage(text, chatId, userName, bot);
            return;
        } else if (text.toLowerCase().contains("кэш") || text.toLowerCase().contains("cache")) {
            message.setText("ℹ️ *Информация о кэшировании:*\n\n" +
                           "Данные о погоде хранятся 10 минут.\n" +
                           "При повторных запросах используются кэшированные данные.\n\n" +
                           "📊 Статистика: /cachestats\n" +
                           "🧹 Очистка: /clearcache");
            message.setParseMode("Markdown");
        } else if (text.toLowerCase().contains("геолокация") || text.toLowerCase().contains("локация")) {
            message.setText("📍 *Как отправить геолокацию:*\n\n" +
                           "1. Нажмите на скрепку 📎\n" +
                           "2. Выберите \"Location\"\n" +
                           "3. Отправьте свою геолокацию\n\n" +
                           "Или используйте команду:\n" +
                           "`/weather 55.7558,37.6173`");
            message.setParseMode("Markdown");
        } else if (text.toLowerCase().contains("инлайн") || text.toLowerCase().contains("inline")) {
            message.setText("💬 *Инлайн-режим:*\n\n" +
                           "Чтобы использовать бота в любом чате:\n" +
                           "1. Начните вводить `@" + com.mybot.config.BotConfig.BOT_USERNAME + "`\n" +
                           "2. Добавьте город (например, Москва)\n" +
                           "3. Выберите результат\n\n" +
                           "*Пример:*\n" +
                           "`@" + com.mybot.config.BotConfig.BOT_USERNAME + " Москва`");
            message.setParseMode("Markdown");
        } else {
            message.setText("Ты сказал: " + text + "\n\n" +
                           "Используй /help для списка команд\n" +
                           "Или отправь геолокацию для погоды вокруг!\n\n" +
                           "💡 Попробуй инлайн-режим: набери `@" + com.mybot.config.BotConfig.BOT_USERNAME + " Москва` в любом чате!");
            message.setParseMode("Markdown");
        }
        
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    
    private void handleWeatherInMessage(String text, Long chatId, String userName, TelegramLongPollingBot bot) {
        String[] words = text.split(" ");
        String city = null;
        
        // Ищем название города после слова "погода"
        for (int i = 0; i < words.length - 1; i++) {
            if (words[i].toLowerCase().equals("погода") || 
                words[i].toLowerCase().equals("погоду") ||
                words[i].toLowerCase().equals("погоде")) {
                city = words[i + 1];
                break;
            }
        }
        
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        
        if (city != null && !city.isEmpty()) {
            try {
                message.setText("⏳ Запрашиваю погоду для " + city + "...");
                bot.execute(message);
                
                WeatherData weather = weatherAPI.getWeather(city);
                
                SendMessage weatherMessage = new SendMessage();
                weatherMessage.setChatId(chatId.toString());
                weatherMessage.setText(weather.toString());
                
                bot.execute(weatherMessage);
                return;
                
            } catch (Exception e) {
                message.setText("❌ Не удалось получить погоду для " + city + 
                              "\nПопробуйте использовать команду: /weather " + city);
            }
        } else {
            message.setText("Чтобы узнать погоду, напиши:\n\"погода Москва\"\nили используй команду:\n/weather Москва");
        }
        
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    
    private void handleCityLikeMessage(String text, Long chatId, String userName, TelegramLongPollingBot bot) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        
        message.setText("Хочешь узнать погоду в " + text + "?\n" +
                       "Используй команду: /weather " + text + "\n\n" +
                       "Или просто напиши \"погода " + text + "\"\n\n" +
                       "💡 *Инлайн-режим:* набери `@" + com.mybot.config.BotConfig.BOT_USERNAME + " " + text + "` в любом чате!");
        message.setParseMode("Markdown");
        
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    
    private boolean isPotentialCityName(String text) {
        if (text == null || text.trim().isEmpty()) return false;
        
        String cleanText = text.trim();
        
        // Проверяем, что текст не слишком длинный для города
        if (cleanText.length() > 30 || cleanText.contains("\n")) {
            return false;
        }
        
        // Список общих слов, которые редко бывают названиями городов
        String[] commonWords = {"какой", "какая", "какое", "какие", "что", "где", 
                               "когда", "как", "почему", "сегодня", "завтра"};
        for (String word : commonWords) {
            if (cleanText.equalsIgnoreCase(word)) {
                return false;
            }
        }
        
        // Если текст состоит из одного слова и не содержит цифр
        if (!cleanText.contains(" ") && !cleanText.matches(".*\\d.*")) {
            return true;
        }
        
        return false;
    }
}