package com.mybot.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigLoaderTest {
    
    @Test
    void testGetProperty() {
        String token = ConfigLoader.getProperty("bot.token");
        assertNotNull(token);
        assertEquals("TEST_BOT_TOKEN", token);
    }
    
    @Test
    void testGetPropertyWithDefault() {
        String value = ConfigLoader.getProperty("non.existing.key", "default");
        assertEquals("default", value);
    }
    
    @Test
    void testGetInt() {
        int retryCount = ConfigLoader.getInt("api.retry.count", 5);
        assertEquals(1, retryCount); // Из тестового файла
        
        int nonExisting = ConfigLoader.getInt("non.existing", 999);
        assertEquals(999, nonExisting);
    }
    
    @Test
    void testGetInt_InvalidNumber() {
        // Используем рефлексию для добавления некорректного значения
        try {
            var field = ConfigLoader.class.getDeclaredField("properties");
            field.setAccessible(true);
            java.util.Properties props = (java.util.Properties) field.get(null);
            props.setProperty("test.invalid.int", "not-a-number");
            
            int value = ConfigLoader.getInt("test.invalid.int", 100);
            assertEquals(100, value); // Должен вернуть default при ошибке парсинга
        } catch (Exception e) {
            fail("Failed to test invalid int: " + e.getMessage());
        }
    }
    
    @Test
    void testGetBoolean() {
        boolean useCache = ConfigLoader.getBoolean("use.cache", false);
        assertTrue(useCache); // Из тестового файла
        
        boolean nonExisting = ConfigLoader.getBoolean("non.existing", true);
        assertTrue(nonExisting);
    }
    
    @Test
    void testGetBoolean_Variations() {
        try {
            var field = ConfigLoader.class.getDeclaredField("properties");
            field.setAccessible(true);
            java.util.Properties props = (java.util.Properties) field.get(null);
            
            props.setProperty("test.bool.true", "true");
            props.setProperty("test.bool.false", "false");
            props.setProperty("test.bool.yes", "yes");
            props.setProperty("test.bool.no", "no");
            props.setProperty("test.bool.on", "on");
            props.setProperty("test.bool.off", "off");
            
            assertTrue(ConfigLoader.getBoolean("test.bool.true", false));
            assertFalse(ConfigLoader.getBoolean("test.bool.false", true));
            assertTrue(ConfigLoader.getBoolean("test.bool.yes", false));
            assertFalse(ConfigLoader.getBoolean("test.bool.no", true));
            assertTrue(ConfigLoader.getBoolean("test.bool.on", false));
            assertFalse(ConfigLoader.getBoolean("test.bool.off", true));
        } catch (Exception e) {
            fail("Failed to test boolean variations: " + e.getMessage());
        }
    }
    
    @Test
    void testConfigLoaderInitialization() {
        // Просто проверяем, что статический блок отработал без ошибок
        assertNotNull(ConfigLoader.getProperty("bot.token"));
    }
}