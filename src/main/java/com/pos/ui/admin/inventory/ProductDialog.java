package com.pos.ui.admin.inventory;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.math.BigDecimal;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import com.pos.db.ProductDAO;
import com.pos.model.Product;
import com.pos.ui.admin.inventory.CategoryManagementDialog;

public class ProductDialog extends JDialog {
    private JTextField barcodeField;
    private JTextField nameField;
    private JComboBox<String> categoryField;
    private JTextField priceField;
    private JSpinner stockSpinner;
    private JSpinner thresholdSpinner;
    private JTextField imagePathField;
    private JButton browseImageButton;
    private JLabel imagePreviewLabel;
    private JTextField descriptionField;
    
    private Product product;
    private boolean productSaved = false;
    
    private byte[] imageBytes;
    
    private boolean isNewProduct;
    
    public ProductDialog(Window owner, Product product) {
        super(owner, product == null ? "Add New Product" : "Edit Product", ModalityType.APPLICATION_MODAL);
        System.out.println("=== DEBUG: ProductDialog constructor started ===");
        System.out.println("=== DEBUG: ProductDialog super() called ===");
        this.product = product;
        this.isNewProduct = (product.getId() == null || product.getId().isEmpty());
        System.out.println("=== DEBUG: ProductDialog fields initialized ===");
        
        setSize(450, 500);
        setLocationRelativeTo(owner);
        setResizable(false);
        System.out.println("=== DEBUG: ProductDialog size and location set ===");
        
        // Create main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        System.out.println("=== DEBUG: Content panel created ===");
        
        // Create form panel
        JPanel formPanel = createFormPanel();
        contentPanel.add(formPanel);
        System.out.println("=== DEBUG: Form panel added ===");
        
        // Add some spacing
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Create buttons panel
        JPanel buttonsPanel = createButtonsPanel();
        contentPanel.add(buttonsPanel);
        System.out.println("=== DEBUG: Buttons panel added ===");
        
        setContentPane(contentPanel);
        System.out.println("=== DEBUG: Content pane set ===");
        
        // Initialize form with product data if editing
        if (product != null) {
            populateForm();
            System.out.println("=== DEBUG: Form populated with existing product data ===");
        }
        System.out.println("=== DEBUG: ProductDialog constructor completed ===");
    }
    
