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
                message.setText("Помощь:\n/start - начать\n/help - помощь\n/about - что делает бот\n/authors - авторы\n\n" +
                               "Используйте: /help [команда] для получения подробной информации");
                break;

            case "/about":
                message.setText("Бот принимает сообщения и отвечает на них");
                break;
            
            case "/authors":
                message.setText("Ушков Роман и Крылосов Даниил");
                break;
                
            default:
                // Обработка команды /help с аргументами
                if (command.startsWith("/help ")) {
                    String helpText = getHelpText(command.substring(6)); // Убираем "/help "
                    message.setText(helpText);
                } else {
                    message.setText("Неизвестная команда");
                }
                break;
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
                return "Команда /start:\n" +
                       "Запускает бота и выводит приветственное сообщение.\n" +
                       "Использование: просто отправьте /start";
                
            case "help":
                return "Команда /help:\n" +
                       "Показывает справочную информацию.\n" +
                       "Использование:\n" +
                       "/help - общая справка\n" +
                       "/help [команда] - справка по конкретной команде";
                
            case "about":
                return "Команда /about:\n" +
                       "Рассказывает о функционале бота.\n" +
                       "Использование: просто отправьте /about";
                
            case "authors":
                return "Команда /authors:\n" +
                       "Показывает информацию об авторах проекта.\n" +
                       "Использование: просто отправьте /authors";
                
            default:
                return "Раздел помощи для команды '" + subCommand + "' не найден.\n\n" +
                       "Доступные команды для справки:\n" +
                       "• start\n• help\n• about\n• authors\n\n" +
                       "Пример: /help start";
        }
    }
}
