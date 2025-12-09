package com.mybot.handlers;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.mybot.weather.WeatherAPI;
import com.mybot.weather.WeatherData;
import com.mybot.location.LocationService;

public class CommandHandler {
    private final WeatherAPI weatherAPI;
    private final LocationService locationService;
    
    // ИЗМЕНЕННЫЙ КОНСТРУКТОР
    public CommandHandler(WeatherAPI weatherAPI) {
        this.weatherAPI = weatherAPI;
        this.locationService = new LocationService();
        this.locationService.setWeatherAPI(weatherAPI);
    }
    
    // Геттер для WeatherAPI (нужен в классе Bot)
    public WeatherAPI getWeatherAPI() {
        return weatherAPI;
    }
    
    public void handleCommand(String command, Long chatId, String userName, TelegramLongPollingBot bot) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        
        String baseCommand = command.split(" ")[0];
        
        switch (baseCommand) {
            case "/start":
                message.setText("Привет, " + userName + "! 👋\nЯ погодный бот с кэшированием!\n\n" +
                               "✨ *Новые возможности:*\n" +
                               "• Инлайн-режим: наберите `@" + com.mybot.config.BotConfig.BOT_USERNAME + " Москва` в любом чате!\n" +
                               "• Отправьте геолокацию 📍 - узнайте погоду вокруг\n" +
                               "• Частые запросы кэшируются ⚡\n" +
                               "• Команда /cachestats - статистика кэша\n\n" +
                               "📋 *Основные команды:*\n" +
                               "/weather [город] - погода\n" +
                               "/help - помощь\n" +
                               "/about - о боте");
                message.setParseMode("Markdown");
                break;
                
            case "/help":
                if (command.contains(" ")) {
                    String helpArg = command.substring(6).trim();
                    String helpText = getHelpText(helpArg);
                    message.setText(helpText);
                } else {
                    message.setText("🌤 *Помощь по командам:*\n\n" +
                                   "*✨ ИНЛАЙН-РЕЖИМ:*\n" +
                                   "Наберите `@" + com.mybot.config.BotConfig.BOT_USERNAME + " Москва` в любом чате!\n\n" +
                                   "*📍 ГЕОЛОКАЦИЯ:*\n" +
                                   "Просто отправьте свою геолокацию (скрепка 📎 → Location)\n\n" +
                                   "*⚡ КЭШИРОВАНИЕ:*\n" +
                                   "/cachestats - статистика кэша\n" +
                                   "/clearcache - очистить кэш\n\n" +
                                   "*📋 ОСНОВНЫЕ КОМАНДЫ:*\n" +
                                   "/start - начать работу\n" +
                                   "/weather [город] - погода\n" +
                                   "/about - информация\n" +
                                   "/authors - авторы\n\n" +
                                   "*Примеры:*\n" +
                                   "`/weather Москва`\n" +
                                   "`/help weather`");
                    message.setParseMode("Markdown");
                }
                break;
                
            case "/cachestats":
                message.setText(weatherAPI.getCacheStats());
                break;
                
            case "/clearcache":
                weatherAPI.clearCache();
                message.setText("🧹 Кэш успешно очищен!");
                break;
                
            case "/location":
                message.setText("📍 Чтобы получить погоду по геолокации:\n\n" +
                               "1. Нажмите на скрепку 📎 в поле ввода\n" +
                               "2. Выберите \"Location\"\n" +
                               "3. Отправьте свою геолокацию\n\n" +
                               "Или используйте команду:\n" +
                               "`/weather 55.7558,37.6173`");
                message.setParseMode("Markdown");
                break;
                
            case "/weather":
                // Проверяем, если это координаты (например, /weather 55.7558,37.6173)
                String[] parts = command.split(" ", 2);
                if (parts.length >= 2) {
                    String query = parts[1].trim();
                    
                    // Проверяем, является ли запрос координатами
                    if (query.matches("-?\\d+\\.\\d+,-?\\d+\\.\\d+")) {
                        handleWeatherByCoordinates(query, chatId, userName, bot);
                        return;
                    } else {
                        // Обычный запрос по городу
                        handleWeatherCommand(command, chatId, userName, bot);
                        return;
                    }
                } else {
                    message.setText("Пожалуйста, укажите город или координаты.\n*Примеры:*\n" +
                                   "`/weather Москва`\n" +
                                   "`/weather 55.7558,37.6173`");
                    message.setParseMode("Markdown");
                }
                break;
                
            case "/about":
                message.setText("🤖 *Погодный Бот v3.0*\n\n" +
                               "*✨ Новые возможности:*\n" +
                               "• 💬 Инлайн-режим (работает в любом чате!)\n" +
                               "• ⚡ Интеллектуальное кэширование\n" +
                               "• 📍 Погода по геолокации\n" +
                               "• 🔄 Автоматические повторные попытки\n" +
                               "• 📊 Подробная статистика\n\n" +
                               "*🔧 Технологии:*\n" +
                               "• Weatherstack API\n" +
                               "• OpenStreetMap для геокодирования\n" +
                               "• In-Memory кэш\n" +
                               "• Многопоточная обработка\n\n" +
                               "*💡 Инлайн-режим:*\n" +
                               "Просто наберите `@" + com.mybot.config.BotConfig.BOT_USERNAME + " Москва` в любом чате!");
                message.setParseMode("Markdown");
                break;
            
            case "/authors":
                message.setText("👨‍💻 *Авторы проекта v3.0:*\n\n" +
                               "• Ушков Роман\n" +
                               "• Крылосов Даниил\n\n" +
                               "Теперь с инлайн-режимом и геолокацией!");
                message.setParseMode("Markdown");
                break;
                
            case "/inline":
                message.setText("💡 *Как использовать инлайн-режим:*\n\n" +
                               "1. Откройте любой чат в Telegram\n" +
                               "2. Начните вводить `@" + com.mybot.config.BotConfig.BOT_USERNAME + "`\n" +
                               "3. Добавьте название города\n" +
                               "4. Выберите результат из списка\n\n" +
                               "*Пример:*\n" +
                               "`@" + com.mybot.config.BotConfig.BOT_USERNAME + " Москва`\n\n" +
                               "🤖 Бот отправит погоду прямо в этот чат!");
                message.setParseMode("Markdown");
                break;
                
            default:
                message.setText("Неизвестная команда 🤔\nИспользуйте /help для списка команд");
                break;
        }
        
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    
    private void handleWeatherByCoordinates(String coordinates, Long chatId, String userName, TelegramLongPollingBot bot) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        
        try {
            String[] coords = coordinates.split(",");
            double latitude = Double.parseDouble(coords[0].trim());
            double longitude = Double.parseDouble(coords[1].trim());
            
            // Проверяем валидность координат
            if (!locationService.isValidCoordinates(latitude, longitude)) {
                message.setText("❌ Неверные координаты!\n" +
                              "Широта должна быть от -90 до 90\n" +
                              "Долгота от -180 до 180\n\n" +
                              "*Пример:* `/weather 55.7558,37.6173`");
                message.setParseMode("Markdown");
                bot.execute(message);
                return;
            }
            
            // Получаем название города
            message.setText("📍 Определяю местоположение...");
            bot.execute(message);
            
            String cityName = locationService.getCityNameByCoordinates(latitude, longitude);
            
            // Получаем погоду
            SendMessage loadingMsg = new SendMessage();
            loadingMsg.setChatId(chatId.toString());
            loadingMsg.setText("🌤 Запрашиваю погоду...");
            bot.execute(loadingMsg);
            
            WeatherData weather = locationService.getWeatherByCoordinates(latitude, longitude);
            
            // Форматируем ответ
            String locationInfo = locationService.formatCoordinates(latitude, longitude);
            
            SendMessage weatherMessage = new SendMessage();
            weatherMessage.setChatId(chatId.toString());
            weatherMessage.setText(String.format(
                "📍 *Погода по координатам*\n\n" +
                "📌 Координаты: %s\n" +
                "🏙️ Ближайший населенный пункт: %s\n\n" +
                "%s",
                locationInfo, cityName, weather.toString()
            ));
            weatherMessage.setParseMode("Markdown");
            bot.execute(weatherMessage);
            
        } catch (NumberFormatException e) {
            message.setText("❌ Неверный формат координат!\n" +
                          "Используйте формат: широта,долгота\n" +
                          "*Пример:* `/weather 55.7558,37.6173`");
            message.setParseMode("Markdown");
            try {
                bot.execute(message);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            message.setText("❌ Не удалось получить погоду по координатам.\n" +
                          "Причина: " + e.getMessage() + "\n\n" +
                          "Попробуйте отправить название города:");
            try {
                bot.execute(message);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void handleWeatherCommand(String command, Long chatId, String userName, TelegramLongPollingBot bot) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        
        // Проверяем, указан ли город
        String[] parts = command.split(" ", 2);
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            message.setText("Пожалуйста, укажите город после команды.\n*Пример:* /weather Москва");
            message.setParseMode("Markdown");
        } else {
            String city = parts[1].trim();
            
            try {
                // Показываем сообщение о загрузке
                message.setText("⏳ Запрашиваю погоду для " + city + "...");
                bot.execute(message);
                
                // Получаем данные о погоде (теперь с кэшированием)
                WeatherData weather = weatherAPI.getWeather(city);
                
                // Отправляем результат
                SendMessage weatherMessage = new SendMessage();
                weatherMessage.setChatId(chatId.toString());
                weatherMessage.setText(weather.toString());
                
                bot.execute(weatherMessage);
                return; // Возвращаемся, так как уже отправили два сообщения
                
            } catch (Exception e) {
                String errorMessage = "❌ " + e.getMessage();
                
                // Более информативное сообщение об ошибке
                if (e.getMessage().contains("после 3 попыток")) {
                    errorMessage += "\n\n🔧 *Что можно сделать:*" +
                                  "\n• Проверить интернет-соединение" +
                                  "\n• Попробовать через 1-2 минуты" +
                                  "\n• Использовать другой город";
                } else {
                    errorMessage += "\n\n🔧 *Что можно сделать:*" +
                                  "\n• Проверить название города" +
                                  "\n• Использовать английское название" +
                                  "\n• Попробовать позже";
                }
                
                message.setText(errorMessage);
            }
        }
        
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    
    private String getHelpText(String subCommand) {
        switch (subCommand.toLowerCase()) {
            case "start":
                return "🚀 *Команда /start*\n\n" +
                       "Запускает бота и выводит приветственное сообщение.\n\n" +
                       "📝 *Использование:*\n" +
                       "Просто отправьте `/start`";
                
            case "help":
                return "❓ *Команда /help*\n\n" +
                       "Показывает справочную информацию о командах бота.\n\n" +
                       "📝 *Использование:*\n" +
                       "`/help` - общая справка по всем командам\n" +
                       "`/help [команда]` - подробная справка по конкретной команде\n\n" +
                       "📋 *Примеры:*\n" +
                       "`/help weather` - справка по команде погоды\n" +
                       "`/help about` - справка по команде о боте";
                
            case "about":
                return "ℹ️ *Команда /about*\n\n" +
                       "Рассказывает о функционале бота, его возможностях и используемых технологиях.\n\n" +
                       "📝 *Использование:*\n" +
                       "Просто отправьте `/about`\n\n" +
                       "📊 *Что узнаете:*\n" +
                       "• Возможности бота\n" +
                       "• Используемые технологии\n" +
                       "• Тип получаемой информации о погоде";
                
            case "authors":
                return "👨‍💻 *Команда /authors*\n\n" +
                       "Показывает информацию об авторах проекта.\n\n" +
                       "📝 *Использование:*\n" +
                       "Просто отправьте `/authors`\n\n" +
                       "👥 *Что узнаете:*\n" +
                       "• Имена разработчиков\n" +
                       "• Информацию о создателях бота";
                
            case "weather":
                return "🌤 *Команда /weather*\n\n" +
                       "Показывает текущую погоду в указанном городе или по координатам.\n\n" +
                       "📝 *Использование:*\n" +
                       "`/weather [название города]` - погода в городе\n" +
                       "`/weather [широта],[долгота]` - погода по координатам\n\n" +
                       "📋 *Примеры:*\n" +
                       "`/weather Москва`\n" +
                       "`/weather London`\n" +
                       "`/weather 55.7558,37.6173`\n\n" +
                       "🌍 *Особенности:*\n" +
                       "• Поддерживаются города на разных языках\n" +
                       "• Работает с координатами\n" +
                       "• Кэширование частых запросов\n\n" +
                       "📍 *Альтернатива:*\n" +
                       "Отправьте геолокацию через меню 📎 → Location";
                
            case "cache":
            case "cachestats":
                return "⚡ *Команда /cachestats*\n\n" +
                       "Показывает статистику кэша погоды.\n\n" +
                       "📊 *Что показывает:*\n" +
                       "• Количество городов в кэше\n" +
                       "• Максимальный размер кэша\n" +
                       "• Время жизни записей (TTL)\n" +
                       "• Процент использования\n\n" +
                       "🔄 *Как работает кэш:*\n" +
                       "Данные о погоде хранятся 10 минут\n" +
                       "При повторном запросе того же города\n" +
                       "используются кэшированные данные\n\n" +
                       "💾 *Преимущества:*\n" +
                       "• Быстрые ответы\n" +
                       "• Снижение нагрузки на API\n" +
                       "• Экономия лимита запросов";
                
            case "clearcache":
                return "🧹 *Команда /clearcache*\n\n" +
                       "Очищает весь кэш погоды.\n\n" +
                       "⚠️ *Когда использовать:*\n" +
                       "• Если данные устарели\n" +
                       "• При проблемах с отображением\n" +
                       "• Для тестирования\n\n" +
                       "📝 *Использование:*\n" +
                       "Просто отправьте `/clearcache`\n\n" +
                       "🔄 *Что происходит:*\n" +
                       "Все кэшированные данные удаляются\n" +
                       "Следующие запросы будут к API";
                
            case "location":
                return "📍 *Геолокация*\n\n" +
                       "Позволяет получить погоду по вашим координатам.\n\n" +
                       "📱 *Как отправить геолокацию:*\n" +
                       "1. Нажмите на скрепку 📎\n" +
                       "2. Выберите \"Location\"\n" +
                       "3. Отправьте свою геолокацию\n\n" +
                       "📍 *Команда /location:*\n" +
                       "Показывает инструкцию по отправке геолокации\n\n" +
                       "🌐 *Альтернатива:*\n" +
                       "Используйте команду с координатами:\n" +
                       "`/weather 55.7558,37.6173`";
                
            case "inline":
                return "💬 *Инлайн-режим*\n\n" +
                       "Позволяет использовать бота в любом чате без добавления в контакты.\n\n" +
                       "📱 *Как использовать:*\n" +
                       "1. Откройте любой чат (личный или групповой)\n" +
                       "2. Начните вводить `@" + com.mybot.config.BotConfig.BOT_USERNAME + "`\n" +
                       "3. Добавьте название города\n" +
                       "4. Выберите результат из списка\n\n" +
                       "💡 *Преимущества:*\n" +
                       "• Работает в любом чате\n" +
                       "• Не нужно добавлять бота в контакты\n" +
                       "• Быстрый доступ к погоде\n\n" +
                       "📋 *Пример:*\n" +
                       "`@" + com.mybot.config.BotConfig.BOT_USERNAME + " Москва`";
                
            default:
                return "❌ Раздел помощи для команды '" + subCommand + "' не найден.\n\n" +
                       "📋 *Доступные команды для справки:*\n" +
                       "• `start` - запуск бота\n" +
                       "• `help` - помощь\n" +
                       "• `about` - о боте\n" +
                       "• `authors` - авторы\n" +
                       "• `weather` - погода\n" +
                       "• `cachestats` - статистика кэша\n" +
                       "• `clearcache` - очистка кэша\n" +
                       "• `location` - геолокация\n" +
                       "• `inline` - инлайн-режим\n\n" +
                       "💡 *Пример:* `/help weather`";
        }
    }
}