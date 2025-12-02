package com.mybot.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BotConfigTest {

    @Test
    void testBotConfig() {
        assertNotNull(BotConfig.BOT_TOKEN);
        assertNotNull(BotConfig.BOT_USERNAME);
        assertNotNull(BotConfig.WEATHER_API_KEY);
        assertNotNull(BotConfig.WEATHER_API_URL);
        
        assertFalse(BotConfig.BOT_TOKEN.isEmpty());
        assertFalse(BotConfig.BOT_USERNAME.isEmpty());
        assertFalse(BotConfig.WEATHER_API_KEY.isEmpty());
        assertFalse(BotConfig.WEATHER_API_URL.isEmpty());
    }
}