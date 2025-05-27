package com.pos.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for loading and managing application configuration properties.
 */
public class ConfigLoader {
    private static final Logger LOGGER = Logger.getLogger(ConfigLoader.class.getName());
    private static final String CONFIG_FILE = "application.properties";
    private static ConfigLoader instance;
    private final Properties properties;

    private ConfigLoader() {
        properties = new Properties();
        loadProperties();
    }

    public static ConfigLoader getInstance() {
        if (instance == null) {
            instance = new ConfigLoader();
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                LOGGER.severe("Unable to find " + CONFIG_FILE);
                throw new RuntimeException("Configuration file not found: " + CONFIG_FILE);
            }
            properties.load(input);
            LOGGER.info("Configuration loaded successfully");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading configuration", e);
            throw new RuntimeException("Error loading configuration", e);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getIntProperty(String key) {
        String value = getProperty(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid integer property: " + key, e);
            throw new RuntimeException("Invalid integer property: " + key, e);
        }
    }

    public int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid integer property: " + key + ", using default: " + defaultValue);
            return defaultValue;
        }
    }

    public boolean getBooleanProperty(String key) {
        String value = getProperty(key);
        return Boolean.parseBoolean(value);
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public double getDoubleProperty(String key) {
        String value = getProperty(key);
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid double property: " + key, e);
            throw new RuntimeException("Invalid double property: " + key, e);
        }
    }

    public double getDoubleProperty(String key, double defaultValue) {
        String value = getProperty(key);
        try {
            return value != null ? Double.parseDouble(value) : defaultValue;
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid double property: " + key + ", using default: " + defaultValue);
            return defaultValue;
        }
    }

    public void reloadProperties() {
        loadProperties();
    }
} 