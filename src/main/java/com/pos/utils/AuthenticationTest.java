package com.pos.utils;

import com.pos.db.DatabaseConnection;
import com.pos.db.UserDAO;
import com.pos.model.User;
import com.pos.util.PasswordUtils;

import java.util.Optional;

public class AuthenticationTest {
    public static void main(String[] args) {
        System.out.println("Starting Authentication Test...");
        
        // Test database connection
        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        if (!dbConn.testConnection()) {
            System.err.println("Failed to connect to database. Please check your MySQL configuration.");
            System.err.println("Make sure MySQL is running and the root password is correctly set.");
            return;
        }
        
        // Test user authentication
        UserDAO userDAO = new UserDAO();
        
        // Test admin login
        System.out.println("\nTesting admin login...");
        testAuthentication(userDAO, "admin", "admin");
        
        // Test cashier login
        System.out.println("\nTesting cashier login...");
        testAuthentication(userDAO, "cashier", "cashier");
        
        // Test with wrong password
        System.out.println("\nTesting wrong password...");
        testAuthentication(userDAO, "admin", "wrongpassword");
        
        // Test with non-existent user
        System.out.println("\nTesting non-existent user...");
        testAuthentication(userDAO, "nonexistent", "password");
        
        // Close database connection
        dbConn.closePool();
    }
    
    private static void testAuthentication(UserDAO userDAO, String username, String password) {
        System.out.println("Attempting to authenticate user: " + username);
        
        // Get user from database
        Optional<User> userOpt = userDAO.getUserByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("User found in database:");
            System.out.println("  Username: " + user.getUsername());
            System.out.println("  Role: " + user.getRole());
            System.out.println("  Active: " + user.isActive());
            System.out.println("  Password Hash: " + user.getPasswordHash());
            
            // Test password verification
            boolean verified = PasswordUtils.verifyPassword(password, user.getPasswordHash());
            System.out.println("Password verification result: " + verified);
            
            if (!verified) {
                // Test hash generation for debugging
                String newHash = PasswordUtils.hashPassword(password);
                System.out.println("Debug - New hash for '" + password + "': " + newHash);
                System.out.println("Debug - Stored hash: " + user.getPasswordHash());
            }
        } else {
            System.out.println("User not found in database");
        }
        System.out.println("Authentication test complete for: " + username);
        System.out.println("----------------------------------------");
    }
} 