package com.pos.utils;

import com.pos.db.UserDAO;

public class AdminPasswordReset {
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();
        boolean success = userDAO.resetAdminPassword();
        
        if (success) {
            System.out.println("Admin password has been reset successfully.");
            System.out.println("You can now log in with:");
            System.out.println("Username: admin");
            System.out.println("Password: admin");
        } else {
            System.err.println("Failed to reset admin password. Please check the logs for details.");
        }
    }
} 