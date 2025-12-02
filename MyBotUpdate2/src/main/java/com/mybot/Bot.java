package com.mybot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.mybot.config.BotConfig;
import com.mybot.handlers.CommandHandler;
import com.mybot.handlers.MessageHandler;

public class Bot extends TelegramLongPollingBot {
    private final CommandHandler commandHandler;
    private final MessageHandler messageHandler;
    
    public Bot() {
        this.commandHandler = new CommandHandler();
        this.messageHandler = new MessageHandler();
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
        // 1. Обработка сообщений с текстом
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleTextMessage(update);
        } 
        // 2. Обработка геолокации
        else if (update.hasMessage() && update.getMessage().hasLocation()) {
            handleLocationMessage(update);
        }
        // 3. Обработка команд (например, /location)
        else if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            if (messageText.startsWith("/")) {
                handleTextMessage(update);
            }
        }
    }
    
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
            locationService.setWeatherAPI(commandHandler.getWeatherAPI());
            
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