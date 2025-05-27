package com.pos.ui.admin.settings;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.Arrays;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.pos.dao.PrinterConfigDAO;
import com.pos.dao.ReceiptDesignDAO;
import com.pos.model.PrinterConfig;
import com.pos.model.ReceiptDesign;
import com.pos.utils.PrinterUtil;

public class PrinterSettingsPanel extends JPanel {
    private final PrinterConfigDAO printerConfigDAO;
    private final ReceiptDesignDAO receiptDesignDAO;
    
    private JComboBox<String> printerComboBox;
    private JComboBox<String> paperSizeComboBox;
    private JCheckBox autoPrintCheckBox;
    private JSpinner copiesSpinner;
    private JComboBox<String> paperTypeComboBox;
    
    private JTextField headerTextField;
    private JTextField footerTextField;
    private JCheckBox showLogoCheckBox;
    private JTextField logoPathField;
    private JButton browseLogoButton;
    private JCheckBox showDateTimeCheckBox;
    private JCheckBox showCashierNameCheckBox;
    private JCheckBox showTaxDetailsCheckBox;
    private JComboBox<String> fontFamilyComboBox;
    private JSpinner fontSizeSpinner;
    private JCheckBox showBorderCheckBox;
    
    private JTextField locationTextField;
    private JTextField contactNumberTextField;
    
    private JButton saveButton;
    private JButton testPrintButton;
    
    public PrinterSettingsPanel() {
        this.printerConfigDAO = new PrinterConfigDAO();
        this.receiptDesignDAO = new ReceiptDesignDAO();
        setLayout(new BorderLayout());
        initComponents();
        loadSettings();
    }
    
