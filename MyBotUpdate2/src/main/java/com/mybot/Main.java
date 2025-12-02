package com.mybot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import com.mybot.config.BotConfig;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 Запуск погодного бота...");
        System.out.println("==============================");
        
        try {
            // Создаем и регистрируем бота
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot());
            
            System.out.println("✅ Бот успешно запущен!");
            System.out.println("🤖 Ищите бота в Telegram: @" + BotConfig.BOT_USERNAME);
            System.out.println("==============================");
            
        } catch (TelegramApiException e) {
            System.out.println("❌ Ошибка запуска бота!");
            System.out.println("Причина: " + e.getMessage());
            System.out.println("\n🔧 Возможные решения:");
            System.out.println("1. Проверьте файл bot-config.properties");
            System.out.println("2. Убедитесь, что BOT_TOKEN корректен");
            System.out.println("3. Проверьте интернет-соединение");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("❌ Критическая ошибка: " + e.getMessage());
            System.exit(1);
        }
    }
}