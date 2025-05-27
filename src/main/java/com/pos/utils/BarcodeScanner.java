package com.pos.utils;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Handles barcode scanning input through keyboard emulation
 */
public class BarcodeScanner {
    private static final Logger LOGGER = Logger.getLogger(BarcodeScanner.class.getName());
    private static final long SCAN_TIMEOUT = 100; // milliseconds between keystrokes
    private static final int MIN_BARCODE_LENGTH = 8;
    private StringBuilder buffer;
    private long lastKeyTime;
    private final Consumer<String> onBarcodeScanned;
    private final AtomicBoolean enabled;
    private final JTextField targetField;

    public BarcodeScanner(JTextField targetField, Consumer<String> onBarcodeScanned) {
        this.targetField = targetField;
        this.onBarcodeScanned = onBarcodeScanned;
        this.buffer = new StringBuilder();
        this.enabled = new AtomicBoolean(true);
        
        setupKeyListener();
    }

    private void setupKeyListener() {
        targetField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!enabled.get()) return;
                
                long currentTime = System.currentTimeMillis();
                char keyChar = e.getKeyChar();

                // If it's been too long since the last keystroke, clear the buffer
                if (currentTime - lastKeyTime > SCAN_TIMEOUT && buffer.length() > 0) {
                    buffer = new StringBuilder();
                }

                // Update the last key time
                lastKeyTime = currentTime;

                // Add character to buffer if it's a digit or letter
                if (Character.isLetterOrDigit(keyChar)) {
                    buffer.append(keyChar);
                }

                // Check if we have a complete barcode (terminated by Enter key)
                if (keyChar == KeyEvent.VK_ENTER && buffer.length() >= MIN_BARCODE_LENGTH) {
                    String barcode = buffer.toString();
                    buffer = new StringBuilder();
                    
                    // Clear the text field
                    SwingUtilities.invokeLater(() -> {
                        targetField.setText("");
                        onBarcodeScanned.accept(barcode);
                    });
                    
                    // Consume the event to prevent it from being processed further
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // Not used
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // Not used
            }
        });
    }

    public void enable() {
        enabled.set(true);
    }

    public void disable() {
        enabled.set(false);
        buffer = new StringBuilder();
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    public void clear() {
        buffer = new StringBuilder();
    }

    public static boolean isValidBarcode(String barcode) {
        if (barcode == null || barcode.length() < MIN_BARCODE_LENGTH) {
            return false;
        }

        // Check if barcode contains only digits and letters
        return barcode.matches("^[A-Za-z0-9]+$");
    }

    public static String formatBarcode(String barcode) {
        if (!isValidBarcode(barcode)) {
            return null;
        }
        return barcode.toUpperCase();
    }
} 