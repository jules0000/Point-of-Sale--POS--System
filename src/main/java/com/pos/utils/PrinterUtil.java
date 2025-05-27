package com.pos.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;

/**
 * Utility class for handling thermal printer operations and cash drawer commands
 */
public class PrinterUtil {
    
    // ESC/POS command for opening cash drawer
    private static final byte[] OPEN_DRAWER_COMMAND = {0x1B, 0x70, 0x00, 0x19, (byte) 0xFA};
    
    private PrinterUtil() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Get list of available printers
     * @return Array of printer names
     */
    public static String[] getAvailablePrinters() {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        String[] printerNames = new String[printServices.length];
        for (int i = 0; i < printServices.length; i++) {
            printerNames[i] = printServices[i].getName();
        }
        return printerNames;
    }
    
    /**
     * Get default printer name
     * @return Default printer name or null if no default printer
     */
    public static String getDefaultPrinterName() {
        PrintService defaultPrinter = PrintServiceLookup.lookupDefaultPrintService();
        return defaultPrinter != null ? defaultPrinter.getName() : null;
    }
    
    /**
     * Find printer service by name
     * @param printerName Name of the printer to find
     * @return PrintService or null if not found
     */
    public static PrintService findPrintService(String printerName) {
        if (printerName == null || printerName.trim().isEmpty()) {
            return PrintServiceLookup.lookupDefaultPrintService();
        }
        
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        PrintService selectedService = null;
        
        // First try exact match
        for (PrintService service : printServices) {
            if (service.getName().equalsIgnoreCase(printerName)) {
                selectedService = service;
                break;
            }
        }
        
        // If no exact match, try partial match
        if (selectedService == null) {
            for (PrintService service : printServices) {
                if (service.getName().toLowerCase().contains(printerName.toLowerCase())) {
                    selectedService = service;
                    break;
                }
            }
        }
        
        // If still no match, try to find a printer with similar name
        if (selectedService == null) {
            String simplifiedName = printerName.toLowerCase().replaceAll("[^a-z0-9]", "");
            for (PrintService service : printServices) {
                String serviceName = service.getName().toLowerCase().replaceAll("[^a-z0-9]", "");
                if (serviceName.contains(simplifiedName) || simplifiedName.contains(serviceName)) {
                    selectedService = service;
                    break;
                }
            }
        }
        
        return selectedService;
    }
    
    /**
     * Create print request attributes
     * @param paperSize Paper size (e.g. "Thermal 80mm", "Thermal 58mm")
     * @param copies Number of copies
     * @return PrintRequestAttributeSet with configured attributes
     */
    public static PrintRequestAttributeSet createPrintAttributes(String paperSize, int copies) {
        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
        
        // Set paper size
        if ("Thermal 80mm".equals(paperSize)) {
            attributes.add(new MediaSize(80, 200, MediaSize.MM));
        } else if ("Thermal 58mm".equals(paperSize)) {
            attributes.add(new MediaSize(58, 200, MediaSize.MM));
        } else {
            attributes.add(MediaSizeName.ISO_A4); // Default to A4
        }
        
        // Set orientation and copies
        attributes.add(OrientationRequested.PORTRAIT);
        attributes.add(new Copies(copies));
        
        return attributes;
    }
    
    /**
     * Print raw text to printer
     * @param printerName Name of the printer
     * @param text Text to print
     * @param paperSize Paper size
     * @param copies Number of copies
     * @throws Exception if printing fails
     */
    public static void printRawText(String printerName, String text, String paperSize, int copies) throws Exception {
        PrintService printService = findPrintService(printerName);
        if (printService == null) {
            throw new Exception("Printer not found: " + printerName + ". Available printers: " + 
                String.join(", ", getAvailablePrinters()));
        }
        
        try {
            DocPrintJob job = printService.createPrintJob();
            DocFlavor flavor = DocFlavor.STRING.TEXT_PLAIN;
            Doc doc = new SimpleDoc(text, flavor, null);
            
            PrintRequestAttributeSet attributes = createPrintAttributes(paperSize, copies);
            
            // Add additional attributes for better compatibility
            attributes.add(new javax.print.attribute.standard.MediaPrintableArea(0, 0, 80, 200, javax.print.attribute.standard.MediaPrintableArea.MM));
            
            // Set print quality and other attributes
            attributes.add(javax.print.attribute.standard.PrintQuality.HIGH);
            attributes.add(javax.print.attribute.standard.Sides.ONE_SIDED);
            
            job.print(doc, attributes);
        } catch (Exception e) {
            throw new Exception("Failed to print: " + e.getMessage() + 
                "\nPrinter: " + printerName +
                "\nAvailable printers: " + String.join(", ", getAvailablePrinters()));
        }
    }
    
