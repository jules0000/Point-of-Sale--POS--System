package com.pos.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtils {
    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
    private static final NumberFormat numberFormatter = NumberFormat.getNumberInstance(new Locale("en", "US"));
    
    static {
        currencyFormatter.setMinimumFractionDigits(2);
        currencyFormatter.setMaximumFractionDigits(2);
        numberFormatter.setGroupingUsed(true);
    }

    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return currencyFormatter.format(0.00);
        }
        return currencyFormatter.format(amount.setScale(2, RoundingMode.HALF_UP));
    }

    public static String formatNumber(int number) {
        return numberFormatter.format(number);
    }

    public static String formatNumber(double number) {
        return numberFormatter.format(number);
    }

    public static String formatNumber(BigDecimal number) {
        if (number == null) {
            return numberFormatter.format(0);
        }
        return numberFormatter.format(number);
    }

    public static String formatDecimal(BigDecimal number, int scale) {
        if (number == null) {
            return "0." + "0".repeat(scale);
        }
        return number.setScale(scale, RoundingMode.HALF_UP).toString();
    }

    public static BigDecimal parseCurrency(String amount) {
        try {
            String cleaned = amount.replaceAll("[^\\d.-]", "");
            return new BigDecimal(cleaned).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
} 