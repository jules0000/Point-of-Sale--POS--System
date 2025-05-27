package com.pos.utils;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;

import com.pos.model.PrinterConfig;
import com.pos.model.ReceiptDesign;

public class ReceiptPrinter implements Printable {
    private final PrinterConfig printerConfig;
    private final ReceiptDesign receiptDesign;
    private final String cashierName;
    private final String[][] items;
    private final double subtotal;
    private final double tax;
    private final double total;
    private final String receiptNumber;
    private final double tendered;
    private final double change;
    private final int pointsEarned;
    private final String customerName;
    private final Integer customerPointsAfter;

    public ReceiptPrinter(PrinterConfig printerConfig, ReceiptDesign receiptDesign,
                          String cashierName, String[][] items, double subtotal,
                          double tax, double total, double tendered, double change,
                          int pointsEarned, String receiptNumber) {
        this(printerConfig, receiptDesign, cashierName, items, subtotal, tax, total, tendered, change, pointsEarned, receiptNumber, null, null);
    }

    public ReceiptPrinter(PrinterConfig printerConfig, ReceiptDesign receiptDesign,
                          String cashierName, String[][] items, double subtotal,
                          double tax, double total, double tendered, double change,
                          int pointsEarned, String receiptNumber,
                          String customerName, Integer customerPointsAfter) {
        this.printerConfig = printerConfig;
        this.receiptDesign = receiptDesign;
        this.cashierName = cashierName;
        this.items = items;
        this.subtotal = subtotal;
        this.tax = tax;
        this.total = total;
        this.tendered = tendered;
        this.change = change;
        this.pointsEarned = pointsEarned;
        this.receiptNumber = receiptNumber;
        this.customerName = customerName;
        this.customerPointsAfter = customerPointsAfter;
    }

