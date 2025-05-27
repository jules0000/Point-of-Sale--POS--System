package com.pos.util;

import com.pos.db.DatabaseConnection;
import com.pos.db.UserDAO;
import com.pos.model.User;
import java.sql.Connection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseTest {
    private static final Logger LOGGER = Logger.getLogger(DatabaseTest.class.getName());
    
    public static void main(String[] args) {
        try {
            // Test database connection
            LOGGER.info("Testing database connection...");
            Connection conn = DatabaseConnection.getInstance().getConnection();
            LOGGER.info("Database connection successful!");
            
            // Test user authentication
            UserDAO userDAO = new UserDAO();
            
            // Test admin login
            LOGGER.info("\nTesting admin login...");
            boolean adminAuth = userDAO.authenticateUser("admin", "admin123");
            LOGGER.info("Admin authentication result: " + adminAuth);
            
            // Test cashier login
            LOGGER.info("\nTesting cashier login...");
            boolean cashierAuth = userDAO.authenticateUser("cashier", "cashier123");
            LOGGER.info("Cashier authentication result: " + cashierAuth);
            
            // Print user details
            LOGGER.info("\nFetching user details...");
            Optional<User> adminUser = userDAO.getUserByUsername("admin");
            Optional<User> cashierUser = userDAO.getUserByUsername("cashier");
            
            if (adminUser.isPresent()) {
                LOGGER.info("Admin user found:");
                LOGGER.info("Username: " + adminUser.get().getUsername());
                LOGGER.info("Role: " + adminUser.get().getRole());
                LOGGER.info("Active: " + adminUser.get().isActive());
            } else {
                LOGGER.info("Admin user not found!");
            }
            
            if (cashierUser.isPresent()) {
                LOGGER.info("Cashier user found:");
                LOGGER.info("Username: " + cashierUser.get().getUsername());
                LOGGER.info("Role: " + cashierUser.get().getRole());
                LOGGER.info("Active: " + cashierUser.get().isActive());
            } else {
                LOGGER.info("Cashier user not found!");
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during database test: " + e.getMessage(), e);
        }
    }
} 