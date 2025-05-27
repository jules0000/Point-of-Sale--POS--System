package com.pos.utils;

import com.pos.util.PasswordUtils;

public class PasswordGenerator {
    public static void main(String[] args) {
        // Generate hashes for default users
        String adminPassword = "admin";
        String cashierPassword = "cashier";
        
        String adminHash = PasswordUtils.hashPassword(adminPassword);
        String cashierHash = PasswordUtils.hashPassword(cashierPassword);
        
        System.out.println("Generated password hashes:");
        System.out.println("Admin password hash: " + adminHash);
        System.out.println("Cashier password hash: " + cashierHash);
        
        System.out.println("\nSQL to update users:");
        System.out.println("UPDATE users SET password_hash = '" + adminHash + "' WHERE username = 'admin';");
        System.out.println("UPDATE users SET password_hash = '" + cashierHash + "' WHERE username = 'cashier';");
        
        System.out.println("\nSQL for data.sql:");
        System.out.println("INSERT INTO users (id, username, password_hash, full_name, role, email, active) VALUES");
        System.out.println("(UUID(), 'admin', '" + adminHash + "', 'System Administrator', 'Admin', 'admin@pos.com', true),");
        System.out.println("(UUID(), 'cashier', '" + cashierHash + "', 'Default Cashier', 'Cashier', 'cashier@pos.com', true);");
    }
} 