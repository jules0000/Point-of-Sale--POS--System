package com.pos.util;

import java.io.*;
import java.sql.*;
import java.util.logging.*;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import com.pos.db.DatabaseConnection;

public class PrinterUtil {
    private static final Logger LOGGER = Logger.getLogger(PrinterUtil.class.getName());
    
    // ESC/POS Commands
    private static final byte[] ESC_INIT = {0x1B, 0x40}; // Initialize printer
    private static final byte[] LF = {0x0A}; // Line feed
    private static final byte[] CR = {0x0D}; // Carriage return
    private static final byte[] CUT_PAPER = {0x1D, 0x56, 0x41}; // Full cut
    private static final byte[] BOLD_ON = {0x1B, 0x45, 0x01}; // Bold on
    private static final byte[] BOLD_OFF = {0x1B, 0x45, 0x00}; // Bold off
    private static final byte[] DOUBLE_WIDTH = {0x1B, 0x21, 0x10}; // Double width
    private static final byte[] NORMAL_WIDTH = {0x1B, 0x21, 0x00}; // Normal width
    private static final byte[] ALIGN_LEFT = {0x1B, 0x61, 0x00}; // Left alignment
    private static final byte[] ALIGN_CENTER = {0x1B, 0x61, 0x01}; // Center alignment
    private static final byte[] ALIGN_RIGHT = {0x1B, 0x61, 0x02}; // Right alignment
    private static final byte[] DRAWER_KICK = {0x1B, 0x70, 0x00, 0x19, (byte)0xFA}; // Open cash drawer
    
    private String printerName;
    private int charsPerLine;
    
    public PrinterUtil() {
        loadSettings();
    }
    
    private void loadSettings() {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT setting_key, setting_value FROM settings WHERE setting_key IN (?, ?)")) {
            
            stmt.setString(1, "printer_name");
            stmt.setString(2, "printer_char_per_line");
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String key = rs.getString("setting_key");
                String value = rs.getString("setting_value");
                
                if ("printer_name".equals(key)) {
                    printerName = value;
                } else if ("printer_char_per_line".equals(key)) {
                    charsPerLine = Integer.parseInt(value);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading printer settings", e);
            printerName = "POS-80"; // Default printer name
            charsPerLine = 42; // Default chars per line
        }
    }
    
    public void printReceipt(String content) throws PrintException {
        // Find the printer
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        PrintService printer = null;
        
        for (PrintService service : services) {
            if (service.getName().equalsIgnoreCase(printerName)) {
                printer = service;
                break;
            }
        }
        
        if (printer == null) {
            throw new PrintException("Printer '" + printerName + "' not found");
        }
        
        // Create print job
        DocPrintJob job = printer.createPrintJob();
        
        // Prepare content with ESC/POS commands
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        try {
            // Initialize printer
            data.write(ESC_INIT);
            
            // Split content into lines and format
            String[] lines = content.split("\n");
            for (String line : lines) {
                if (line.startsWith("HEADER:")) {
                    // Center-aligned, bold header
                    data.write(ALIGN_CENTER);
                    data.write(BOLD_ON);
                    data.write(line.substring(7).getBytes());
                    data.write(BOLD_OFF);
                    data.write(LF);
                } else if (line.startsWith("TOTAL:")) {
                    // Right-aligned total
                    data.write(ALIGN_RIGHT);
                    data.write(BOLD_ON);
                    data.write(line.substring(6).getBytes());
                    data.write(BOLD_OFF);
                    data.write(LF);
                } else {
                    // Normal left-aligned text
                    data.write(ALIGN_LEFT);
                    data.write(line.getBytes());
                    data.write(LF);
                }
            }
            
            // Cut paper
            data.write(CUT_PAPER);
            
            // Open cash drawer
            data.write(DRAWER_KICK);
            
        } catch (IOException e) {
            throw new PrintException(e.getMessage());
        }
        
        // Create print data
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        Doc doc = new SimpleDoc(data.toByteArray(), flavor, null);
        
        // Print
        job.print(doc, null);
    }
    
    public String centerText(String text) {
        if (text.length() >= charsPerLine) {
            return text;
        }
        
        int spaces = (charsPerLine - text.length()) / 2;
        return String.format("%" + spaces + "s%s%" + spaces + "s", "", text, "");
    }
    
    public String rightAlign(String text) {
        if (text.length() >= charsPerLine) {
            return text;
        }
        
        return String.format("%" + charsPerLine + "s", text);
    }
    
    public String formatAmount(double amount) {
        return String.format("₱%,.2f", amount);
    }
    
    public String formatLine(String label, String value) {
        int spaces = charsPerLine - label.length() - value.length();
        if (spaces < 1) {
            return label + value;
        }
        return label + String.format("%" + spaces + "s", "") + value;
    }
    
    public String repeatChar(char c, int count) {
        return new String(new char[count]).replace('\0', c);
    }
    
    public void openCashDrawer() throws PrintException {
        printReceipt(new String(DRAWER_KICK));
    }
} 