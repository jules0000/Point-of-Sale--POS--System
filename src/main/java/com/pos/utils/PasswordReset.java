package com.pos.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.pos.db.DatabaseConnection;
import com.pos.util.PasswordUtils;

public class PasswordReset {
    public static void main(String[] args) {
        System.out.println("Starting password reset...");
        
        try {
            // Test database connection first
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            if (!dbConn.testConnection()) {
                System.err.println("Failed to connect to database. Please check your MySQL configuration.");
                return;
            }
            
            // Get database connection
            Connection conn = dbConn.getConnection();
            System.out.println("Connected to database successfully.");
            
            // Generate new password hash for 'admin'
            String newPasswordHash = PasswordUtils.hashPassword("admin");
            System.out.println("Generated new password hash: " + newPasswordHash);
            
            // Update admin password
            String sql = "UPDATE users SET password_hash = ? WHERE username = 'admin'";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newPasswordHash);
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    System.out.println("Admin password reset successfully.");
                    System.out.println("You can now log in with:");
                    System.out.println("Username: admin");
                    System.out.println("Password: admin");
                } else {
                    System.out.println("Admin user not found.");
                }
            }
            
            // Close database connection
            dbConn.closePool();
            
        } catch (Exception e) {
            System.err.println("Error resetting password: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 