    private void initComponents() {
        // Create main panel with scroll pane
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        
        // Printer Configuration Panel
        JPanel printerPanel = createPrinterConfigPanel();
        mainPanel.add(printerPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Receipt Design Panel
        JPanel receiptPanel = createReceiptDesignPanel();
        mainPanel.add(receiptPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Buttons Panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createPrinterConfigPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Printer Configuration"));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Printer Selection
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Printer:"), gbc);
        gbc.gridx = 1;
        printerComboBox = new JComboBox<>(getAvailablePrinters());
        panel.add(printerComboBox, gbc);
        
        // Paper Size
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Paper Size:"), gbc);
        gbc.gridx = 1;
        paperSizeComboBox = new JComboBox<>(new String[]{"A4", "A5", "Thermal 80mm", "Thermal 58mm"});
        panel.add(paperSizeComboBox, gbc);
        
        // Auto Print
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Auto Print:"), gbc);
        gbc.gridx = 1;
        autoPrintCheckBox = new JCheckBox();
        panel.add(autoPrintCheckBox, gbc);
        
        // Copies
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Copies:"), gbc);
        gbc.gridx = 1;
        copiesSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        panel.add(copiesSpinner, gbc);
        
        // Paper Type
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Paper Type:"), gbc);
        gbc.gridx = 1;
        paperTypeComboBox = new JComboBox<>(new String[]{"Normal", "Thermal", "Glossy"});
        panel.add(paperTypeComboBox, gbc);
        
        return panel;
    }
    
    private JPanel createReceiptDesignPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Receipt Design"));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Header Text
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Header Text:"), gbc);
        gbc.gridx = 1;
        headerTextField = new JTextField(20);
        panel.add(headerTextField, gbc);
        
        // Location
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1;
        locationTextField = new JTextField(20);
        panel.add(locationTextField, gbc);
        
        // Contact Number
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Contact Number:"), gbc);
        gbc.gridx = 1;
        contactNumberTextField = new JTextField(20);
        panel.add(contactNumberTextField, gbc);
        
        // Footer Text
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Footer Text:"), gbc);
        gbc.gridx = 1;
        footerTextField = new JTextField(20);
        panel.add(footerTextField, gbc);
        
        // Logo
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Show Logo:"), gbc);
        gbc.gridx = 1;
        showLogoCheckBox = new JCheckBox();
        panel.add(showLogoCheckBox, gbc);
        
        // Logo Path
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Logo Path:"), gbc);
        gbc.gridx = 1;
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPathField = new JTextField(20);
        browseLogoButton = new JButton("Browse");
        logoPanel.add(logoPathField, BorderLayout.CENTER);
        logoPanel.add(browseLogoButton, BorderLayout.EAST);
        panel.add(logoPanel, gbc);
        
        // Additional Options
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Show Date/Time:"), gbc);
        gbc.gridx = 1;
        showDateTimeCheckBox = new JCheckBox();
        panel.add(showDateTimeCheckBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(new JLabel("Show Cashier Name:"), gbc);
        gbc.gridx = 1;
        showCashierNameCheckBox = new JCheckBox();
        panel.add(showCashierNameCheckBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 8;
        panel.add(new JLabel("Show Tax Details:"), gbc);
        gbc.gridx = 1;
        showTaxDetailsCheckBox = new JCheckBox();
        panel.add(showTaxDetailsCheckBox, gbc);
        
        // Font Settings
        gbc.gridx = 0; gbc.gridy = 9;
        panel.add(new JLabel("Font Family:"), gbc);
        gbc.gridx = 1;
        fontFamilyComboBox = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        panel.add(fontFamilyComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 10;
        panel.add(new JLabel("Font Size:"), gbc);
        gbc.gridx = 1;
        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(12, 8, 24, 1));
        panel.add(fontSizeSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 11;
        panel.add(new JLabel("Show Border:"), gbc);
        gbc.gridx = 1;
        showBorderCheckBox = new JCheckBox();
        panel.add(showBorderCheckBox, gbc);
        
        // Add action listeners
        browseLogoButton.addActionListener(e -> browseLogo());
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Save Settings");
        testPrintButton = new JButton("Test Print");
        
        saveButton.addActionListener(e -> saveSettings());
        testPrintButton.addActionListener(e -> testPrint());
        
        panel.add(testPrintButton);
        panel.add(saveButton);
        
        return panel;
    }
    
    private String[] getAvailablePrinters() {
        return Arrays.stream(PrintServiceLookup.lookupPrintServices(null, null))
            .map(PrintService::getName)
            .toArray(String[]::new);
    }
    
    private void browseLogo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png", "gif"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            logoPathField.setText(selectedFile.getAbsolutePath());
        }
    }
    
    private void loadSettings() {
        // Load printer configuration
        PrinterConfig printerConfig = printerConfigDAO.getCurrentConfig();
        if (printerConfig != null) {
            printerComboBox.setSelectedItem(printerConfig.getPrinterName());
            paperSizeComboBox.setSelectedItem(printerConfig.getPaperSize());
            autoPrintCheckBox.setSelected(printerConfig.isAutoPrint());
            copiesSpinner.setValue(printerConfig.getCopies());
            paperTypeComboBox.setSelectedItem(printerConfig.getPaperType());
        }
        
        // Load receipt design
        ReceiptDesign receiptDesign = receiptDesignDAO.getCurrentDesign();
        if (receiptDesign != null) {
            headerTextField.setText(receiptDesign.getHeaderText());
            locationTextField.setText(receiptDesign.getLocation());
            contactNumberTextField.setText(receiptDesign.getContactNumber());
            footerTextField.setText(receiptDesign.getFooterText());
            showLogoCheckBox.setSelected(receiptDesign.isShowLogo());
            logoPathField.setText(receiptDesign.getLogoPath());
            showDateTimeCheckBox.setSelected(receiptDesign.isShowDateTime());
            showCashierNameCheckBox.setSelected(receiptDesign.isShowCashierName());
            showTaxDetailsCheckBox.setSelected(receiptDesign.isShowTaxDetails());
            fontFamilyComboBox.setSelectedItem(receiptDesign.getFontFamily());
            fontSizeSpinner.setValue(receiptDesign.getFontSize());
            showBorderCheckBox.setSelected(receiptDesign.isShowBorder());
        }
    }
    
    private void saveSettings() {
        try {
            // Validate printer settings
            if (printerComboBox.getSelectedItem() == null || ((String) printerComboBox.getSelectedItem()).trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a printer.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (paperSizeComboBox.getSelectedItem() == null || ((String) paperSizeComboBox.getSelectedItem()).trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a paper size.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (paperTypeComboBox.getSelectedItem() == null || ((String) paperTypeComboBox.getSelectedItem()).trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a paper type.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if ((Integer) copiesSpinner.getValue() <= 0) {
                JOptionPane.showMessageDialog(this, "Number of copies must be at least 1.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate receipt design settings
            if (fontFamilyComboBox.getSelectedItem() == null || ((String) fontFamilyComboBox.getSelectedItem()).trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a font family.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if ((Integer) fontSizeSpinner.getValue() <= 0) {
                JOptionPane.showMessageDialog(this, "Font size must be greater than 0.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Save printer configuration
            PrinterConfig printerConfig = new PrinterConfig();
            printerConfig.setPrinterName((String) printerComboBox.getSelectedItem());
            printerConfig.setPaperSize((String) paperSizeComboBox.getSelectedItem());
            printerConfig.setAutoPrint(autoPrintCheckBox.isSelected());
            printerConfig.setCopies((Integer) copiesSpinner.getValue());
            printerConfig.setPaperType((String) paperTypeComboBox.getSelectedItem());

            PrinterConfig existingConfig = printerConfigDAO.getCurrentConfig();
            boolean printerSaved;
            if (existingConfig != null) {
                printerConfig.setId(existingConfig.getId());
                printerSaved = printerConfigDAO.updateConfig(printerConfig);
            } else {
                printerSaved = printerConfigDAO.saveConfig(printerConfig);
            }

            // Save receipt design
            ReceiptDesign receiptDesign = new ReceiptDesign();
            receiptDesign.setHeaderText(headerTextField.getText());
            receiptDesign.setLocation(locationTextField.getText());
            receiptDesign.setContactNumber(contactNumberTextField.getText());
            receiptDesign.setFooterText(footerTextField.getText());
            receiptDesign.setShowLogo(showLogoCheckBox.isSelected());
            receiptDesign.setLogoPath(logoPathField.getText());
            receiptDesign.setShowDateTime(showDateTimeCheckBox.isSelected());
            receiptDesign.setShowCashierName(showCashierNameCheckBox.isSelected());
            receiptDesign.setShowTaxDetails(showTaxDetailsCheckBox.isSelected());
            receiptDesign.setFontFamily((String) fontFamilyComboBox.getSelectedItem());
            receiptDesign.setFontSize((Integer) fontSizeSpinner.getValue());
            receiptDesign.setShowBorder(showBorderCheckBox.isSelected());

            ReceiptDesign existingDesign = receiptDesignDAO.getCurrentDesign();
            boolean designSaved;
            if (existingDesign != null) {
                receiptDesign.setId(existingDesign.getId());
                designSaved = receiptDesignDAO.updateDesign(receiptDesign);
            } else {
                designSaved = receiptDesignDAO.saveDesign(receiptDesign);
            }

            if (printerSaved && designSaved) {
                JOptionPane.showMessageDialog(this, "Settings saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save some settings. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving settings: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void testPrint() {
        try {
            // Validate printer settings before test print
            if (printerComboBox.getSelectedItem() == null || ((String) printerComboBox.getSelectedItem()).trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a printer before testing.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate paper size
            if (paperSizeComboBox.getSelectedItem() == null || ((String) paperSizeComboBox.getSelectedItem()).trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a paper size before testing.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate paper type
            if (paperTypeComboBox.getSelectedItem() == null || ((String) paperTypeComboBox.getSelectedItem()).trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a paper type before testing.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate font settings
            if (fontFamilyComboBox.getSelectedItem() == null || ((String) fontFamilyComboBox.getSelectedItem()).trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a font family before testing.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if ((Integer) fontSizeSpinner.getValue() <= 0) {
                JOptionPane.showMessageDialog(this, "Font size must be greater than 0.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if printer is available
            PrintService printService = PrinterUtil.findPrintService((String) printerComboBox.getSelectedItem());
            if (printService == null) {
                String[] availablePrinters = PrinterUtil.getAvailablePrinters();
                String printerList = String.join("\n", availablePrinters);
                JOptionPane.showMessageDialog(this, 
                    "Selected printer not found. Please check if the printer is connected and properly configured in Windows.\n\n" +
                    "Available printers:\n" + printerList, 
                    "Printer Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create test receipt content
            String[][] testItems = {
                {"Test Item 1", "1", "100.00"},
                {"Test Item 2", "2", "50.00"}
            };
            double subtotal = 200.00;
            double tax = 20.00;
            double total = 220.00;
            double tendered = 250.00;
            double change = 30.00;
            int pointsEarned = 22;
            String testReceiptNumber = "TEST" + System.currentTimeMillis();

            // Get current settings
            PrinterConfig printerConfig = new PrinterConfig();
            printerConfig.setPrinterName((String) printerComboBox.getSelectedItem());
            printerConfig.setPaperSize((String) paperSizeComboBox.getSelectedItem());
            printerConfig.setAutoPrint(autoPrintCheckBox.isSelected());
            printerConfig.setCopies((Integer) copiesSpinner.getValue());
            printerConfig.setPaperType((String) paperTypeComboBox.getSelectedItem());

            ReceiptDesign receiptDesign = new ReceiptDesign();
            receiptDesign.setHeaderText(headerTextField.getText());
            receiptDesign.setFooterText(footerTextField.getText());
            receiptDesign.setShowLogo(showLogoCheckBox.isSelected());
            receiptDesign.setLogoPath(logoPathField.getText());
            receiptDesign.setShowDateTime(showDateTimeCheckBox.isSelected());
            receiptDesign.setShowCashierName(showCashierNameCheckBox.isSelected());
            receiptDesign.setShowTaxDetails(showTaxDetailsCheckBox.isSelected());
            receiptDesign.setFontFamily((String) fontFamilyComboBox.getSelectedItem());
            receiptDesign.setFontSize((Integer) fontSizeSpinner.getValue());
            receiptDesign.setShowBorder(showBorderCheckBox.isSelected());

            // Test print
            try {
                com.pos.utils.ReceiptPrinter printer = new com.pos.utils.ReceiptPrinter(
                    printerConfig,
                    receiptDesign,
                    "Test Cashier",
                    testItems,
                    subtotal,
                    tax,
                    total,
                    tendered,
                    change,
                    pointsEarned,
                    testReceiptNumber
                );
                printer.print();
                JOptionPane.showMessageDialog(this, "Test print successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (javax.print.PrintException ex) {
                JOptionPane.showMessageDialog(this, "Error printing test receipt: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            String errorMessage = "Error during test print: " + e.getMessage();
            if (e.getCause() != null) {
                errorMessage += "\nCause: " + e.getCause().getMessage();
            }
            JOptionPane.showMessageDialog(this, 
                errorMessage + "\n\nPlease check all printer settings and try again.", 
                "Print Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
} 