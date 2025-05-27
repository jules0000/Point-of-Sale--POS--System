package com.pos.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.Locale;

/**
 * Utility class for formatting numbers, currency, and dates.
 */
public class FormatUtils {
    private static final ConfigLoader config = ConfigLoader.getInstance();
    private static final Currency currency = Currency.getInstance("PHP");
    private static final Locale locale = new Locale("en", "PH");
    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
    private static final DecimalFormat quantityFormatter = new DecimalFormat("#,##0.##");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(
            config.getProperty("app.date.format", "yyyy-MM-dd"));
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(
            config.getProperty("app.time.format", "HH:mm:ss"));
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
            config.getProperty("app.datetime.format", "yyyy-MM-dd HH:mm:ss"));

    static {
        currencyFormatter.setCurrency(currency);
        quantityFormatter.setRoundingMode(RoundingMode.HALF_UP);
    }

    private FormatUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Format a BigDecimal as currency
     * @param amount The amount to format
     * @return The formatted currency string
     */
    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return currencyFormatter.format(0);
        }
        return currencyFormatter.format(amount.doubleValue());
    }

    /**
     * Format a quantity with up to 2 decimal places
     * @param quantity The quantity to format
     * @return The formatted quantity string
     */
    public static String formatQuantity(double quantity) {
        return quantityFormatter.format(quantity);
    }

    /**
     * Format a quantity with up to 2 decimal places
     * @param quantity The quantity to format
     * @return The formatted quantity string
     */
    public static String formatQuantity(int quantity) {
        return quantityFormatter.format(quantity);
    }

    /**
     * Format a date
     * @param dateTime The date to format
     * @return The formatted date string
     */
    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(dateFormatter);
    }

    /**
     * Format a time
     * @param dateTime The time to format
     * @return The formatted time string
     */
    public static String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(timeFormatter);
    }

    /**
     * Format a date and time
     * @param dateTime The date and time to format
     * @return The formatted date and time string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(dateTimeFormatter);
    }

    /**
     * Format a percentage
     * @param value The value to format as percentage
     * @param decimalPlaces The number of decimal places
     * @return The formatted percentage string
     */
    public static String formatPercentage(double value, int decimalPlaces) {
        DecimalFormat df = new DecimalFormat("0." + "0".repeat(decimalPlaces) + "%");
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(value / 100);
    }

    /**
     * Parse a currency string to BigDecimal
     * @param currencyStr The currency string to parse
     * @return The parsed BigDecimal value
     * @throws IllegalArgumentException if the string cannot be parsed
     */
    public static BigDecimal parseCurrency(String currencyStr) {
        try {
            // Remove currency symbol and any special characters
            String cleanStr = currencyStr.replaceAll("[^\\d.-]", "");
            return new BigDecimal(cleanStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid currency format: " + currencyStr);
        }
    }

    /**
     * Parse a quantity string to double
     * @param quantityStr The quantity string to parse
     * @return The parsed double value
     * @throws IllegalArgumentException if the string cannot be parsed
     */
    public static double parseQuantity(String quantityStr) {
        try {
            // Remove any special characters except decimal point
            String cleanStr = quantityStr.replaceAll("[^\\d.-]", "");
            return Double.parseDouble(cleanStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid quantity format: " + quantityStr);
        }
    }

    /**
     * Get the currency symbol
     * @return The currency symbol
     */
    public static String getCurrencySymbol() {
        return currency.getSymbol(locale);
    }
} 