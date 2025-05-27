package com.pos.ui.admin.settings;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.logging.*;
import com.pos.db.DatabaseConnection;

public class SystemSettings extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(SystemSettings.class.getName());
    
    // UI Components
    private JTextField receiptHeaderField;
    private JTextField receiptFooterField;
    private JCheckBox showTaxCheckbox;
    private JCheckBox showCashierCheckbox;
    private JTextField paperWidthField;
    private JTextField printerNameField;
    private JTextField printerPortField;
    private JTextField charPerLineField;
    private JTextField pointsPerPesoField;
    private JTextField pointsValueField;
    private JTextField minRedeemField;
    private JTextField pointsExpiryField;
    private JTextField taxRateField;
    private JCheckBox taxInclusiveCheckbox;

    public SystemSettings() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Receipt Settings", createReceiptPanel());
        tabbedPane.addTab("Printer Settings", createPrinterPanel());
        tabbedPane.addTab("Points Settings", createPointsPanel());
        tabbedPane.addTab("Tax Settings", createTaxPanel());

        // Add save button at bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save Settings");
        saveButton.addActionListener(e -> saveSettings());
        buttonPanel.add(saveButton);

        add(tabbedPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load current settings
        loadSettings();
    }

    private JPanel createReceiptPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Receipt Header
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Receipt Header:"), gbc);
        gbc.gridx = 1;
        receiptHeaderField = new JTextField(30);
        panel.add(receiptHeaderField, gbc);

        // Receipt Footer
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Receipt Footer:"), gbc);
        gbc.gridx = 1;
        receiptFooterField = new JTextField(30);
        panel.add(receiptFooterField, gbc);

        // Show Tax
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Show Tax:"), gbc);
        gbc.gridx = 1;
        showTaxCheckbox = new JCheckBox();
        panel.add(showTaxCheckbox, gbc);

        // Show Cashier
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Show Cashier:"), gbc);
        gbc.gridx = 1;
        showCashierCheckbox = new JCheckBox();
        panel.add(showCashierCheckbox, gbc);

        // Paper Width
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Paper Width (mm):"), gbc);
        gbc.gridx = 1;
        paperWidthField = new JTextField(10);
        panel.add(paperWidthField, gbc);

        return panel;
    }

    private JPanel createPrinterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Printer Name
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Printer Name:"), gbc);
        gbc.gridx = 1;
        printerNameField = new JTextField(20);
        panel.add(printerNameField, gbc);

        // Printer Port
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Printer Port:"), gbc);
        gbc.gridx = 1;
        printerPortField = new JTextField(10);
        panel.add(printerPortField, gbc);

        // Characters per Line
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Characters per Line:"), gbc);
        gbc.gridx = 1;
        charPerLineField = new JTextField(10);
        panel.add(charPerLineField, gbc);

        // Test Print Button
        gbc.gridx = 1; gbc.gridy = 3;
        JButton testButton = new JButton("Test Print");
        testButton.addActionListener(e -> testPrint());
        panel.add(testButton, gbc);

        return panel;
    }

    private JPanel createPointsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Points per Peso
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("₱ per Point:"), gbc);
        gbc.gridx = 1;
        pointsPerPesoField = new JTextField(10);
        panel.add(pointsPerPesoField, gbc);

        // Point Value in Peso
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Point Value (₱):"), gbc);
        gbc.gridx = 1;
        pointsValueField = new JTextField(10);
        panel.add(pointsValueField, gbc);

        // Minimum Points for Redemption
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Minimum Points:"), gbc);
        gbc.gridx = 1;
        minRedeemField = new JTextField(10);
        panel.add(minRedeemField, gbc);

        // Points Expiry
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Expiry (months):"), gbc);
        gbc.gridx = 1;
        pointsExpiryField = new JTextField(10);
        panel.add(pointsExpiryField, gbc);

        return panel;
    }

    private JPanel createTaxPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Tax Rate
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Tax Rate (%):"), gbc);
        gbc.gridx = 1;
        taxRateField = new JTextField(10);
        panel.add(taxRateField, gbc);

        // Tax Inclusive
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Tax Inclusive:"), gbc);
        gbc.gridx = 1;
        taxInclusiveCheckbox = new JCheckBox();
        panel.add(taxInclusiveCheckbox, gbc);

        return panel;
    }

    private void loadSettings() {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT setting_key, setting_value FROM settings")) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String key = rs.getString("setting_key");
                String value = rs.getString("setting_value");
                
                switch (key) {
                    case "receipt_header":
                        receiptHeaderField.setText(value);
                        break;
                    case "receipt_footer":
                        receiptFooterField.setText(value);
                        break;
                    case "receipt_show_tax":
                        showTaxCheckbox.setSelected(Boolean.parseBoolean(value));
                        break;
                    case "receipt_show_cashier":
                        showCashierCheckbox.setSelected(Boolean.parseBoolean(value));
                        break;
                    case "receipt_paper_width":
                        paperWidthField.setText(value);
                        break;
                    case "printer_name":
                        printerNameField.setText(value);
                        break;
                    case "printer_port":
                        printerPortField.setText(value);
                        break;
                    case "printer_char_per_line":
                        charPerLineField.setText(value);
                        break;
                    case "points_peso_per_point":
                        pointsPerPesoField.setText(value);
                        break;
                    case "points_peso_value":
                        pointsValueField.setText(value);
                        break;
                    case "points_minimum_redeem":
                        minRedeemField.setText(value);
                        break;
                    case "points_expiry_months":
                        pointsExpiryField.setText(value);
                        break;
                    case "tax_rate":
                        taxRateField.setText(value);
                        break;
                    case "tax_inclusive":
                        taxInclusiveCheckbox.setSelected(Boolean.parseBoolean(value));
                        break;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading settings", e);
            JOptionPane.showMessageDialog(this,
                "Error loading settings: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveSettings() {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "UPDATE settings SET setting_value = ? WHERE setting_key = ?")) {
            
            // Receipt Settings
            updateSetting(stmt, "receipt_header", receiptHeaderField.getText());
            updateSetting(stmt, "receipt_footer", receiptFooterField.getText());
            updateSetting(stmt, "receipt_show_tax", String.valueOf(showTaxCheckbox.isSelected()));
            updateSetting(stmt, "receipt_show_cashier", String.valueOf(showCashierCheckbox.isSelected()));
            updateSetting(stmt, "receipt_paper_width", paperWidthField.getText());
            
            // Printer Settings
            updateSetting(stmt, "printer_name", printerNameField.getText());
            updateSetting(stmt, "printer_port", printerPortField.getText());
            updateSetting(stmt, "printer_char_per_line", charPerLineField.getText());
            
            // Points Settings
            updateSetting(stmt, "points_peso_per_point", pointsPerPesoField.getText());
            updateSetting(stmt, "points_peso_value", pointsValueField.getText());
            updateSetting(stmt, "points_minimum_redeem", minRedeemField.getText());
            updateSetting(stmt, "points_expiry_months", pointsExpiryField.getText());
            
            // Tax Settings
            updateSetting(stmt, "tax_rate", taxRateField.getText());
            updateSetting(stmt, "tax_inclusive", String.valueOf(taxInclusiveCheckbox.isSelected()));
            
            JOptionPane.showMessageDialog(this,
                "Settings saved successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving settings", e);
            JOptionPane.showMessageDialog(this,
                "Error saving settings: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSetting(PreparedStatement stmt, String key, String value) throws SQLException {
        stmt.setString(1, value);
        stmt.setString(2, key);
        stmt.executeUpdate();
    }

    private void testPrint() {
        try {
            // Create test receipt content
            StringBuilder receipt = new StringBuilder();
            receipt.append("=== TEST RECEIPT ===\n");
            receipt.append("Printer: ").append(printerNameField.getText()).append("\n");
            receipt.append("Port: ").append(printerPortField.getText()).append("\n");
            receipt.append("Characters per line: ").append(charPerLineField.getText()).append("\n");
            receipt.append("==================\n");
            
            // TODO: Implement actual printer interface
            JOptionPane.showMessageDialog(this,
                "Test Receipt Content:\n\n" + receipt.toString(),
                "Test Print",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during test print", e);
            JOptionPane.showMessageDialog(this,
                "Error during test print: " + e.getMessage(),
                "Printer Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 