package com.pos;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pos.ui.SplashScreen;
import com.pos.db.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        // Set up a global exception handler
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Uncaught exception", e);
            JOptionPane.showMessageDialog(null, "A fatal error occurred: " + e.getMessage(), "Fatal Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        });

        // Setup the look and feel
        try {
            // Try to use FlatLaf if available
            Class.forName("com.formdev.flatlaf.FlatLightLaf");
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
            Logger.getLogger(Main.class.getName()).info("Using FlatLaf look and feel");
        } catch (ClassNotFoundException e) {
            Logger.getLogger(Main.class.getName()).info("FlatLaf not available, using system look and feel");
            setSystemLookAndFeel();
        } catch (IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            Logger.getLogger(Main.class.getName()).warning("Error setting FlatLaf, using system look and feel");
            setSystemLookAndFeel();
        }
        
        // Check database connectivity before launching UI
        if (!checkDatabaseConnection()) {
            JOptionPane.showMessageDialog(null, 
                "Cannot connect to the database. Please check your configuration.", 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Start with splash screen
        SwingUtilities.invokeLater(() -> {
            new SplashScreen().setVisible(true);
        });
    }

    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            Logger.getLogger(Main.class.getName()).log(Level.WARNING, "Error setting system look and feel", e);
        }
    }

    private static boolean checkDatabaseConnection() {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Database connection error", e);
            return false;
        }
    }
} 