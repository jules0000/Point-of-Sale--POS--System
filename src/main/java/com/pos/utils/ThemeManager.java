package com.pos.utils;

import com.formdev.flatlaf.*;
import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

// Requires FlatLaf library (com.formdev:flatlaf)
public class ThemeManager {
    private static ThemeManager instance;
    private String currentTheme;
    private Consumer<String> onThemeChanged;

    private ThemeManager() {
        ConfigLoader config = ConfigLoader.getInstance();
        this.currentTheme = config.getUiTheme();
    }

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    public void initialize() {
        // Set system properties for better rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        // Apply the theme
        applyTheme(currentTheme);
    }

    public void setTheme(String theme) {
        if (!theme.equals(currentTheme)) {
            applyTheme(theme);
            if (onThemeChanged != null) {
                onThemeChanged.accept(theme);
            }
        }
    }

    public void setOnThemeChanged(Consumer<String> callback) {
        this.onThemeChanged = callback;
    }

    public String getCurrentTheme() {
        return currentTheme;
    }

    public boolean isDarkTheme() {
        return "dark".equalsIgnoreCase(currentTheme);
    }

    private void applyTheme(String theme) {
        try {
            switch (theme.toLowerCase()) {
                case "dark":
                    FlatDarkLaf.setup();
                    break;
                case "light":
                    FlatLightLaf.setup();
                    break;
                case "intellij":
                    FlatIntelliJLaf.setup();
                    break;
                case "darcula":
                    FlatDarculaLaf.setup();
                    break;
                default:
                    FlatLightLaf.setup();
                    theme = "light";
            }
            
            currentTheme = theme;
            
            // Update all window decorations
            for (Window window : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
            }
            
            // Configure custom colors and fonts
            configureCustomUI();
            
        } catch (Exception e) {
            System.err.println("Failed to apply theme: " + theme);
            e.printStackTrace();
        }
    }

    private void configureCustomUI() {
        ConfigLoader config = ConfigLoader.getInstance();
        int fontSize = config.getUiFontSize();
        
        // Set default font size
        Font defaultFont = new Font(Font.SANS_SERIF, Font.PLAIN, fontSize);
        UIManager.put("defaultFont", defaultFont);
        
        // Configure colors based on theme
        if (isDarkTheme()) {
            configureDarkThemeColors();
        } else {
            configureLightThemeColors();
        }
        
        // Configure common UI properties
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", true);
        UIManager.put("TextField.arc", 8);
        UIManager.put("Button.arc", 8);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("ScrollBar.thumbArc", 8);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
    }

    private void configureDarkThemeColors() {
        // Primary colors
        Color primary = new Color(82, 145, 255);
        Color primaryHover = new Color(98, 159, 255);
        Color primaryPressed = new Color(66, 133, 255);
        
        // Accent colors
        Color accent = new Color(255, 64, 129);
        Color success = new Color(76, 175, 80);
        Color warning = new Color(255, 152, 0);
        Color error = new Color(244, 67, 54);
        
        // Background colors
        Color background = new Color(30, 30, 30);
        Color surface = new Color(37, 37, 37);
        Color hover = new Color(45, 45, 45);
        
        // Configure UI properties
        UIManager.put("Button.default.background", primary);
        UIManager.put("Button.default.foreground", Color.WHITE);
        UIManager.put("Button.default.hoverBackground", primaryHover);
        UIManager.put("Button.default.pressedBackground", primaryPressed);
        
        UIManager.put("Component.accentColor", accent);
        UIManager.put("Component.errorFocusColor", error);
        UIManager.put("Component.warningFocusColor", warning);
        
        UIManager.put("Panel.background", background);
        UIManager.put("Table.background", surface);
        UIManager.put("Table.alternateRowColor", hover);
    }

    private void configureLightThemeColors() {
        // Primary colors
        Color primary = new Color(33, 150, 243);
        Color primaryHover = new Color(66, 165, 245);
        Color primaryPressed = new Color(25, 118, 210);
        
        // Accent colors
        Color accent = new Color(233, 30, 99);
        Color success = new Color(67, 160, 71);
        Color warning = new Color(251, 140, 0);
        Color error = new Color(229, 57, 53);
        
        // Background colors
        Color background = new Color(250, 250, 250);
        Color surface = new Color(255, 255, 255);
        Color hover = new Color(245, 245, 245);
        
        // Configure UI properties
        UIManager.put("Button.default.background", primary);
        UIManager.put("Button.default.foreground", Color.WHITE);
        UIManager.put("Button.default.hoverBackground", primaryHover);
        UIManager.put("Button.default.pressedBackground", primaryPressed);
        
        UIManager.put("Component.accentColor", accent);
        UIManager.put("Component.errorFocusColor", error);
        UIManager.put("Component.warningFocusColor", warning);
        
        UIManager.put("Panel.background", background);
        UIManager.put("Table.background", surface);
        UIManager.put("Table.alternateRowColor", hover);
    }
} 