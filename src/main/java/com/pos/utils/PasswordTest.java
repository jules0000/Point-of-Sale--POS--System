package com.pos.utils;

import com.pos.util.PasswordUtils;

public class PasswordTest {
    public static void main(String[] args) {
        // Test admin password
        String adminPassword = "admin";
        String adminHash = PasswordUtils.hashPassword(adminPassword);
        System.out.println("Admin password hash: " + adminHash);
        System.out.println("Admin verification: " + PasswordUtils.verifyPassword(adminPassword, adminHash));
        
        // Test cashier password
        String cashierPassword = "cashier";
        String cashierHash = PasswordUtils.hashPassword(cashierPassword);
        System.out.println("\nCashier password hash: " + cashierHash);
        System.out.println("Cashier verification: " + PasswordUtils.verifyPassword(cashierPassword, cashierHash));
    }
} 