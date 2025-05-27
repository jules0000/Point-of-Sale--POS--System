package com.pos.model;

public class PrinterConfig {
    private int id;
    private String printerName;
    private String paperSize;
    private boolean autoPrint;
    private int copies;
    private String paperType;
    
    public PrinterConfig() {
        this.paperSize = "Thermal 58mm";
    }
    
    public PrinterConfig(int id, String printerName, String paperSize, boolean autoPrint, int copies, String paperType) {
        this.id = id;
        this.printerName = printerName;
        this.paperSize = (paperSize == null || paperSize.trim().isEmpty()) ? "Thermal 58mm" : paperSize;
        this.autoPrint = autoPrint;
        this.copies = copies;
        this.paperType = paperType;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getPrinterName() { return printerName; }
    public void setPrinterName(String printerName) { this.printerName = printerName; }
    
    public String getPaperSize() { return paperSize; }
    public void setPaperSize(String paperSize) { this.paperSize = paperSize; }
    
    public boolean isAutoPrint() { return autoPrint; }
    public void setAutoPrint(boolean autoPrint) { this.autoPrint = autoPrint; }
    
    public int getCopies() { return copies; }
    public void setCopies(int copies) { this.copies = copies; }
    
    public String getPaperType() { return paperType; }
    public void setPaperType(String paperType) { this.paperType = paperType; }
} 