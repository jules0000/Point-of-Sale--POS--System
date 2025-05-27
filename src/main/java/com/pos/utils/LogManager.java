package com.pos.utils;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

public class LogManager {
    private static final ConfigLoader config = ConfigLoader.getInstance();
    private static final Logger LOGGER = Logger.getLogger("POS");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int MAX_LOG_SIZE = 10 * 1024 * 1024; // 10MB
    private static final int MAX_LOG_FILES = 5;

    static {
        try {
            setupLogger();
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

    private static void setupLogger() throws IOException {
        // Create logs directory if it doesn't exist
        Path logDir = Paths.get("logs");
        if (!Files.exists(logDir)) {
            Files.createDirectories(logDir);
        }

        // Create file handler with rotation
        FileHandler fileHandler = new FileHandler(
            "logs/pos_%g.log",
            MAX_LOG_SIZE,
            MAX_LOG_FILES,
            true
        );

        // Create console handler
        ConsoleHandler consoleHandler = new ConsoleHandler();

        // Create custom formatter
        Formatter formatter = new Formatter() {
            @Override
            public String format(LogRecord record) {
                return String.format(
                    "[%s] [%s] %s - %s%n",
                    LocalDateTime.now().format(DATE_FORMAT),
                    record.getLevel(),
                    record.getSourceClassName(),
                    record.getMessage()
                );
            }
        };

        // Set formatter for both handlers
        fileHandler.setFormatter(formatter);
        consoleHandler.setFormatter(formatter);

        // Set log level from configuration
        Level logLevel = Level.parse(config.getLogLevel());
        LOGGER.setLevel(logLevel);
        fileHandler.setLevel(logLevel);
        consoleHandler.setLevel(logLevel);

        // Remove default handlers and add our custom ones
        for (Handler handler : LOGGER.getHandlers()) {
            LOGGER.removeHandler(handler);
        }
        LOGGER.addHandler(fileHandler);
        LOGGER.addHandler(consoleHandler);

        // Don't use parent handlers
        LOGGER.setUseParentHandlers(false);
    }

    public static void info(String message) {
        LOGGER.info(message);
    }

    public static void info(String message, Object... params) {
        LOGGER.info(String.format(message, params));
    }

    public static void warning(String message) {
        LOGGER.warning(message);
    }

    public static void warning(String message, Object... params) {
        LOGGER.warning(String.format(message, params));
    }

    public static void error(String message) {
        LOGGER.severe(message);
    }

    public static void error(String message, Throwable throwable) {
        LOGGER.severe(message + "\n" + getStackTrace(throwable));
    }

    public static void error(String message, Object... params) {
        LOGGER.severe(String.format(message, params));
    }

    public static void debug(String message) {
        LOGGER.fine(message);
    }

    public static void debug(String message, Object... params) {
        LOGGER.fine(String.format(message, params));
    }

    public static void trace(String message) {
        LOGGER.finest(message);
    }

    public static void trace(String message, Object... params) {
        LOGGER.finest(String.format(message, params));
    }

    private static String getStackTrace(Throwable throwable) {
        if (throwable == null) return "";
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    public static void logUserAction(String userId, String action) {
        info("User %s performed action: %s", userId, action);
    }

    public static void logSystemAction(String action) {
        info("System action: %s", action);
    }

    public static void logSecurityEvent(String event) {
        warning("Security event: %s", event);
    }

    public static void logDatabaseAction(String action) {
        debug("Database action: %s", action);
    }

    public static void logException(String context, Exception e) {
        error("Exception in %s: %s", context, e.getMessage(), e);
    }
} 