    @Override
    public void setVisible(boolean visible) {
        System.out.println("=== DEBUG: ProductDialog setVisible(" + visible + ") called ===");
        super.setVisible(visible);
        System.out.println("=== DEBUG: ProductDialog setVisible(" + visible + ") completed ===");
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 15));
        
        // Barcode
        JLabel barcodeLabel = new JLabel("Barcode:");
        barcodeField = new JTextField();
        panel.add(barcodeLabel);
        panel.add(barcodeField);
        
        // Name
        JLabel nameLabel = new JLabel("Product Name:");
        nameField = new JTextField();
        panel.add(nameLabel);
        panel.add(nameField);
        
        // Category
        JLabel categoryLabel = new JLabel("Category:");
        java.util.List<String> categories = CategoryManagementDialog.getAllCategories();
        if (categories.isEmpty()) {
            categories = java.util.Arrays.asList("Food", "Beverages", "Household");
        }
        categoryField = new JComboBox<>(categories.toArray(new String[0]));
        panel.add(categoryLabel);
        panel.add(categoryField);
        
        // Price
        JLabel priceLabel = new JLabel("Price (₱):");
        priceField = new JTextField();
        panel.add(priceLabel);
        panel.add(priceField);
        
        // Stock
        JLabel stockLabel = new JLabel("Stock Quantity:");
        stockSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        panel.add(stockLabel);
        panel.add(stockSpinner);
        
        // Low Stock Threshold
        JLabel thresholdLabel = new JLabel("Low Stock Threshold:");
        thresholdSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 999, 1));
        panel.add(thresholdLabel);
        panel.add(thresholdSpinner);
        
        // Description
        JLabel descLabel = new JLabel("Description:");
        descriptionField = new JTextField();
        panel.add(descLabel);
        panel.add(descriptionField);
        
        // Image
        JLabel imageLabel = new JLabel("Image:");
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        imagePathField = new JTextField(15);
        browseImageButton = new JButton("Browse");
        imagePreviewLabel = new JLabel();
        imagePreviewLabel.setPreferredSize(new Dimension(60, 60));
        imagePanel.add(imagePathField);
        imagePanel.add(browseImageButton);
        imagePanel.add(imagePreviewLabel);
        panel.add(imageLabel);
        panel.add(imagePanel);
        
        // Browse action
        browseImageButton.addActionListener(e -> browseImage());
        
        return panel;
    }
    
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save");
        
        cancelButton.addActionListener(e -> dispose());
        
        saveButton.addActionListener(e -> {
            if (validateForm()) {
                saveProduct();
            }
        });
        
        // Style save button
        saveButton.setBackground(new Color(76, 175, 80));
        saveButton.setForeground(Color.WHITE);
        
        panel.add(cancelButton);
        panel.add(saveButton);
        
        return panel;
    }
    
    private void populateForm() {
        barcodeField.setText(product.getBarcode());
        nameField.setText(product.getName());
        categoryField.setSelectedItem(product.getCategory());
        priceField.setText(product.getSellingPrice() == null ? "0.00" : product.getSellingPrice().toString());
        stockSpinner.setValue(product.getCurrentStock());
        thresholdSpinner.setValue(product.getLowStockThreshold());
        product.setQuantity(product.getCurrentStock());
        product.setReorderLevel(product.getLowStockThreshold());
        descriptionField.setText(product.getDescription());
        imagePathField.setText(product.getImagePath());
        imageBytes = product.getImage();
        if (imageBytes != null && imageBytes.length > 0) {
            javax.swing.ImageIcon icon = new javax.swing.ImageIcon(imageBytes);
            java.awt.Image img = icon.getImage().getScaledInstance(60, 60, java.awt.Image.SCALE_SMOOTH);
            imagePreviewLabel.setIcon(new javax.swing.ImageIcon(img));
        } else if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
            javax.swing.ImageIcon icon = new javax.swing.ImageIcon(product.getImagePath());
            java.awt.Image img = icon.getImage().getScaledInstance(60, 60, java.awt.Image.SCALE_SMOOTH);
            imagePreviewLabel.setIcon(new javax.swing.ImageIcon(img));
        }
    }
    
    private boolean validateForm() {
        // Validate barcode
        if (barcodeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Barcode is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            barcodeField.requestFocus();
            return false;
        }
        
        // Validate name
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Product name is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        
        // Validate price
        try {
            BigDecimal price = new BigDecimal(priceField.getText().trim());
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Price must be greater than zero", "Validation Error", JOptionPane.ERROR_MESSAGE);
                priceField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price must be a valid number", "Validation Error", JOptionPane.ERROR_MESSAGE);
            priceField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void browseImage() {
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));
        if (fileChooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            imagePathField.setText(selectedFile.getAbsolutePath());
            try {
                java.nio.file.Path path = selectedFile.toPath();
                imageBytes = java.nio.file.Files.readAllBytes(path);
            } catch (Exception ex) {
                imageBytes = null;
            }
            javax.swing.ImageIcon icon = new javax.swing.ImageIcon(selectedFile.getAbsolutePath());
            java.awt.Image img = icon.getImage().getScaledInstance(60, 60, java.awt.Image.SCALE_SMOOTH);
            imagePreviewLabel.setIcon(new javax.swing.ImageIcon(img));
        }
    }
    
    private void saveProduct() {
        try {
            // Validate input
            String barcode = barcodeField.getText().trim();
            String name = nameField.getText().trim();
            String category = (String) categoryField.getSelectedItem();
            BigDecimal sellingPrice = new BigDecimal(priceField.getText().trim());
            int stock = (Integer) stockSpinner.getValue();
            int threshold = (Integer) thresholdSpinner.getValue();
            String description = descriptionField.getText().trim();
            String imagePath = imagePathField.getText().trim();
            
            if (name.isEmpty() || barcode.isEmpty() || category == null) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Set product fields
            product.setBarcode(barcode);
            product.setName(name);
            product.setCategory(category);
            product.setSellingPrice(sellingPrice);
            product.setCostPrice(sellingPrice); // Default cost price to selling price, can be updated later
            product.setCurrentStock(stock);
            product.setLowStockThreshold(threshold);
            product.setQuantity(stock);
            product.setReorderLevel(threshold);
            product.setDescription(description);
            product.setImagePath(imagePath);
            product.setImage(imageBytes);
            product.setActive(true);
            
            // Ensure new UUID for new product
            if (isNewProduct) {
                product.setId(java.util.UUID.randomUUID().toString());
            }
            
            // Save using ProductDAO
            ProductDAO productDAO = new ProductDAO();
            if (isNewProduct) {
                productDAO.save(product);
                JOptionPane.showMessageDialog(this, "Product added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                productDAO.update(product);
                JOptionPane.showMessageDialog(this, "Product updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            
            productSaved = true;
            dispose();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price format", "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving product: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isProductSaved() {
        return productSaved;
    }
} 