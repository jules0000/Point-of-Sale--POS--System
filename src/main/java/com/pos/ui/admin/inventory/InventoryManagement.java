package com.pos.ui.admin.inventory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Window;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.pos.db.DatabaseConnection;
import com.pos.db.ProductDAO;
import com.pos.model.Product;
import com.pos.util.FormatUtils;

public class InventoryManagement extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(InventoryManagement.class.getName());
    private final ProductDAO productDAO;
    private final JTable productTable;
    private final DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> categoryFilter;
    private JButton addButton, editButton, deleteButton, refreshButton;
    private List<Product> products = new ArrayList<>();
    
    public InventoryManagement() {
        System.out.println("=== DEBUG: InventoryManagement constructor started ===");
        this.productDAO = new ProductDAO();
        this.tableModel = new DefaultTableModel(
            new Object[][] {},
            new String[] {"ID", "Name", "Barcode", "Category", "Price", "Stock", "Status"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return String.class;
                return super.getColumnClass(columnIndex);
            }
        };
        this.productTable = new JTable(tableModel);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Top panel with search and filters
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with product table
        JScrollPane tablePane = createTablePanel();
        add(tablePane, BorderLayout.CENTER);
        
        // Bottom panel with action buttons
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Load initial data
        loadProducts();
        System.out.println("=== DEBUG: InventoryManagement constructor completed ===");
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Search section
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        JLabel searchLabel = new JLabel("Search Products:");
        searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(33, 150, 243));
        searchButton.setForeground(Color.WHITE);
        
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        // Filter section
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel filterLabel = new JLabel("Category:");
        java.util.List<String> categories = new java.util.ArrayList<>();
        categories.add("All Categories");
        categories.addAll(CategoryManagementDialog.getAllCategories());
        categoryFilter = new JComboBox<>(categories.toArray(new String[0]));
        filterPanel.add(filterLabel);
        filterPanel.add(categoryFilter);
        
        // Add search and filter to top panel
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(filterPanel, BorderLayout.EAST);
        
        // Add action listeners
        searchButton.addActionListener(e -> searchProducts(searchField.getText()));
        categoryFilter.addActionListener(e -> filterByCategory());
        
        return panel;
    }
    
    private JScrollPane createTablePanel() {
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.setRowHeight(30);
        productTable.getTableHeader().setReorderingAllowed(false);
        
        // Column widths
        productTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        productTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        productTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        productTable.getColumnModel().getColumn(6).setPreferredWidth(150);
        
        // Change row color for low stock items
        productTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Get product at this row
                if (row < products.size()) {
                    Product product = products.get(row);
                    if (product.isLowOnStock()) {
                        c.setBackground(isSelected ? new Color(255, 150, 150) : new Color(255, 200, 200));
                    } else {
                        c.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                    }
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(productTable);
        return scrollPane;
    }
    
    private JPanel createBottomPanel() {
        System.out.println("=== DEBUG: Creating bottom panel ===");
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        refreshButton = new JButton("Refresh");
        addButton = new JButton("Add Product");
        addButton.setBackground(Color.RED);
        addButton.setForeground(Color.WHITE);
        System.out.println("=== DEBUG: Add button created and styled ===");
        editButton = new JButton("Edit Product");
        deleteButton = new JButton("Delete Product");
        JButton manageCategoriesButton = new JButton("Manage Categories");
        manageCategoriesButton.setBackground(new Color(255, 193, 7));
        manageCategoriesButton.setForeground(Color.BLACK);
        manageCategoriesButton.addActionListener(e -> {
            new CategoryManagementDialog(SwingUtilities.getWindowAncestor(this)).setVisible(true);
        });
        
        // Style buttons
        editButton.setBackground(new Color(33, 150, 243));
        editButton.setForeground(Color.WHITE);
        
        deleteButton.setBackground(new Color(244, 67, 54));
        deleteButton.setForeground(Color.WHITE);
        
        // Add action listeners
        refreshButton.addActionListener(e -> loadProducts());
        addButton.addActionListener(e -> {
            try {
                System.out.println("Step 1: Creating new Product");
                Product product = new Product();
                
                System.out.println("Step 2: Getting window ancestor");
                Window owner = SwingUtilities.getWindowAncestor(this);
                if (owner == null) {
                    JOptionPane.showMessageDialog(this, "Error: Could not find parent window", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                System.out.println("Step 3: Creating ProductDialog");
                ProductDialog dialog = new ProductDialog(owner, product);
                
                System.out.println("Step 4: Setting dialog visible");
                dialog.setVisible(true);
                
                System.out.println("Step 5: Dialog closed");
                if (dialog.isProductSaved()) {
                    System.out.println("Step 6: Product was saved, reloading");
                    loadProducts();
                }
            } catch (Exception ex) {
                System.out.println("Error in add button: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error creating product dialog: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        editButton.addActionListener(e -> editProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        
        panel.add(refreshButton);
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(manageCategoriesButton);
        
        return panel;
    }
    
    private void loadProducts() {
        products.clear();
        tableModel.setRowCount(0);
        
        try {
            products = productDAO.getAllProducts();
            for (Product product : products) {
                Object[] row = {
                    product.getId(),
                    product.getName(),
                    product.getBarcode(),
                    product.getCategory(),
                    FormatUtils.formatCurrency(product.getSellingPrice()),
                    product.getCurrentStock(),
                    product.isLowOnStock() ? "Low" : "OK"
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading products", e);
            showError("Failed to load products: " + e.getMessage());
        }
    }
    
    private void searchProducts(String searchTerm) {
        if (searchTerm.isEmpty()) {
            loadProducts();
            return;
        }
        
        products.clear();
        tableModel.setRowCount(0);
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM inventory WHERE item_name LIKE ? OR barcode LIKE ? ORDER BY item_name")) {
            
            stmt.setString(1, "%" + searchTerm + "%");
            stmt.setString(2, "%" + searchTerm + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getString("id"));
                    product.setBarcode(rs.getString("barcode"));
                    product.setName(rs.getString("item_name"));
                    product.setCategory(rs.getString("category"));
                    product.setSellingPrice(rs.getBigDecimal("price"));
                    product.setCurrentStock(rs.getInt("quantity"));
                    product.setLowStockThreshold(rs.getInt("reorder_level"));
                    product.setActive(true);
                    
                    products.add(product);
                    
                    // Add to table model
                    Object[] row = {
                        product.getId(),
                        product.getName(),
                        product.getBarcode(),
                        product.getCategory(),
                        FormatUtils.formatCurrency(product.getSellingPrice()),
                        product.getCurrentStock(),
                        product.isLowOnStock() ? "Low" : "OK"
                    };
                    
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching products", e);
            showError("Failed to search products: " + e.getMessage());
        }
    }
    
    private void filterByCategory() {
        String selectedCategory = (String) categoryFilter.getSelectedItem();
        
        if ("All Categories".equals(selectedCategory)) {
            loadProducts();
            return;
        }
        
        products.clear();
        tableModel.setRowCount(0);
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM inventory WHERE category = ? ORDER BY item_name")) {
            
            stmt.setString(1, selectedCategory);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getString("id"));
                    product.setBarcode(rs.getString("barcode"));
                    product.setName(rs.getString("item_name"));
                    product.setCategory(rs.getString("category"));
                    product.setSellingPrice(rs.getBigDecimal("price"));
                    product.setCurrentStock(rs.getInt("quantity"));
                    product.setLowStockThreshold(rs.getInt("reorder_level"));
                    product.setActive(true);
                    
                    products.add(product);
                    
                    // Add to table model
                    Object[] row = {
                        product.getId(),
                        product.getName(),
                        product.getBarcode(),
                        product.getCategory(),
                        FormatUtils.formatCurrency(product.getSellingPrice()),
                        product.getCurrentStock(),
                        product.isLowOnStock() ? "Low" : "OK"
                    };
                    
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error filtering products", e);
            showError("Failed to filter products: " + e.getMessage());
        }
    }
    
    private void addProduct() {
        System.out.println("=== DEBUG: Inside addProduct() method ===");
        Product product = new Product();
        System.out.println("=== DEBUG: Created new Product object ===");
        System.out.println("=== DEBUG: About to create ProductDialog ===");
        ProductDialog dialog = new ProductDialog(SwingUtilities.getWindowAncestor(this), product);
        System.out.println("=== DEBUG: ProductDialog created ===");
        System.out.println("=== DEBUG: About to show dialog ===");
        dialog.setVisible(true);
        System.out.println("=== DEBUG: Dialog closed ===");
        if (dialog.isProductSaved()) {
            System.out.println("=== DEBUG: Product was saved, reloading products ===");
            loadProducts();
        } else {
            System.out.println("=== DEBUG: Product was not saved ===");
        }
    }
    
    private void editProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Product selectedProduct = products.get(selectedRow);
        ProductDialog dialog = new ProductDialog(SwingUtilities.getWindowAncestor(this), selectedProduct);
        dialog.setVisible(true);
        
        if (dialog.isProductSaved()) {
            loadProducts();
        }
    }
    
    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Product selectedProduct = products.get(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete " + selectedProduct.getName() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                productDAO.delete(selectedProduct.getId());
                loadProducts();
                JOptionPane.showMessageDialog(this, "Product deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error deleting product", e);
                showError("Failed to delete product: " + e.getMessage());
            }
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
} 