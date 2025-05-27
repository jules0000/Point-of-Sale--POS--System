package com.pos.utils;

import com.pos.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class AuditLogger {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public static void logAction(String userId, String action, String tableName, String recordId, String details) {
        String sql = "INSERT INTO audit_logs (id, user_id, action, table_name, record_id, details) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setString(2, userId);
            stmt.setString(3, action);
            stmt.setString(4, tableName);
            stmt.setString(5, recordId);
            stmt.setString(6, details);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Log to file system as fallback
            System.err.println("Failed to write audit log: " + e.getMessage());
            System.err.println("Action: " + action + ", Table: " + tableName + ", Record: " + recordId);
        }
    }

    public static void logUserAction(String userId, String action, String details) {
        logAction(userId, action, "users", userId, details);
    }

    public static void logProductAction(String userId, String productId, String action, String details) {
        logAction(userId, action, "products", productId, details);
    }

    public static void logSaleAction(String userId, String saleId, String action, String details) {
        logAction(userId, action, "sales", saleId, details);
    }

    public static void logSystemAction(String action, String details) {
        logAction(null, action, "system", null, details);
    }
} 