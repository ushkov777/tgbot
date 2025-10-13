package com.mybot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot());
            System.out.println("✅ Бот УСПЕШНО запущен!");
            System.out.println("🤖 Ищи бота в Telegram: @" + com.mybot.config.BotConfig.BOT_USERNAME);
        } catch (TelegramApiException e) {
            System.out.println("❌ Ошибка запуска бота:");
            e.printStackTrace();
        }
    }
}