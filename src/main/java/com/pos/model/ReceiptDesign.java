package com.pos.model;

public class ReceiptDesign {
    private int id;
    private String headerText;
    private String footerText;
    private boolean showLogo;
    private String logoPath;
    private boolean showDateTime;
    private boolean showCashierName;
    private boolean showTaxDetails;
    private String fontFamily;
    private int fontSize;
    private boolean showBorder;
    private String location;
    private String contactNumber;
    
    public ReceiptDesign() {}
    
    public ReceiptDesign(int id, String headerText, String footerText, boolean showLogo, 
                        String logoPath, boolean showDateTime, boolean showCashierName,
                        boolean showTaxDetails, String fontFamily, int fontSize, boolean showBorder) {
        this.id = id;
        this.headerText = headerText;
        this.footerText = footerText;
        this.showLogo = showLogo;
        this.logoPath = logoPath;
        this.showDateTime = showDateTime;
        this.showCashierName = showCashierName;
        this.showTaxDetails = showTaxDetails;
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
        this.showBorder = showBorder;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getHeaderText() { return headerText; }
    public void setHeaderText(String headerText) { this.headerText = headerText; }
    
    public String getFooterText() { return footerText; }
    public void setFooterText(String footerText) { this.footerText = footerText; }
    
    public boolean isShowLogo() { return showLogo; }
    public void setShowLogo(boolean showLogo) { this.showLogo = showLogo; }
    
    public String getLogoPath() { return logoPath; }
    public void setLogoPath(String logoPath) { this.logoPath = logoPath; }
    
    public boolean isShowDateTime() { return showDateTime; }
    public void setShowDateTime(boolean showDateTime) { this.showDateTime = showDateTime; }
    
    public boolean isShowCashierName() { return showCashierName; }
    public void setShowCashierName(boolean showCashierName) { this.showCashierName = showCashierName; }
    
    public boolean isShowTaxDetails() { return showTaxDetails; }
    public void setShowTaxDetails(boolean showTaxDetails) { this.showTaxDetails = showTaxDetails; }
    
    public String getFontFamily() { return fontFamily; }
    public void setFontFamily(String fontFamily) { this.fontFamily = fontFamily; }
    
    public int getFontSize() { return fontSize; }
    public void setFontSize(int fontSize) { this.fontSize = fontSize; }
    
    public boolean isShowBorder() { return showBorder; }
    public void setShowBorder(boolean showBorder) { this.showBorder = showBorder; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
} 