    /**
     * Open cash drawer
     * @param printerName Name of the printer
     * @throws Exception if operation fails
     */
    public static void openCashDrawer(String printerName) throws Exception {
        PrintService printService = findPrintService(printerName);
        if (printService == null) {
            throw new Exception("Printer not found: " + printerName);
        }
        
        DocPrintJob job = printService.createPrintJob();
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        Doc doc = new SimpleDoc(OPEN_DRAWER_COMMAND, flavor, null);
        
        try {
            job.print(doc, null);
        } catch (Exception e) {
            throw new Exception("Failed to open cash drawer: " + e.getMessage());
        }
    }
    
    /**
     * Print a receipt for a sale
     * 
     * @param saleId Sale ID
     * @param dateTime Date and time of sale
     * @param cashierName Name of the cashier
     * @param storeName Name of the store
     * @param items Array of item names
     * @param quantities Array of quantities
     * @param prices Array of prices
     * @param subtotal Subtotal amount
     * @param tax Tax amount
     * @param total Total amount
     * @param paymentMethod Payment method used
     * @param amountPaid Amount paid by customer
     * @param change Change given to customer
     * @param printService Printer service to use (null for default)
     * @return true if success, false otherwise
     */
    public static boolean printReceipt(int saleId, Date dateTime, String cashierName, String storeName,
                                      String[] items, int[] quantities, BigDecimal[] prices, 
                                      BigDecimal subtotal, BigDecimal tax, BigDecimal total,
                                      String paymentMethod, BigDecimal amountPaid, BigDecimal change,
                                      PrintService printService) {
        try {
            StringBuilder receipt = new StringBuilder();
            
            // Format header
            receipt.append(centerText(storeName, 40)).append("\n");
            receipt.append(centerText("Receipt", 40)).append("\n");
            receipt.append(repeatChar('-', 40)).append("\n");
            
            // Sale details
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            receipt.append("Date: ").append(dateFormat.format(dateTime)).append("\n");
            receipt.append("Sale #: ").append(saleId).append("\n");
            receipt.append("Cashier: ").append(cashierName).append("\n");
            receipt.append(repeatChar('-', 40)).append("\n");
            
            // Items
            receipt.append(String.format("%-20s %5s %7s %7s\n", "Item", "Qty", "Price", "Total"));
            receipt.append(repeatChar('-', 40)).append("\n");
            
            for (int i = 0; i < items.length; i++) {
                String name = truncateText(items[i], 20);
                BigDecimal itemTotal = prices[i].multiply(new BigDecimal(quantities[i]));
                receipt.append(String.format("%-20s %5d %7.2f %7.2f\n", 
                        name, quantities[i], prices[i], itemTotal));
            }
            
            receipt.append(repeatChar('-', 40)).append("\n");
            
            // Totals
            receipt.append(String.format("%-33s %7.2f\n", "Subtotal:", subtotal));
            receipt.append(String.format("%-33s %7.2f\n", "Tax:", tax));
            receipt.append(String.format("%-33s %7.2f\n", "TOTAL:", total));
            receipt.append(repeatChar('=', 40)).append("\n");
            
            // Payment info
            receipt.append("Payment Method: ").append(paymentMethod).append("\n");
            receipt.append(String.format("%-33s %7.2f\n", "Amount Paid:", amountPaid));
            receipt.append(String.format("%-33s %7.2f\n", "Change:", change));
            
            // Footer
            receipt.append(repeatChar('-', 40)).append("\n");
            receipt.append(centerText("Thank you for your purchase!", 40)).append("\n");
            receipt.append(centerText("Please come again", 40)).append("\n");
            
            // Add feed to ensure complete printing
            receipt.append("\n\n\n\n\n");
            
            // Print the receipt
            return printText(receipt.toString(), printService);
            
        } catch (Exception e) {
            System.err.println("Error printing receipt: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Print text to the printer
     * 
     * @param text Text to print
     * @param printService Printer service to use (null for default)
     * @return true if success, false otherwise
     */
    public static boolean printText(String text, PrintService printService) {
        try {
            if (printService == null) {
                PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
                if (services.length > 0) {
                    printService = services[0];
                } else {
                    return false;
                }
            }
            
            DocPrintJob job = printService.createPrintJob();
            Doc doc = new SimpleDoc(text, DocFlavor.CHAR_ARRAY.TEXT_PLAIN, null);
            PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
            job.print(doc, attributes);
            
            return true;
        } catch (Exception e) {
            System.err.println("Error printing text: " + e.getMessage());
            return false;
        }
    }
    
    // Utility methods for formatting text
    
    private static String centerText(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        int leftPadding = (width - text.length()) / 2;
        int rightPadding = width - text.length() - leftPadding;
        return repeatChar(' ', leftPadding) + text + repeatChar(' ', rightPadding);
    }
    
    private static String repeatChar(char c, int count) {
        return new String(new char[count]).replace('\0', c);
    }
    
    private static String truncateText(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
} 