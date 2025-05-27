package com.pos.utils;

import com.pos.db.DatabaseConnection;
import com.pos.util.PasswordUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CashierPasswordReset {
    private static final Logger LOGGER = Logger.getLogger(CashierPasswordReset.class.getName());
    
    public static void main(String[] args) {
        LOGGER.info("Starting cashier password reset...");
        
        try {
            // Test database connection first
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            if (!dbConn.testConnection()) {
                LOGGER.severe("Failed to connect to database. Please check your MySQL configuration.");
                return;
            }
            
            // Get database connection
            Connection conn = dbConn.getConnection();
            LOGGER.info("Connected to database successfully.");
            
            // Generate new password hash for 'cashier'
            String newPasswordHash = PasswordUtils.hashPassword("cashier");
            LOGGER.info("Generated new password hash: " + newPasswordHash);
            
            // Update cashier password
            String sql = "UPDATE users SET password_hash = ?, active = true WHERE username = 'cashier'";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newPasswordHash);
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    LOGGER.info("Cashier password reset successfully.");
                    LOGGER.info("You can now log in with:");
                    LOGGER.info("Username: cashier");
                    LOGGER.info("Password: cashier");
                } else {
                    LOGGER.warning("Cashier user not found. Creating new cashier account...");
                    
                    // Create new cashier account if it doesn't exist
                    sql = "INSERT INTO users (id, username, password_hash, full_name, role, email, active) " +
                          "VALUES (UUID(), 'cashier', ?, 'Default Cashier', 'CASHIER', 'cashier@pos.com', true)";
                    
                    try (PreparedStatement insertStmt = conn.prepareStatement(sql)) {
                        insertStmt.setString(1, newPasswordHash);
                        insertStmt.executeUpdate();
                        LOGGER.info("New cashier account created successfully.");
                        LOGGER.info("You can now log in with:");
                        LOGGER.info("Username: cashier");
                        LOGGER.info("Password: cashier");
                    }
                }
            }
            
            // Close database connection
            dbConn.closePool();
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during cashier password reset: " + e.getMessage(), e);
        }
    }
} 