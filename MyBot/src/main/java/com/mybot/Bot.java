package com.mybot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
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
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom().getFirstName();
            
            if (messageText.startsWith("/")) {
                commandHandler.handleCommand(messageText, chatId, userName, this);
            } else {
                messageHandler.handleMessage(messageText, chatId, userName, this);
            }
        }
    }
}