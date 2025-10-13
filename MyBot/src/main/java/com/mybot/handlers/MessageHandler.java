package com.mybot.handlers;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MessageHandler {
    
    public void handleMessage(String text, Long chatId, String userName, TelegramLongPollingBot bot) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        
        if (text.toLowerCase().contains("привет")) {
            message.setText("Привет, " + userName + "! 😊");
        } else {
            message.setText("Ты сказал: " + text);
        }
        
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
