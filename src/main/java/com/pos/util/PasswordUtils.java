package com.pos.util;

import org.mindrot.jbcrypt.BCrypt;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for password hashing and verification using BCrypt
 */
public class PasswordUtils {
    private static final Logger LOGGER = Logger.getLogger(PasswordUtils.class.getName());
    
    /**
     * Hash a password using BCrypt
     * 
     * @param password The password to hash
     * @return The hashed password
     */
    public static String hashPassword(String password) {
        try {
            return BCrypt.hashpw(password, BCrypt.gensalt(12));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error hashing password: " + e.getMessage(), e);
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Verify a password against a hash
     * 
     * @param password The password to verify
     * @param hash The hash to verify against
     * @return true if the password matches the hash
     */
    public static boolean verifyPassword(String password, String hash) {
        try {
            if (hash == null || hash.isEmpty()) {
                return false;
            }
            return BCrypt.checkpw(password, hash);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error verifying password: " + e.getMessage(), e);
            return false;
        }
    }

    // Test method to generate hashes
    public static void main(String[] args) {
        String adminPass = "admin123";
        String cashierPass = "cashier123";
        
        String adminHash = hashPassword(adminPass);
        String cashierHash = hashPassword(cashierPass);
        
        System.out.println("Admin password hash: " + adminHash);
        System.out.println("Cashier password hash: " + cashierHash);
        
        // Verify the hashes
        System.out.println("\nVerifying hashes:");
        System.out.println("Admin password verification: " + verifyPassword(adminPass, adminHash));
        System.out.println("Cashier password verification: " + verifyPassword(cashierPass, cashierHash));
    }
} 