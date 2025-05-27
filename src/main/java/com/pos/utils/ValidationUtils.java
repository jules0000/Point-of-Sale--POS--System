package com.pos.utils;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ValidationUtils {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[A-Za-z0-9_]{3,20}$"
    );
    
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[A-Za-z\\s'-]{2,50}$"
    );
    
    private static final Pattern BARCODE_PATTERN = Pattern.compile(
        "^[A-Za-z0-9]{8,20}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+?[0-9]{10,15}$"
    );

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }

    public static boolean isValidBarcode(String barcode) {
        return barcode != null && BARCODE_PATTERN.matcher(barcode).matches();
    }

    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }
        
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    public static boolean isValidPrice(BigDecimal price) {
        return price != null && price.compareTo(BigDecimal.ZERO) >= 0 &&
               price.scale() <= 2;
    }

    public static boolean isValidQuantity(int quantity) {
        return quantity >= 0;
    }

    public static boolean isValidDiscount(BigDecimal discount, BigDecimal total) {
        return discount != null && discount.compareTo(BigDecimal.ZERO) >= 0 &&
               discount.compareTo(total) <= 0;
    }

    public static boolean isValidPayment(BigDecimal payment, BigDecimal total) {
        return payment != null && payment.compareTo(total) >= 0;
    }

    public static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        return input.trim()
                   .replaceAll("[<>\"'&]", "")
                   .replaceAll("[\u0000-\u001F\u007F-\u009F]", "");
    }

    public static String sanitizeHtml(String html) {
        if (html == null) {
            return "";
        }
        return html.replaceAll("<[^>]*>", "")
                  .replaceAll("&[^;]+;", "")
                  .trim();
    }

    public static String sanitizeFilename(String filename) {
        if (filename == null) {
            return "";
        }
        return filename.replaceAll("[^a-zA-Z0-9.-]", "_")
                      .replaceAll("\\.{2,}", ".")
                      .trim();
    }

    public static String sanitizeSql(String sql) {
        if (sql == null) {
            return "";
        }
        return sql.replaceAll("['\"\\\\;]", "")
                 .trim();
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNullOrZero(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) == 0;
    }

    public static boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    public static boolean isNegative(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) < 0;
    }

    public static boolean isBetween(BigDecimal value, BigDecimal min, BigDecimal max) {
        return value != null && value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
    }

    public static boolean isBetween(int value, int min, int max) {
        return value >= min && value <= max;
    }
} 