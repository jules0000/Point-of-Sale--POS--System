package com.pos.utils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
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

    public String getDatabaseUsername() {
        return getProperty("db.username");
    }

    public String getDatabasePassword() {
        return getProperty("db.password", "");
    }

    public String getBackupPath() {
        return getProperty("backup.path", "backups/");
    }

    public void reloadProperties() {
        loadProperties();
    }

    public String getDatabaseUrl() {
        return getProperty("db.url", "jdbc:mysql://localhost:3306/pos_db");
    }

    public String getAppName() {
        return getProperty("app.name", "POS System");
    }

    public String getAppVersion() {
        return getProperty("app.version", "1.0.0");
    }

    public String getCompanyName() {
        return getProperty("app.company", "Your Company");
    }

    public String getCurrency() {
        return getProperty("app.currency", "USD");
    }

    public BigDecimal getTaxRate() {
        String rate = getProperty("app.tax.rate", "0.10");
        try {
            return new BigDecimal(rate);
        } catch (NumberFormatException e) {
            return new BigDecimal("0.10");
        }
    }

    public int getLowStockThreshold() {
        String threshold = getProperty("app.low.stock.threshold", "10");
        try {
            return Integer.parseInt(threshold);
        } catch (NumberFormatException e) {
            return 10;
        }
    }

    public String getUiTheme() {
        return getProperty("ui.theme", "light");
    }

    public int getUiFontSize() {
        String size = getProperty("ui.font.size", "12");
        try {
            return Integer.parseInt(size);
        } catch (NumberFormatException e) {
            return 12;
        }
    }

    public String getPrinterName() {
        return getProperty("printer.name", "POS-58");
    }

    public int getPrinterWidth() {
        String width = getProperty("printer.width", "48");
        try {
            return Integer.parseInt(width);
        } catch (NumberFormatException e) {
            return 48;
        }
    }

    public boolean isBackupEnabled() {
        return Boolean.parseBoolean(getProperty("backup.enabled", "true"));
    }

    public int getSessionTimeout() {
        String timeout = getProperty("security.session.timeout", "30");
        try {
            return Integer.parseInt(timeout);
        } catch (NumberFormatException e) {
            return 30;
        }
    }

    public int getMinPasswordLength() {
        String length = getProperty("security.password.min.length", "8");
        try {
            return Integer.parseInt(length);
        } catch (NumberFormatException e) {
            return 8;
        }
    }

    public String getLogFile() {
        return getProperty("logging.file", "logs/pos.log");
    }

    public String getLogLevel() {
        return getProperty("logging.level", "INFO");
    }

    public boolean isLoggingEnabled() {
        return Boolean.parseBoolean(getProperty("logging.enabled", "true"));
    }
} 