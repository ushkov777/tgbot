### MyBot - Telegram Bot

Java-бот для Telegram, который отвечает на сообщения пользователей. Создан с использованием Java 11, Maven и Telegram Bot API.

### Описание проекта

Это Telegram бот на Java, использующий Long Polling для получения обновлений. Бот умеет обрабатывать как команды (начинающиеся с `/`), так и обычные текстовые сообщения.

### Структура проекта

**MyBot/**
**├── src/**
**│ ├── main/**
**│ │ └── java/**
**│ │ └── com/**
**│ │ └── mybot/**
**│ │ ├── Bot.java # Главный класс бота**
**│ │ ├── Main.java # Точка входа в приложение**
**│ │ ├── config/**
**│ │ │ └── BotConfig.java # Конфигурация бота**
**│ │ └── handlers/**
**│ │ ├── CommandHandler.java # Обработка команд**
**│ │ └── MessageHandler.java # Обработка сообщений**
**│ └── test/**
**│ └── java/**
**│ └── com/**
**│ └── mybot/**
**│ └── handlers/**
**│ └── MessageHandlerTest.java # Юнит-тесты**
**├── pom.xml # Конфигурация Maven**

### Быстрый старт

### Предварительные требования
- Java 11 или выше
- Maven 3.6+
- Токен бота Telegram от [@BotFather](https://t.me/BotFather)

### Установка и настройка

1. **Клонируйте и настройте бота:**

bash
git clone <ваш-репозиторий>
cd MyBot
   
2. **Обновите учетные данные бота в BotConfig.java:**

java
public class BotConfig {
    public static final String BOT_TOKEN = "ваш-настоящий-токен-бота";
    public static final String BOT_USERNAME = "ваш-username-бота";
}

3. **Соберите проект:**

bash
mvn clean compile

4. **Запустите бота:**

bash
mvn exec:java -Dexec.mainClass="com.mybot.Main"

### Разработка

**Сборка**

bash
mvn clean package

**Тестирование**

bash
mvn test

**Отчет о покрытии кода**

bash
mvn jacoco:report

Посмотрите отчет: target/site/jacoco/index.html

## Зависимости

**Основные зависимости**

telegrambots (6.8.0) - API для Telegram ботов

slf4j-simple (2.0.9) - Логирование

**Тестовые зависимости**

JUnit Jupiter (5.9.2) - Юнит-тестирование

Mockito (4.11.0) - Фреймворк для моков

## Тестирование

Проект включает комплексные юнит-тесты с использованием JUnit 5 и Mockito:

bash

**Запуск тестов**

mvn test

**Запуск тестов с покрытием**

mvn jacoco:prepare-agent test jacoco:report

**Структура тестов**

MessageHandlerTest - Тесты для обработки сообщений

Параметризованные тесты для различных форматов приветственных сообщений

Тестирование взаимодействий с Telegram API на основе моков

## Конфигурация

**Конфигурация бота**

Отредактируйте src/main/java/com/mybot/config/BotConfig.java:

java
public class BotConfig {
    public static final String BOT_TOKEN = "ВАШ_ТОКЕН_БОТА";
    public static final String BOT_USERNAME = "ВАШ_USERNAME_БОТА";
}

**Конфигурация Maven**

Ключевые свойства в pom.xml:

Версия Java: 11

Кодировка UTF-8

Тестирование с JUnit 5 и Mockito

JaCoCo для покрытия кода

## Основные компоненты

**Основные классы**

Bot.java - Главный класс бота, расширяющий TelegramLongPollingBot

Main.java - Точка входа в приложение

BotConfig.java - Централизованная конфигурация

**Обработчики**

CommandHandler.java - Обрабатывает команды бота (начинающиеся с /)

MessageHandler.java - Обрабатывает обычные текстовые сообщения

**Примеры использования**

Бот поддерживает:

Команду /start

Приветственные сообщения ("привет", "hello" и т.д.)

Обработку обычных текстовых сообщений

**Плагины Maven**

maven-surefire-plugin - Выполнение тестов

jacoco-maven-plugin - Отчеты о покрытии кода

**Качество кода**

Покрытие юнит-тестами с JUnit 5

Мок-тестирование с Mockito

Отчеты о покрытии кода с JaCoCo

Автоматизация сборки на основе Maven