    public void print() throws PrintException {
        try {
        // Validate printerConfig and receiptDesign before proceeding
            if (printerConfig == null) throw new PrintException("Printer settings not configured. Please configure printer settings in System Settings.");
            if (receiptDesign == null) throw new PrintException("Receipt design not configured. Please configure receipt design in System Settings.");
        if (printerConfig.getPrinterName() == null || printerConfig.getPrinterName().trim().isEmpty())
                throw new PrintException("Printer name is not set in settings. Please select a printer in System Settings.");
        if (printerConfig.getPaperSize() == null || printerConfig.getPaperSize().trim().isEmpty())
                throw new PrintException("Paper size is not set in settings. Please select a paper size in System Settings.");
            if (printerConfig.getCopies() <= 0) throw new PrintException("Copies must be at least 1. Please check printer settings.");
        if (printerConfig.getPaperType() == null || printerConfig.getPaperType().trim().isEmpty())
                throw new PrintException("Paper type is not set in settings. Please select a paper type in System Settings.");
        if (receiptDesign.getFontFamily() == null || receiptDesign.getFontFamily().trim().isEmpty())
                throw new PrintException("Font family is not set in receipt design. Please select a font in System Settings.");
            if (receiptDesign.getFontSize() <= 0) throw new PrintException("Font size must be greater than 0. Please check receipt design settings.");

            // Validate items array
            if (items == null || items.length == 0) {
                throw new PrintException("No items to print. Please check receipt content.");
            }

            // Check if printer is available
        PrintService printService = findPrintService(printerConfig.getPrinterName());
        if (printService == null) {
                String availablePrinters = String.join(", ", Arrays.stream(PrintServiceLookup.lookupPrintServices(null, null))
                    .map(PrintService::getName)
                    .toArray(String[]::new));
                throw new PrintException("Printer not found: " + printerConfig.getPrinterName() + 
                    "\nAvailable printers: " + availablePrinters);
            }

            System.out.println("Using printer: " + printService.getName());
            System.out.println("Paper size: " + printerConfig.getPaperSize());
            System.out.println("Copies: " + printerConfig.getCopies());

            // Try raw text printing first
            try {
                System.out.println("Attempting raw text printing...");
                printRawText(printService);
                System.out.println("Raw text printing completed successfully");
                return;
            } catch (Exception e) {
                System.out.println("Raw text printing failed, falling back to graphics printing: " + e.getMessage());
                if (e.getCause() != null) {
                    System.out.println("Cause: " + e.getCause().getMessage());
                }
            }

            // Create and configure print job
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintService(printService);
            PageFormat pf = job.defaultPage();
            java.awt.print.Paper paper = pf.getPaper();

            // Set paper dimensions based on paper size
            double width = 164.41; // Default to 58mm
            double height = 566.9; // Default height

            if ("Thermal 80mm".equals(printerConfig.getPaperSize())) {
                width = 227; // 80mm in points
            }

            // Calculate required height based on content
            int numLines = 10; // Base lines
                if (receiptDesign.getHeaderText() != null) numLines += 2;
                if (receiptDesign.isShowDateTime()) numLines++;
                if (receiptDesign.isShowCashierName()) numLines++;
                if (receiptDesign.isShowTaxDetails()) numLines++;
            numLines += items.length + 5; // Items + totals
                if (receiptDesign.getFooterText() != null) numLines++;
                double lineHeight = 18;
                height = Math.max(numLines * lineHeight, 300);

            // Ensure height > width
            if (height <= width) {
                height = width * 3;
            }

            System.out.println("Paper dimensions - Width: " + width + ", Height: " + height);

            // Set paper dimensions and margins
            paper.setSize(width, height);
            paper.setImageableArea(0, 0, width, height);
            pf.setPaper(paper);
            pf.setOrientation(PageFormat.PORTRAIT);

            // Set print job attributes
            PrintRequestAttributeSet attributes = createPageAttributes();
            job.setPrintable(this, pf);

            // Attempt to print
            try {
                System.out.println("Starting graphics print job...");
                job.print(attributes);
                System.out.println("Graphics print job completed successfully");
            } catch (PrinterException e) {
                System.err.println("PrinterException details: " + e.getMessage());
                if (e.getCause() != null) {
                    System.err.println("Cause: " + e.getCause().getMessage());
                }
                e.printStackTrace();
                throw new PrintException("Failed to print: " + e.getMessage() + 
                    "\nPrinter: " + printService.getName() +
                    "\nPaper size: " + printerConfig.getPaperSize() +
                    "\nPlease check printer connection and paper.");
            }

        } catch (PrintException e) {
            throw e; // Re-throw PrintException as is
        } catch (ClassCastException e) {
            System.err.println("ClassCastException details: " + e.getMessage());
            e.printStackTrace();
            throw new PrintException("Type conversion error during printing: " + e.getMessage() + 
                "\nPrinter: " + (printerConfig != null ? printerConfig.getPrinterName() : "unknown") +
                "\nPlease check printer settings and try again.");
        } catch (Exception e) {
            System.err.println("Unexpected error details: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getMessage());
            }
            e.printStackTrace();
            throw new PrintException("Unexpected error during printing: " + e.getMessage() + 
                "\nPrinter: " + (printerConfig != null ? printerConfig.getPrinterName() : "unknown") +
                "\nError type: " + e.getClass().getName() +
                "\nStack trace: " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void printRawText(PrintService printService) throws PrintException {
        try {
            StringBuilder content = new StringBuilder();
            // Header (centered)
            content.append(centerText(receiptDesign.getHeaderText(), 32)).append("\n");
            content.append(centerText(receiptDesign.getLocation(), 32)).append("\n");
            content.append(centerText(receiptDesign.getContactNumber(), 32)).append("\n\n");
            // Date, cashier, receipt number
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            content.append("Date: ").append(sdf.format(new Date())).append("\n");
            content.append("Cashier: ").append(cashierName).append("\n");
            content.append("Receipt #: ").append(receiptNumber).append("\n");
            // If customer is registered, print name and points
            if (customerName != null && !customerName.trim().isEmpty() && customerPointsAfter != null) {
                content.append("Customer: ").append(customerName).append("\n");
                content.append("Points: ").append(customerPointsAfter).append("\n");
            }
            content.append("-".repeat(32)).append("\n");
            // Table header
            content.append(String.format("%-10s %6s %10s\n", "Item", "Qty", "Price"));
            // Only one separator line before items
            // Items
            for (String[] item : items) {
                try {
                    System.out.println("DEBUG ITEM: " + java.util.Arrays.toString(item));
                    String itemName = item.length > 0 ? item[0] : "";
                    String quantity = item.length > 1 ? item[1] : "1";
                    String price = item.length > 2 ? item[2] : "0.00";
                    double priceVal = 0;
                    try { priceVal = Double.parseDouble(price); } catch (Exception ignore) {}
                    content.append(String.format("%-10s %6s %10s\n",
                        itemName,
                        quantity,
                        String.format("%.2f", priceVal)));
                } catch (Exception e) {
                    content.append((item.length > 0 ? item[0] : "") + "\n");
                }
            }
            content.append("-".repeat(32)).append("\n");
            // Totals section (right-aligned)
            content.append(padLeft("Subtotal:", 22) + padLeft(String.format("%.2f", subtotal), 10) + "\n");
            content.append(padLeft("Tax (10%):", 22) + padLeft(String.format("%.2f", tax), 10) + "\n");
            content.append(padLeft("TOTAL:", 22) + padLeft(String.format("%.2f", total), 10) + "\n");
            content.append("\n");
            content.append(padLeft("Tendered:", 22) + padLeft(String.format("%.2f", tendered), 10) + "\n");
            content.append(padLeft("Change:", 22) + padLeft(String.format("%.2f", change), 10) + "\n");
            content.append(padLeft("Points Earned:", 22) + padLeft(String.valueOf(pointsEarned), 10) + "\n");
            content.append("\n");
            // Footer (centered)
            content.append(centerText("Thank you for shopping with us!", 32)).append("\n");
            // Paper cut
            content.append("\n\n\n");
            content.append((char) 29).append("V").append((char) 65);
            // Print job
            DocPrintJob job = printService.createPrintJob();
            byte[] printData = content.toString().getBytes("UTF-8");
            Doc doc = new SimpleDoc(printData, DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
            job.print(doc, null);
        } catch (Exception e) {
            throw new PrintException("Raw text printing failed: " + e.getMessage());
        }
    }

    // Helper for centering text
    private String centerText(String text, int width) {
        int pad = (width - text.length()) / 2;
        if (pad < 0) pad = 0;
        return " ".repeat(pad) + text;
    }
    // Helper for right-align
    private String padLeft(String text, int width) {
        return String.format("%" + width + "s", text);
    }

    private PrintService findPrintService(String printerName) {
        if (printerName == null || printerName.trim().isEmpty()) {
            PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
            System.out.println("Using default printer: " + (defaultService != null ? defaultService.getName() : "none"));
            return defaultService;
        }

        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        System.out.println("Available printers:");
        for (PrintService service : printServices) {
            System.out.println("- " + service.getName());
        }

        PrintService selectedService = null;
        
        // First try exact match
        for (PrintService service : printServices) {
            if (service.getName().equalsIgnoreCase(printerName)) {
                selectedService = service;
                System.out.println("Found exact match: " + service.getName());
                break;
            }
        }
        
        // If no exact match, try partial match
        if (selectedService == null) {
            for (PrintService service : printServices) {
                if (service.getName().toLowerCase().contains(printerName.toLowerCase())) {
                    selectedService = service;
                    System.out.println("Found partial match: " + service.getName());
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
                    System.out.println("Found similar name match: " + service.getName());
                    break;
                }
            }
        }

        // If still no match, try to find any thermal printer
        if (selectedService == null) {
            for (PrintService service : printServices) {
                String name = service.getName().toLowerCase();
                if (name.contains("thermal") || name.contains("pos") || name.contains("receipt")) {
                    selectedService = service;
                    System.out.println("Found thermal/POS printer: " + service.getName());
                    break;
                }
            }
        }

        if (selectedService == null) {
            System.out.println("No matching printer found for: " + printerName);
        }

        return selectedService;
    }

    private PrintRequestAttributeSet createPageAttributes() {
        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();

        try {
            // Set paper size
            if ("Thermal 80mm".equals(printerConfig.getPaperSize())) {
                attributes.add(new MediaSize(80, 200, MediaSize.MM));
            } else if ("Thermal 58mm".equals(printerConfig.getPaperSize())) {
                attributes.add(new MediaSize(58, 200, MediaSize.MM));
            } else {
                attributes.add(MediaSizeName.ISO_A4);
            }

            // Set orientation and copies
        attributes.add(OrientationRequested.PORTRAIT);
        attributes.add(new Copies(printerConfig.getCopies()));

            // Add additional attributes for better compatibility
            attributes.add(new javax.print.attribute.standard.MediaPrintableArea(0, 0, 58, 200, javax.print.attribute.standard.MediaPrintableArea.MM));
            attributes.add(javax.print.attribute.standard.PrintQuality.HIGH);
            attributes.add(javax.print.attribute.standard.Sides.ONE_SIDED);
        } catch (Exception e) {
            System.err.println("Error creating print attributes: " + e.getMessage());
            // Return basic attributes if there's an error
            PrintRequestAttributeSet basicAttributes = new HashPrintRequestAttributeSet();
            basicAttributes.add(OrientationRequested.PORTRAIT);
            basicAttributes.add(new Copies(printerConfig.getCopies()));
            return basicAttributes;
        }

        return attributes;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) return NO_SUCH_PAGE;
        try {
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        String fontFamily = receiptDesign.getFontFamily();
            if (fontFamily == null || fontFamily.trim().isEmpty()) fontFamily = "Arial";
        int fontSize = receiptDesign.getFontSize();
            if (fontSize <= 0) fontSize = 10;
        Font font = new Font(fontFamily, Font.PLAIN, fontSize);
            Font fontBold = new Font(fontFamily, Font.BOLD, fontSize);
            g2d.setFont(fontBold);
        int y = 20;
        int lineHeight = g2d.getFontMetrics().getHeight();
            double width = pageFormat.getImageableWidth();
            // Store name (bold, centered)
            drawCenteredText(g2d, receiptDesign.getHeaderText(), width, y); y += lineHeight;
            g2d.setFont(font);
            drawCenteredText(g2d, receiptDesign.getLocation(), width, y); y += lineHeight;
            drawCenteredText(g2d, receiptDesign.getContactNumber(), width, y); y += lineHeight * 2;
            // Date, cashier, receipt number
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            drawLeftAlignedText(g2d, "Date: " + sdf.format(new Date()), 0, y); y += lineHeight;
            drawLeftAlignedText(g2d, "Cashier: " + cashierName, 0, y); y += lineHeight;
            drawLeftAlignedText(g2d, "Receipt #: " + receiptNumber, 0, y); y += lineHeight;
            // If customer is registered, print name and points
            if (customerName != null && !customerName.trim().isEmpty() && customerPointsAfter != null) {
                drawLeftAlignedText(g2d, "Customer: " + customerName, 0, y); y += lineHeight;
                drawLeftAlignedText(g2d, "Points: " + customerPointsAfter, 0, y); y += lineHeight;
            }
            drawDashedLine(g2d, width, y); y += lineHeight;
            // Table header
            g2d.setFont(fontBold);
            drawLeftAlignedText(g2d, "Item", 0, y);
            drawLeftAlignedText(g2d, "Qty", (int) (width * 0.35), y);
            drawRightAlignedText(g2d, "Price", width, y); y += lineHeight;
            g2d.setFont(font);
            // Only one separator line before items
            // Items
        for (String[] item : items) {
                try {
                    System.out.println("DEBUG ITEM: " + java.util.Arrays.toString(item));
                    String itemName = item.length > 0 ? item[0] : "";
                    String quantity = item.length > 1 ? item[1] : "1";
                    String price = item.length > 2 ? item[2] : "0.00";
                    double priceVal = 0;
                    try { priceVal = Double.parseDouble(price); } catch (Exception ignore) {}
                    drawLeftAlignedText(g2d, itemName, 0, y);
                    drawLeftAlignedText(g2d, quantity, (int) (width * 0.35), y);
                    drawRightAlignedText(g2d, String.format("%.2f", priceVal), width, y); y += lineHeight;
                } catch (Exception e) {
                    drawLeftAlignedText(g2d, (item.length > 0 ? item[0] : ""), 0, y); y += lineHeight;
                }
            }
            drawDashedLine(g2d, width, y); y += lineHeight;
            // Totals section
            drawRightAlignedText(g2d, String.format("Subtotal:  %.2f", subtotal), width, y); y += lineHeight;
            drawRightAlignedText(g2d, String.format("Tax (10%): %.2f", tax), width, y); y += lineHeight;
            g2d.setFont(fontBold);
            drawRightAlignedText(g2d, String.format("TOTAL:     %.2f", total), width, y); y += lineHeight;
            g2d.setFont(font);
            y += lineHeight / 2;
            drawRightAlignedText(g2d, String.format("Tendered:  %.2f", tendered), width, y); y += lineHeight;
            drawRightAlignedText(g2d, String.format("Change:    %.2f", change), width, y); y += lineHeight;
            drawRightAlignedText(g2d, String.format("Points Earned: %d", pointsEarned), width, y); y += lineHeight * 2;
            // Footer
            drawCenteredText(g2d, "Thank you for shopping with us!", width, y);
            return PAGE_EXISTS;
        } catch (Exception e) {
            throw new PrinterException("Error rendering receipt: " + e.getMessage());
        }
    }

    private void drawCenteredText(Graphics2D g2d, String text, double width, int y) {
        FontMetrics metrics = g2d.getFontMetrics();
        int x = (int) (width - metrics.stringWidth(text)) / 2;
        g2d.drawString(text, x, y);
    }

    private void drawRightAlignedText(Graphics2D g2d, String text, double width, int y) {
        FontMetrics metrics = g2d.getFontMetrics();
        int x = (int) (width - metrics.stringWidth(text));
        g2d.drawString(text, x, y);
    }

    private void drawLeftAlignedText(Graphics2D g2d, String text, int x, int y) {
        g2d.drawString(text, x, y);
    }

    private void drawDashedLine(Graphics2D g2d, double width, int y) {
        int dashLength = 4;
        int gapLength = 4;
        int x = 0;
        while (x < width) {
            g2d.drawLine(x, y, x + dashLength, y);
            x += dashLength + gapLength;
        }
    }

    // Static method to always fetch latest settings and print
    public static void printReceipt(String cashierName, String[][] items, double subtotal, double tax, double total, 
                                  double tendered, double change, int pointsEarned, String receiptNumber) throws PrintException {
        PrinterConfig printerConfig = null;
        ReceiptDesign receiptDesign = null;
        try {
            com.pos.dao.PrinterConfigDAO printerConfigDAO = new com.pos.dao.PrinterConfigDAO();
            com.pos.dao.ReceiptDesignDAO receiptDesignDAO = new com.pos.dao.ReceiptDesignDAO();
            printerConfig = printerConfigDAO.getCurrentConfig();
            receiptDesign = receiptDesignDAO.getCurrentDesign();
        } catch (Exception e) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                javax.swing.JOptionPane.showMessageDialog(null, "Failed to load printer/receipt settings: " + e.getMessage(), "Print Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            });
            throw new PrintException("Failed to load printer/receipt settings: " + e.getMessage());
        }
        if (printerConfig == null) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                javax.swing.JOptionPane.showMessageDialog(null, "Printer settings not configured.", "Print Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            });
            throw new PrintException("Printer settings not configured.");
        }
        if (receiptDesign == null) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                javax.swing.JOptionPane.showMessageDialog(null, "Receipt design not configured.", "Print Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            });
            throw new PrintException("Receipt design not configured.");
        }
        new ReceiptPrinter(printerConfig, receiptDesign, cashierName, items, subtotal, tax, total, tendered, change, pointsEarned, receiptNumber).print();
    }
}
