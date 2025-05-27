package com.pos.utils;

import com.pos.dao.PrinterConfigDAO;
import com.pos.dao.ReceiptDesignDAO;
import com.pos.model.PrinterConfig;
import com.pos.model.ReceiptDesign;

public class ReceiptGenerator {
    private final PrinterConfigDAO printerConfigDAO;
    private final ReceiptDesignDAO receiptDesignDAO;
    
    public ReceiptGenerator() {
        this.printerConfigDAO = new PrinterConfigDAO();
        this.receiptDesignDAO = new ReceiptDesignDAO();
    }
    
    public void printReceipt(String cashierName, String[][] items, double subtotal, double tax, double total, double tendered, double change, int pointsEarned, String receiptNumber) {
        try {
            PrinterConfig printerConfig = printerConfigDAO.getCurrentConfig();
            ReceiptDesign receiptDesign = receiptDesignDAO.getCurrentDesign();
            
            if (printerConfig == null || receiptDesign == null) {
                throw new Exception("Printer configuration or receipt design not found");
            }
            
            ReceiptPrinter printer = new ReceiptPrinter(
                printerConfig,
                receiptDesign,
                cashierName,
                items,
                subtotal,
                tax,
                total,
                tendered,
                change,
                pointsEarned,
                receiptNumber
            );
            printer.print();
        } catch (Exception e) {
            throw new RuntimeException("Error printing receipt: " + e.getMessage(), e);
        }
    }
} 