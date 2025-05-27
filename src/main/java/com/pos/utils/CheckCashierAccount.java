package com.pos.utils;

import com.pos.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CheckCashierAccount {
    private static final Logger LOGGER = Logger.getLogger(CheckCashierAccount.class.getName());
    
    public static void main(String[] args) {
        LOGGER.info("Checking cashier account status...");
        
        try {
            // Test database connection
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            if (!dbConn.testConnection()) {
                LOGGER.severe("Failed to connect to database. Please check your MySQL configuration.");
                return;
            }
            
            // Get database connection
            Connection conn = dbConn.getConnection();
            LOGGER.info("Connected to database successfully.");
            
            // Check cashier account
            String sql = "SELECT * FROM users WHERE username = 'cashier'";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    LOGGER.info("Cashier account found:");
                    LOGGER.info("Username: " + rs.getString("username"));
                    LOGGER.info("Role: " + rs.getString("role"));
                    LOGGER.info("Active: " + rs.getBoolean("active"));
                    LOGGER.info("Password Hash: " + rs.getString("password_hash"));
                    
                    // Check if role is correct
                    String role = rs.getString("role");
                    if (!"CASHIER".equalsIgnoreCase(role)) {
                        LOGGER.warning("Cashier role is incorrect. Current role: " + role);
                        LOGGER.info("Fixing role to CASHIER...");
                        
                        // Update role to CASHIER
                        sql = "UPDATE users SET role = 'CASHIER' WHERE username = 'cashier'";
                        try (PreparedStatement updateStmt = conn.prepareStatement(sql)) {
                            updateStmt.executeUpdate();
                            LOGGER.info("Role updated successfully.");
                        }
                    }
                } else {
                    LOGGER.warning("Cashier account not found!");
                    LOGGER.info("Creating new cashier account...");
                    
                    // Create new cashier account
                    sql = "INSERT INTO users (id, username, password_hash, full_name, role, email, active) " +
                          "VALUES (UUID(), 'cashier', ?, 'Default Cashier', 'CASHIER', 'cashier@pos.com', true)";
                    
                    try (PreparedStatement insertStmt = conn.prepareStatement(sql)) {
                        // Use a known working hash for 'cashier' password
                        String passwordHash = "$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBAQHxQxJ5JQHy";
                        insertStmt.setString(1, passwordHash);
                        insertStmt.executeUpdate();
                        LOGGER.info("New cashier account created successfully.");
                    }
                }
            }
            
            // Close database connection
            dbConn.closePool();
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking cashier account: " + e.getMessage(), e);
        }
    }
} 