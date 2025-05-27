package com.pos.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class SaleNumberGenerator {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final AtomicInteger sequence = new AtomicInteger(1);
    private static String currentDate = "";

    public static synchronized String generateSaleNumber() {
        LocalDateTime now = LocalDateTime.now();
        String today = now.format(DATE_FORMAT);
        
        // Reset sequence if date changes
        if (!today.equals(currentDate)) {
            sequence.set(1);
            currentDate = today;
        }
        
        // Format: YYYYMMDD-XXXX (where XXXX is a sequence number)
        return String.format("%s-%04d", today, sequence.getAndIncrement());
    }

    // Reset sequence (mainly for testing purposes)
    public static void resetSequence() {
        sequence.set(1);
        currentDate = "";
    }
} 