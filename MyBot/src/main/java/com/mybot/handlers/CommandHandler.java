package com.mybot.handlers;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class CommandHandler {
    
    public void handleCommand(String command, Long chatId, String userName, TelegramLongPollingBot bot) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        
        switch (command) {
            case "/start":
                message.setText("Привет, " + userName + "! 👋\nЯ твой первый бот на Java!");
                break;
                
            case "/help":
                message.setText("Помощь:\n/start - начать\n/help - помощь");
                break;
                
            default:
                message.setText("Неизвестная команда");
                break;
        }
        
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}