package com.mybot.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BotConfigTest {

    @Test
    void testBotTokenNotNull() {
        assertNotNull(BotConfig.BOT_TOKEN, "BOT_TOKEN should not be null");
    }

    @Test
    void testBotUsernameNotNull() {
        assertNotNull(BotConfig.BOT_USERNAME, "BOT_USERNAME should not be null");
    }

    @Test
    void testBotTokenNotEmpty() {
        assertFalse(BotConfig.BOT_TOKEN.isEmpty(), "BOT_TOKEN should not be empty");
    }

    @Test
    void testBotUsernameNotEmpty() {
        assertFalse(BotConfig.BOT_USERNAME.isEmpty(), "BOT_USERNAME should not be empty");
    }
}