package com.pos.ui.cashier;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.pos.db.DatabaseConnection;
import com.pos.utils.BarcodeScanner;

public class CashierDashboard extends JFrame {
    private JPanel contentPanel;
    private String username;
    private JLabel clockLabel;
    private JLabel totalLabel;
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private Timer clockTimer;
    
    // Member information
    private boolean memberLoggedIn = false;
    private String memberName = "";
    private String memberID = "";
    private String membershipLevel = "";
    private int memberPoints = 0;
    private double discountRate = 0.0;
    private boolean usePointsForDiscount = false;
    
    // Member UI components
    private JPanel memberDetailsPanel;
    private JLabel memberNameLabel;
    private JLabel memberLevelLabel;
    private JLabel memberPointsLabel;
    private JLabel discountLabel;
    private JCheckBox usePointsCheckbox;
    private JLabel pointsLabel;
    
    // Labels for totals
    private JLabel subtotalVal;
    private JLabel taxVal;
    private JLabel discountVal;
    private JLabel memberDiscountVal;
    private JLabel changeVal;
    
    // Color scheme - similar to commercial POS systems
    private final Color PRIMARY_COLOR = new Color(0, 123, 255); // Blue
    private final Color SECONDARY_COLOR = new Color(23, 162, 184); // Teal
    private final Color ACCENT_COLOR = new Color(255, 193, 7); // Yellow
    private final Color SUCCESS_COLOR = new Color(40, 167, 69); // Green
    private final Color DANGER_COLOR = new Color(220, 53, 69); // Red
    private final Color DARK_GRAY = new Color(33, 37, 41); // Dark Gray
    private final Color LIGHT_GRAY = new Color(248, 249, 250); // Light Gray
    private final Color WHITE = Color.WHITE;
    
    // Add this constant at the top of the class
    private static final String WALKIN_CUSTOMER_ID = "WALKIN";
    
    public CashierDashboard(String username) {
        this.username = username;
        
        setTitle("QuickVend POS System - Cashier Dashboard");
        setSize(1280, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize cart table model
        String[] cartColumns = {"Item", "Price", "Quantity", "Total"};
        cartTableModel = new DefaultTableModel(cartColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_GRAY);
        
        // Create top bar
        JPanel topPanel = createTopBar();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Create content split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.7); // Left panel gets 70% of space
        splitPane.setDividerLocation(0.7);
        splitPane.setBorder(null);
        splitPane.setDividerSize(5);
        
        // Left side (product listing and scanning)
        JPanel productPanel = createProductPanel();
        splitPane.setLeftComponent(productPanel);
        
        // Right side (cart and checkout)
        JPanel checkoutPanel = createCheckoutPanel();
        splitPane.setRightComponent(checkoutPanel);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        // Set as content pane
        setContentPane(mainPanel);
        
        // Start the clock
        startClock();
    }
    
    private JPanel createTopBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        // Left side of top bar (logo and title)
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        
        // Add logo image
        JLabel logoLabel = new JLabel();
        try {
            URL logoUrl = getClass().getResource("/icons/pos-logo.png");
            if (logoUrl != null) {
                ImageIcon logoIcon = new ImageIcon(logoUrl);
                Image img = logoIcon.getImage();
                Image resizedImg = img.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(resizedImg));
            }
        } catch (Exception e) {
            System.out.println("Warning: Could not load logo image");
        }
        
        JLabel titleLabel = new JLabel("QUICKVEND");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(WHITE);
        
        JLabel subtitleLabel = new JLabel("CLOUD - BASED POS SYSTEM");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        subtitleLabel.setForeground(WHITE);
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        
        leftPanel.add(logoLabel);
        leftPanel.add(titlePanel);
        
        // Center of top bar (cashier info)
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false);
        
        JLabel userLabel = new JLabel("Cashier: " + username);
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setForeground(WHITE);
        
        clockLabel = new JLabel();
        clockLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        clockLabel.setForeground(WHITE);
        
        centerPanel.add(userLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        centerPanel.add(clockLabel);
        
        // Right side of top bar (logout button)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(DANGER_COLOR);
        logoutBtn.setForeground(WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> logout());
        
        rightPanel.add(logoutBtn);
        
        // Add all components to top panel
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 5));
        panel.setBackground(WHITE);
        
        // Top section for barcode scanning
        JPanel scanPanel = new JPanel(new BorderLayout(10, 0));
        scanPanel.setBorder(new MatteBorder(0, 0, 2, 0, LIGHT_GRAY));
        scanPanel.setBackground(WHITE);
        
        JLabel scanLabel = new JLabel("Scan Barcode:");
        scanLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JTextField barcodeField = new JTextField();
        barcodeField.setPreferredSize(new Dimension(100, 35));
        barcodeField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
                
        // Initialize barcode scanner
        new BarcodeScanner(barcodeField, barcode -> {
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "SELECT item_name, price FROM inventory WHERE barcode = ? AND quantity > 0")) {
                
                stmt.setString(1, barcode);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    String itemName = rs.getString("item_name");
                    String price = rs.getString("price");
                    
                    // Check if item already exists in cart
                    int existingRow = -1;
                    for (int i = 0; i < cartTableModel.getRowCount(); i++) {
                        if (cartTableModel.getValueAt(i, 0).equals(itemName)) {
                            existingRow = i;
                            break;
                        }
                    }
                    
                    if (existingRow != -1) {
                        // Item exists, increment quantity
                        int currentQty = Integer.parseInt(cartTableModel.getValueAt(existingRow, 2).toString());
                        double itemPrice = Double.parseDouble(price);
                        int newQty = currentQty + 1;
                        double newTotal = itemPrice * newQty;
                        
                        cartTableModel.setValueAt(String.valueOf(newQty), existingRow, 2);
                        cartTableModel.setValueAt("₱" + String.format("%.2f", newTotal), existingRow, 3);
                    } else {
                        // Add new item to cart
                        cartTableModel.addRow(new Object[]{
                            itemName,
                            "₱" + price,
                            "1",
                            "₱" + price
                        });
                    }
                    
                    updateTotal();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "No product found with barcode: " + barcode,
                        "Product Not Found",
                        JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException e) {
                Logger.getLogger(CashierDashboard.class.getName()).log(Level.SEVERE, "Error processing barcode", e);
                JOptionPane.showMessageDialog(this,
                    "Error processing barcode: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JPanel scanLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        scanLabelPanel.setBackground(WHITE);
        scanLabelPanel.add(scanLabel);
        
        scanPanel.add(scanLabelPanel, BorderLayout.WEST);
        scanPanel.add(barcodeField, BorderLayout.CENTER);
        
        // Product listing (tabbed categories)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        tabbedPane.setBackground(WHITE);
        
        // Add tabs first before trying to set colors
        tabbedPane.addTab("All Products", createProductGrid("All"));
        tabbedPane.addTab("Food", createProductGrid("Food"));
        tabbedPane.addTab("Beverages", createProductGrid("Beverages"));
        tabbedPane.addTab("Household", createProductGrid("Household"));
        
        // Now it's safe to set the foreground of the first tab
        tabbedPane.setForegroundAt(0, PRIMARY_COLOR);
        
        panel.add(scanPanel, BorderLayout.NORTH);
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createProductGrid(String category) {
        JPanel panel = new JPanel(new GridLayout(0, 4, 10, 10));
        panel.setBorder(new EmptyBorder(15, 10, 10, 10));
        panel.setBackground(WHITE);
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String sql;
            PreparedStatement stmt = null;
            
            try {
                if ("All".equals(category)) {
                    sql = "SELECT id, item_name, category, barcode, price FROM inventory WHERE quantity > 0 ORDER BY item_name";
                    stmt = conn.prepareStatement(sql);
                } else {
                    sql = "SELECT id, item_name, category, barcode, price FROM inventory WHERE category = ? AND quantity > 0 ORDER BY item_name";
                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1, category);
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.isBeforeFirst()) {
                        // No products found
                        JLabel noProductsLabel = new JLabel("No products found in this category");
                        noProductsLabel.setHorizontalAlignment(JLabel.CENTER);
                        panel.add(noProductsLabel);
                    } else {
                        while (rs.next()) {
                            String name = rs.getString("item_name");
                            BigDecimal price = rs.getBigDecimal("price");
                            String barcode = rs.getString("barcode");
                            
                            JButton productBtn = createProductButton(
                                name, 
                                price.toString(),
                                barcode,
                                null, // No image URL in database schema
                                null // No image bytes in database schema
                            );
                            panel.add(productBtn);
                        }
                    }
                }
            } catch (SQLException e) {
                Logger.getLogger(CashierDashboard.class.getName()).log(Level.SEVERE, 
                    "Error executing product query: " + e.getMessage(), e);
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(panel),
                    "Error loading products: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            } finally {
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException e) {
                        Logger.getLogger(CashierDashboard.class.getName()).log(Level.WARNING, 
                            "Error closing statement: " + e.getMessage(), e);
                    }
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(CashierDashboard.class.getName()).log(Level.SEVERE, 
                "Error getting database connection: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(panel),
                "Error connecting to database. Please check your connection.",
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
        
        // Add scroll pane for large product lists
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(WHITE);
        containerPanel.add(scrollPane, BorderLayout.CENTER);
        
        return containerPanel;
    }
    
    private JButton createProductButton(String name, String price, String barcode, String productImageUrl, byte[] productImageBytes) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setPreferredSize(new Dimension(150, 120));
        button.setBackground(WHITE);
        button.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(WHITE);
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        // Add product image
        JLabel imageLabel = new JLabel();
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            ImageIcon icon = null;
            if (productImageBytes != null && productImageBytes.length > 0) {
                icon = new ImageIcon(productImageBytes);
            } else if (productImageUrl != null && !productImageUrl.isEmpty()) {
                icon = new ImageIcon(productImageUrl);
            } else {
            URL defaultImageUrl = getClass().getResource("/icons/product-default.png");
            if (defaultImageUrl != null) {
                    icon = new ImageIcon(defaultImageUrl);
                }
            }
            if (icon != null) {
                Image img = icon.getImage();
                Image resizedImg = img.getScaledInstance(80, 60, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(resizedImg));
            }
        } catch (Exception e) {
            Logger.getLogger(CashierDashboard.class.getName()).log(Level.WARNING, "Could not load product image", e);
        }
        
        JLabel nameLabel = new JLabel(name, JLabel.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel priceLabel = new JLabel("₱" + price, JLabel.CENTER);
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        priceLabel.setForeground(SUCCESS_COLOR);
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add barcode label (optional)
        JLabel barcodeLabel = new JLabel(barcode, JLabel.CENTER);
        barcodeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        barcodeLabel.setForeground(Color.GRAY);
        barcodeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(imageLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(nameLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        contentPanel.add(priceLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        contentPanel.add(barcodeLabel);
        
        button.add(contentPanel, BorderLayout.CENTER);
        
        // Add action to add item to cart
        button.addActionListener(e -> {
            // Check if item already exists in cart
            int existingRow = -1;
            for (int i = 0; i < cartTableModel.getRowCount(); i++) {
                if (cartTableModel.getValueAt(i, 0).equals(name)) {
                    existingRow = i;
                    break;
                }
            }
            
            if (existingRow != -1) {
                // Item exists, increment quantity
                int currentQty = Integer.parseInt(cartTableModel.getValueAt(existingRow, 2).toString());
                double itemPrice = Double.parseDouble(price);
                int newQty = currentQty + 1;
                double newTotal = itemPrice * newQty;
                
                cartTableModel.setValueAt(String.valueOf(newQty), existingRow, 2);
                cartTableModel.setValueAt("₱" + String.format("%.2f", newTotal), existingRow, 3);
            } else {
                // Add new item to cart
                cartTableModel.addRow(new Object[]{
                    name,
                    "₱" + price,
                    "1",
                    "₱" + price
                });
            }
            
            updateTotal();
        });
        
        return button;
    }
    
    private void addToCart(String name, String price, int quantity) {
        // In a real app, this would check stock, handle quantities better, etc.
        double unitPrice = Double.parseDouble(price);
        double itemTax = unitPrice * 0.1; // 10% tax per item
        double totalPrice = (unitPrice + itemTax) * quantity;
        
        // Find if item already exists in cart
        boolean found = false;
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            if (cartTableModel.getValueAt(i, 0).equals(name)) {
                int currentQty = Integer.parseInt(cartTableModel.getValueAt(i, 2).toString());
                int newQty = currentQty + quantity;
                double newTotal = (unitPrice + itemTax) * newQty;
                
                cartTableModel.setValueAt(String.valueOf(newQty), i, 2);
                cartTableModel.setValueAt("₱" + String.format("%.2f", newTotal), i, 3);
                found = true;
                break;
            }
        }
        
        // If not found, add new row
        if (!found) {
            cartTableModel.addRow(new Object[]{
                name,
                "₱" + String.format("%.2f", unitPrice + itemTax), // Price includes tax
                String.valueOf(quantity),
                "₱" + String.format("%.2f", totalPrice)
            });
        }
        
        updateTotal();
    }
    
    private void updateTotal() {
        double subtotal = 0.0;
        
        // Calculate subtotal
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            String totalStr = cartTableModel.getValueAt(i, 3).toString().replace("₱", "");
            subtotal += Double.parseDouble(totalStr);
        }
        
        // Calculate tax (get from settings)
        double taxRate = 0.12; // Default 12% VAT
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT setting_value FROM settings WHERE setting_key = 'tax_rate'")) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                taxRate = Double.parseDouble(rs.getString("setting_value")) / 100.0;
            }
        } catch (SQLException e) {
            Logger.getLogger(CashierDashboard.class.getName()).log(Level.WARNING, "Error getting tax rate, using default", e);
        }
        
        double tax = subtotal * taxRate;
        
        // Calculate member discount if applicable
        double discount = 0.0;
        if (memberLoggedIn) {
            if (usePointsForDiscount) {
                double pointValue = 1.0;
                try (Connection conn2 = DatabaseConnection.getInstance().getConnection();
                     PreparedStatement settingStmt = conn2.prepareStatement(
                         "SELECT setting_value FROM settings WHERE setting_key = 'points_peso_value'")) {
                    ResultSet rs = settingStmt.executeQuery();
                    if (rs.next()) {
                        pointValue = Double.parseDouble(rs.getString("setting_value"));
                    }
                } catch (SQLException e) {
                    Logger.getLogger(CashierDashboard.class.getName()).log(Level.WARNING, "Error getting point value, using default", e);
                }
                discount = Math.min(memberPoints * pointValue, subtotal);
            } else {
                discount = subtotal * (discountRate / 100.0);
            }
        }
        
        // Calculate final total
        double total = subtotal + tax - discount;
        
        // Update labels with peso sign
        subtotalVal.setText(String.format("₱%.2f", subtotal));
        taxVal.setText(String.format("₱%.2f", tax));
        discountVal.setText(String.format("₱%.2f", discount));
        totalLabel.setText(String.format("₱%.2f", total));
    }
    
    private JPanel createCheckoutPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(WHITE);
        
        // Top cart title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JLabel cartLabel = new JLabel("Shopping Cart");
        cartLabel.setFont(new Font("Arial", Font.BOLD, 16));
        cartLabel.setForeground(WHITE);
        
        headerPanel.add(cartLabel, BorderLayout.WEST);
        
        // Create cart table model
        String[] columns = {"Item", "Price", "Qty", "Total"};
        cartTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        
        // Cart table in the middle
        cartTable = new JTable(cartTableModel);
        cartTable.setRowHeight(40);
        cartTable.setShowGrid(false);
        cartTable.setIntercellSpacing(new Dimension(0, 0));
        cartTable.setFillsViewportHeight(true);
        cartTable.setBackground(WHITE);
        cartTable.setSelectionBackground(new Color(232, 240, 254));
        
        // Add right-click context menu for cart items
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem adjustQuantityItem = new JMenuItem("Adjust Quantity");
        JMenuItem voidItem = new JMenuItem("Void Item");
        
        adjustQuantityItem.addActionListener(e -> {
            int selectedRow = cartTable.getSelectedRow();
            if (selectedRow != -1) {
                adjustItemQuantity(selectedRow);
            }
        });
        
        voidItem.addActionListener(e -> {
            int selectedRow = cartTable.getSelectedRow();
            if (selectedRow != -1) {
                cartTableModel.removeRow(selectedRow);
                updateTotal();
            }
        });
        
        popupMenu.add(adjustQuantityItem);
        popupMenu.add(voidItem);
        
        cartTable.setComponentPopupMenu(popupMenu);
        
        // Style the header
        JTableHeader header = cartTable.getTableHeader();
        header.setBackground(LIGHT_GRAY);
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, LIGHT_GRAY));
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        
        JScrollPane tableScrollPane = new JScrollPane(cartTable);
        tableScrollPane.setBorder(null);
        tableScrollPane.getViewport().setBackground(WHITE);
        
        // Bottom panel for totals and checkout button
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(WHITE);
        bottomPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Customer panel (member lookup)
        JPanel customerPanel = new JPanel(new BorderLayout(10, 0));
        customerPanel.setBackground(WHITE);
        customerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JPanel memberPanel = new JPanel(new BorderLayout(5, 0));
        memberPanel.setBackground(WHITE);
        
        JLabel memberLabel = new JLabel("Customer ID:");
        memberLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JTextField memberField = new JTextField();
        memberField.setPreferredSize(new Dimension(150, 30));
        
        JButton lookupBtn = new JButton("Lookup");
        lookupBtn.setBackground(PRIMARY_COLOR);
        lookupBtn.setForeground(WHITE);
        lookupBtn.addActionListener(e -> showMemberLookupDialog(memberField));
        
        JButton walkInBtn = new JButton("Walk-in Customer");
        walkInBtn.setBackground(SECONDARY_COLOR);
        walkInBtn.setForeground(WHITE);
        walkInBtn.addActionListener(e -> {
            memberField.setText("");
            resetMemberStatus();
            memberLoggedIn = false;
            if (memberDetailsPanel != null) {
                memberDetailsPanel.setVisible(false);
            }
            if (usePointsCheckbox != null) {
                usePointsCheckbox.setSelected(false);
                usePointsCheckbox.setEnabled(false);
            }
            if (memberNameLabel != null) {
                memberNameLabel.setText("Customer: Walk-in");
            }
            if (memberPointsLabel != null) {
                memberPointsLabel.setText("Points: 0");
            }
            updateTotal();
        });
        
        memberPanel.add(memberLabel, BorderLayout.WEST);
        memberPanel.add(memberField, BorderLayout.CENTER);
        memberPanel.add(lookupBtn, BorderLayout.CENTER);
        memberPanel.add(walkInBtn, BorderLayout.EAST);
        
        // Add member details panel
        memberDetailsPanel = new JPanel();
        memberDetailsPanel.setLayout(new BoxLayout(memberDetailsPanel, BoxLayout.Y_AXIS));
        memberDetailsPanel.setBackground(WHITE);
        memberDetailsPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        memberNameLabel = new JLabel("Customer: Walk-in");
        memberNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        memberLevelLabel = new JLabel("");
        memberLevelLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        memberPointsLabel = new JLabel("Points: 0");
        memberPointsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        memberDetailsPanel.add(memberNameLabel);
        memberDetailsPanel.add(memberLevelLabel);
        memberDetailsPanel.add(memberPointsLabel);
        memberDetailsPanel.setVisible(false);
        // Insert below memberPanel
        customerPanel.add(memberDetailsPanel, BorderLayout.CENTER);
        
        JPanel pointsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pointsPanel.setBackground(WHITE);
        
        usePointsCheckbox = new JCheckBox("Use Points");
        usePointsCheckbox.setBackground(WHITE);
        usePointsCheckbox.setEnabled(false);
        usePointsCheckbox.addActionListener(e -> {
            usePointsForDiscount = usePointsCheckbox.isSelected();
            updateTotal();
        });
        
        JLabel pointsLabel = new JLabel("Points: 0");
        pointsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        pointsPanel.add(usePointsCheckbox);
        pointsPanel.add(pointsLabel);
        
        customerPanel.add(memberPanel, BorderLayout.WEST);
        customerPanel.add(pointsPanel, BorderLayout.EAST);
        
        // Totals panel
        JPanel totalsPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        totalsPanel.setBackground(WHITE);
        
        Font totalFont = new Font("Arial", Font.PLAIN, 14);
        Font valueFont = new Font("Arial", Font.BOLD, 14);
        
        // Subtotal
        JLabel subtotalLbl = new JLabel("Subtotal:", JLabel.LEFT);
        subtotalLbl.setFont(totalFont);
        subtotalVal = new JLabel("₱0.00", JLabel.RIGHT);
        subtotalVal.setFont(valueFont);
        
        // Tax (now included in price)
        JLabel taxLbl = new JLabel("Tax:", JLabel.LEFT);
        taxLbl.setFont(totalFont);
        taxVal = new JLabel("Included in price", JLabel.RIGHT);
        taxVal.setFont(valueFont);
        
        // Discount
        JLabel discountLbl = new JLabel("Points Discount:", JLabel.LEFT);
        discountLbl.setFont(totalFont);
        discountVal = new JLabel("₱0.00", JLabel.RIGHT);
        discountVal.setFont(valueFont);
        
        // Member Discount
        JLabel memberDiscountLbl = new JLabel("Customer Discount:", JLabel.LEFT);
        memberDiscountLbl.setFont(totalFont);
        memberDiscountVal = new JLabel("₱0.00", JLabel.RIGHT);
        memberDiscountVal.setFont(valueFont);
        
        // Total
        JLabel totalLbl = new JLabel("TOTAL:", JLabel.LEFT);
        totalLbl.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel = new JLabel("₱0.00", JLabel.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(DANGER_COLOR);
        
        // Payment method
        JLabel paymentMethodLbl = new JLabel("Payment Method:", JLabel.LEFT);
        paymentMethodLbl.setFont(totalFont);
        
        JPanel paymentMethodPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        paymentMethodPanel.setBackground(WHITE);
        
        ButtonGroup paymentGroup = new ButtonGroup();
        JRadioButton cashBtn = new JRadioButton("Cash");
        JRadioButton debitBtn = new JRadioButton("Debit");
        
        cashBtn.setBackground(WHITE);
        debitBtn.setBackground(WHITE);
        cashBtn.setSelected(true); // Default to cash
        
        paymentGroup.add(cashBtn);
        paymentGroup.add(debitBtn);
        
        paymentMethodPanel.add(cashBtn);
        paymentMethodPanel.add(debitBtn);
        
        // Tender amount and change
        JLabel tenderLbl = new JLabel("Amount Tendered:", JLabel.LEFT);
        tenderLbl.setFont(totalFont);
        
        JPanel tenderPanel = new JPanel(new BorderLayout());
        tenderPanel.setBackground(WHITE);
        
        JTextField tenderField = new JTextField("0.00");
        tenderField.setFont(new Font("Arial", Font.BOLD, 16));
        tenderField.setHorizontalAlignment(JTextField.RIGHT);
        tenderField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    // Get the input and total values
                    String input = tenderField.getText().trim();
                    String totalText = totalLabel.getText().replace("₱", "").trim();
                    double totalAmount = Double.parseDouble(totalText);
                    
                    // Handle empty input
                    if (input.isEmpty()) {
                        changeVal.setText("ENTER AMOUNT");
                        changeVal.setForeground(DANGER_COLOR);
                        return;
                    }
                    
                    // Remove any non-numeric characters except decimal point
                    input = input.replaceAll("[^0-9.]", "");
                    
                    // Ensure only one decimal point
                    if (input.indexOf(".") != input.lastIndexOf(".")) {
                        input = input.substring(0, input.lastIndexOf("."));
                    }
                    
                    // Parse the tender amount
                    double tenderedAmount = Double.parseDouble(input);
                    
                    // Update the field with cleaned input if different
                    if (!tenderField.getText().equals(input)) {
                        tenderField.setText(input);
                    }
                    
                    // Calculate and display change
                    double change = tenderedAmount - totalAmount;
                    
                    if (tenderedAmount < totalAmount) {
                        changeVal.setText("INSUFFICIENT");
                        changeVal.setForeground(DANGER_COLOR);
                    } else {
                        changeVal.setText("₱" + String.format("%.2f", change));
                        changeVal.setForeground(SUCCESS_COLOR);
                    }
                } catch (NumberFormatException ex) {
                    // Only show invalid if there's actual input
                    if (!tenderField.getText().trim().isEmpty()) {
                        changeVal.setText("INVALID AMOUNT");
                        changeVal.setForeground(DANGER_COLOR);
                    }
                }
            }
        });
        
        JLabel changeLbl = new JLabel("Change:", JLabel.LEFT);
        changeLbl.setFont(totalFont);
        changeVal = new JLabel("₱0.00", JLabel.RIGHT);
        changeVal.setFont(valueFont);
        changeVal.setForeground(SUCCESS_COLOR);
        
        totalsPanel.add(subtotalLbl);
        totalsPanel.add(subtotalVal);
        totalsPanel.add(taxLbl);
        totalsPanel.add(taxVal);
        totalsPanel.add(discountLbl);
        totalsPanel.add(discountVal);
        totalsPanel.add(memberDiscountLbl);
        totalsPanel.add(memberDiscountVal);
        totalsPanel.add(totalLbl);
        totalsPanel.add(totalLabel);
        totalsPanel.add(paymentMethodLbl);
        totalsPanel.add(paymentMethodPanel);
        totalsPanel.add(tenderLbl);
        totalsPanel.add(tenderField);
        totalsPanel.add(changeLbl);
        totalsPanel.add(changeVal);
        
        // Action buttons
        JPanel actionPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        actionPanel.setBackground(WHITE);
        actionPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        JButton clearBtn = new JButton("Clear Cart (F6)");
        clearBtn.setBackground(SECONDARY_COLOR);
        clearBtn.setForeground(WHITE);
        clearBtn.setFont(new Font("Arial", Font.BOLD, 14));
        clearBtn.setFocusPainted(false);
        clearBtn.setBorderPainted(false);
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> {
            cartTableModel.setRowCount(0);
            updateTotal();
            tenderField.setText("0.00");
            changeVal.setText("₱0.00");
        });
        
        JButton voidItemBtn = new JButton("Void Item (F7)");
        voidItemBtn.setBackground(DANGER_COLOR);
        voidItemBtn.setForeground(WHITE);
        voidItemBtn.setFont(new Font("Arial", Font.BOLD, 14));
        voidItemBtn.setFocusPainted(false);
        voidItemBtn.setBorderPainted(false);
        voidItemBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        voidItemBtn.addActionListener(e -> {
            int selectedRow = cartTable.getSelectedRow();
            if (selectedRow != -1) {
                cartTableModel.removeRow(selectedRow);
                updateTotal();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Please select an item to void.",
                    "No Item Selected", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton checkoutBtn = new JButton("Process Payment (F9)");
        checkoutBtn.setBackground(SUCCESS_COLOR);
        checkoutBtn.setForeground(WHITE);
        checkoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        checkoutBtn.setFocusPainted(false);
        checkoutBtn.setBorderPainted(false);
        checkoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkoutBtn.addActionListener(e -> {
            String paymentMethod = cashBtn.isSelected() ? "Cash" : "Debit";
            processPayment(tenderField, changeVal, paymentMethod);
        });
        
        actionPanel.add(clearBtn);
        actionPanel.add(voidItemBtn);
        actionPanel.add(checkoutBtn);
        
        // Add all to bottom panel
        bottomPanel.add(customerPanel);
        bottomPanel.add(totalsPanel);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        bottomPanel.add(actionPanel);
        
        // Add keyboard shortcuts for function keys
        registerFunctionKeys(clearBtn, voidItemBtn, checkoutBtn, tenderField, changeVal);
        
        // Add all to main panel
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // Register function key shortcuts
    private void registerFunctionKeys(JButton clearBtn, JButton voidItemBtn, JButton checkoutBtn,
                                     JTextField tenderField, JLabel changeVal) {
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();
        
        // F6 for Clear Cart
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), "clearCart");
        actionMap.put("clearCart", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearBtn.doClick();
            }
        });
        
        // F7 for Void Item
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0), "voidItem");
        actionMap.put("voidItem", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                voidItemBtn.doClick();
            }
        });
        
        // F9 for Checkout
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), "checkout");
        actionMap.put("checkout", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkoutBtn.doClick();
            }
        });
    }
    
    // Process payment and print receipt
    private void processPayment(JTextField tenderField, JLabel changeLabel, String paymentMethod) {
        try {
            tenderField.requestFocus();
            tenderField.transferFocus();
        } catch (Exception ex) {
            System.out.println("DEBUG: Exception while forcing focus transfer: " + ex.getMessage());
        }
        String totalText = totalLabel.getText().replace("₱", "");
        double totalAmount = Double.parseDouble(totalText);
        System.out.println("DEBUG: tenderField.getText() = '" + tenderField.getText() + "'");
        // Prompt for tendered amount
        String tenderInput = JOptionPane.showInputDialog(this, "Enter amount tendered:", totalLabel.getText());
        if (tenderInput == null || tenderInput.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Payment cancelled. No tendered amount entered.", "Payment Cancelled", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double tenderAmount;
        try {
            tenderAmount = Double.parseDouble(tenderInput.replace("₱", "").trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid tendered amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double changeAmount = tenderAmount - totalAmount;
        if (tenderAmount < totalAmount) {
            JOptionPane.showMessageDialog(this, "Tender amount is less than the total amount!", "Insufficient Payment", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String transactionId = generateTransactionId();
        String receiptNumber = generateReceiptNumber();
        int pointsEarned = 0;
        if (memberLoggedIn) {
            pointsEarned = (int)(totalAmount / 10); // 1 point per 10 pesos
        }
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);
            try {
                // Insert transaction
                try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO transactions (id, transaction_no, cashier_id, member_id, subtotal, total_amount, amount_tendered, change_amount, points_earned, points_redeemed, payment_method, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                    stmt.setString(1, transactionId);
                    stmt.setString(2, receiptNumber);
                    stmt.setString(3, username);
                    // Handle member_id correctly for walk-in vs member
                    if (memberLoggedIn) {
                        stmt.setString(4, memberID);
                    } else {
                        stmt.setNull(4, java.sql.Types.VARCHAR); // Walk-in customer has null member_id
                    }
                    double subtotal = totalAmount / 1.12; // Remove 12% VAT
                    stmt.setDouble(5, subtotal);
                    stmt.setDouble(6, totalAmount);
                    stmt.setDouble(7, tenderAmount);
                    stmt.setDouble(8, changeAmount);
                    stmt.setInt(9, pointsEarned);
                    if (memberLoggedIn && usePointsForDiscount) {
                        stmt.setInt(10, (int)(memberPoints * 1.0)); // Points redeemed
                    } else {
                        stmt.setInt(10, 0);
                    }
                    stmt.setString(11, paymentMethod);
                    stmt.setString(12, "Completed");
                    stmt.executeUpdate();
                }
                // Insert transaction items
                try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO transaction_items (id, transaction_id, item_id, quantity, unit_price, total_amount) VALUES (?, ?, ?, ?, ?, ?)")) {
                    for (int i = 0; i < cartTableModel.getRowCount(); i++) {
                        String itemName = cartTableModel.getValueAt(i, 0).toString();
                        int quantity = Integer.parseInt(cartTableModel.getValueAt(i, 2).toString());
                        String priceStr = cartTableModel.getValueAt(i, 1).toString().replace("₱", "");
                        String totalStr = cartTableModel.getValueAt(i, 3).toString().replace("₱", "");
                        String itemId;
                        try (PreparedStatement itemStmt = conn.prepareStatement(
                            "SELECT id FROM inventory WHERE item_name = ?")) {
                            itemStmt.setString(1, itemName);
                            ResultSet rs = itemStmt.executeQuery();
                            if (!rs.next()) {
                                throw new SQLException("Item not found: " + itemName);
                            }
                            itemId = rs.getString("id");
                        }
                        stmt.setString(1, generateUUID());
                        stmt.setString(2, transactionId);
                        stmt.setString(3, itemId);
                        stmt.setInt(4, quantity);
                        stmt.setDouble(5, Double.parseDouble(priceStr));
                        stmt.setDouble(6, Double.parseDouble(totalStr.replace("₱", "")));
                        stmt.executeUpdate();
                        // Update inventory
                        try (PreparedStatement updateStmt = conn.prepareStatement(
                            "UPDATE inventory SET quantity = quantity - ? WHERE id = ?")) {
                            updateStmt.setInt(1, quantity);
                            updateStmt.setString(2, itemId);
                            updateStmt.executeUpdate();
                        }
                    }
                }
                // If member, update points and points history
                if (memberLoggedIn) {
                    int finalPoints = memberPoints;
                    if (usePointsForDiscount) {
                        double pointValue = 1.0;
                        try (Connection conn2 = DatabaseConnection.getInstance().getConnection();
                             PreparedStatement settingStmt = conn2.prepareStatement(
                                 "SELECT setting_value FROM settings WHERE setting_key = 'points_peso_value'")) {
                            ResultSet rs = settingStmt.executeQuery();
                            if (rs.next()) {
                                pointValue = Double.parseDouble(rs.getString("setting_value"));
                            }
                        } catch (SQLException e) {
                            Logger.getLogger(CashierDashboard.class.getName()).log(Level.WARNING, "Error getting point value, using default", e);
                        }
                        int pointsRedeemed = (int)(memberDiscountVal.getText().replace("₱", "").isEmpty() ? 0 : Double.parseDouble(memberDiscountVal.getText().replace("₱", "")) / pointValue);
                        finalPoints -= pointsRedeemed;
                    }
                    finalPoints += pointsEarned;
                    try (PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE customers SET points = ?, total_spent = total_spent + ? WHERE id = ?")) {
                        stmt.setInt(1, finalPoints);
                        stmt.setDouble(2, totalAmount);
                        stmt.setString(3, memberID);
                        stmt.executeUpdate();
                    }
                    try (PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO points_history (id, membership_id, transaction_id, points_before, points_change, points_after, type) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                        if (pointsEarned > 0) {
                            stmt.setString(1, generateUUID());
                            stmt.setString(2, getMembershipIdFromCustomerId(memberID));
                            stmt.setString(3, transactionId);
                            stmt.setInt(4, memberPoints);
                            stmt.setInt(5, pointsEarned);
                            stmt.setInt(6, memberPoints + pointsEarned);
                            stmt.setString(7, "Earned");
                            stmt.executeUpdate();
                        }
                        if (usePointsForDiscount) {
                            int pointsRedeemed = (int)Double.parseDouble(
                                memberDiscountVal.getText().replace("₱", ""));
                            stmt.setString(1, generateUUID());
                            stmt.setString(2, getMembershipIdFromCustomerId(memberID));
                            stmt.setString(3, transactionId);
                            stmt.setInt(4, memberPoints + pointsEarned);
                            stmt.setInt(5, -pointsRedeemed);
                            stmt.setInt(6, finalPoints);
                            stmt.setString(7, "Redeemed");
                            stmt.executeUpdate();
                        }
                    }
                }
                conn.commit();
                printReceipt(totalAmount, tenderAmount, changeAmount, paymentMethod, memberLoggedIn, memberName, memberPoints + pointsEarned, pointsEarned);
                cartTableModel.setRowCount(0);
                updateTotal();
                tenderField.setText("0.00");
                changeLabel.setText("₱0.00");
                resetMemberStatus();
                if (memberDetailsPanel != null) memberDetailsPanel.setVisible(false);
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            Logger.getLogger(CashierDashboard.class.getName()).log(Level.SEVERE, "Database error during payment processing", e);
            JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
    
    // Generate and print receipt
    private void printReceipt(double totalAmount, double tenderAmount, double change, String paymentMethod, boolean isMember, String customerName, int pointsAfter, int pointsEarned) {
        // Create receipt dialog (preview)
        JDialog receiptDialog = new JDialog(this, "Receipt", true);
        receiptDialog.setSize(400, 600);
        receiptDialog.setLocationRelativeTo(this);
        
        JPanel receiptPanel = new JPanel();
        receiptPanel.setLayout(new BoxLayout(receiptPanel, BoxLayout.Y_AXIS));
        receiptPanel.setBackground(Color.WHITE);
        receiptPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Store information
        JLabel storeLabel = new JLabel("QUICKVEND POS SYSTEM");
        storeLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        storeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel addressLabel = new JLabel("123 Main Street, Anytown");
        addressLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        addressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel phoneLabel = new JLabel("Tel: (123) 456-7890");
        phoneLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        phoneLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Receipt information
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = dateFormat.format(new Date());
        
        JLabel dateLabel = new JLabel("Date: " + dateTime);
        dateLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel cashierLabel = new JLabel("Cashier: " + username);
        cashierLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        cashierLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel receiptLabel = new JLabel("Receipt #: " + generateReceiptNumber());
        receiptLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        receiptLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // If member is logged in, add member info to receipt (below receipt number)
        JPanel memberInfoPanel = null;
        if (isMember) {
            memberInfoPanel = new JPanel();
            memberInfoPanel.setLayout(new BoxLayout(memberInfoPanel, BoxLayout.Y_AXIS));
            memberInfoPanel.setBackground(Color.WHITE);
            memberInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel memberNameLbl = new JLabel("Customer: " + customerName);
            memberNameLbl.setFont(new Font("Monospaced", Font.BOLD, 12));
            memberNameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel memberPointsLbl = new JLabel("Points: " + pointsAfter);
            memberPointsLbl.setFont(new Font("Monospaced", Font.PLAIN, 12));
            memberPointsLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            memberInfoPanel.add(memberNameLbl);
            memberInfoPanel.add(memberPointsLbl);
        }
        
        // Separator
        JSeparator separator1 = new JSeparator();
        separator1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        
        // Items header
        JPanel headerPanel = new JPanel(new GridLayout(1, 4));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        
        JLabel itemHeader = new JLabel("Item");
        itemHeader.setFont(new Font("Monospaced", Font.BOLD, 12));
        
        JLabel qtyHeader = new JLabel("Qty");
        qtyHeader.setFont(new Font("Monospaced", Font.BOLD, 12));
        
        JLabel priceHeader = new JLabel("Price");
        priceHeader.setFont(new Font("Monospaced", Font.BOLD, 12));
        
        JLabel totalHeader = new JLabel("Total");
        totalHeader.setFont(new Font("Monospaced", Font.BOLD, 12));
        
        headerPanel.add(itemHeader);
        headerPanel.add(qtyHeader);
        headerPanel.add(priceHeader);
        headerPanel.add(totalHeader);
        
        // Add receipt items
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(Color.WHITE);
        
        // Prepare items for ReceiptPrinter
        String[][] receiptItems = new String[cartTableModel.getRowCount()][3];
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            JPanel itemPanel = new JPanel(new GridLayout(1, 4));
            itemPanel.setBackground(Color.WHITE);
            itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
            
            String itemNameStr = (String) cartTableModel.getValueAt(i, 0);
            String itemQtyStr = (String) cartTableModel.getValueAt(i, 2);
            String itemPriceStr = (String) cartTableModel.getValueAt(i, 1);
            String itemTotalStr = (String) cartTableModel.getValueAt(i, 3);
            
            JLabel itemName = new JLabel(itemNameStr);
            itemName.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JLabel itemQty = new JLabel(itemQtyStr);
            itemQty.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JLabel itemPrice = new JLabel(itemPriceStr);
            itemPrice.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JLabel itemTotal = new JLabel(itemTotalStr);
            itemTotal.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            itemPanel.add(itemName);
            itemPanel.add(itemQty);
            itemPanel.add(itemPrice);
            itemPanel.add(itemTotal);
            
            itemsPanel.add(itemPanel);
            // For ReceiptPrinter: [name, quantity, price]
            receiptItems[i][0] = itemNameStr;
            receiptItems[i][1] = itemQtyStr;
            receiptItems[i][2] = itemPriceStr.replace("₱", "").replace(",", "").trim();
        }
        
        // Separator
        JSeparator separator2 = new JSeparator();
        separator2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        
        // Totals
        JPanel totalsPanel = new JPanel();
        totalsPanel.setLayout(new BoxLayout(totalsPanel, BoxLayout.Y_AXIS));
        totalsPanel.setBackground(Color.WHITE);
        totalsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Subtotal
        JPanel subtotalPanel = new JPanel(new BorderLayout());
        subtotalPanel.setBackground(Color.WHITE);
        
        JLabel subtotalLabel = new JLabel("Subtotal:");
        subtotalLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        double subtotal = totalAmount * 0.9; // Assuming 10% tax
        double tax = totalAmount - subtotal;
        
        JLabel subtotalValue = new JLabel("₱" + String.format("%.2f", subtotal));
        subtotalValue.setFont(new Font("Monospaced", Font.PLAIN, 12));
        subtotalValue.setHorizontalAlignment(SwingConstants.RIGHT);
        
        subtotalPanel.add(subtotalLabel, BorderLayout.WEST);
        subtotalPanel.add(subtotalValue, BorderLayout.EAST);
        
        // Tax
        JPanel taxPanel = new JPanel(new BorderLayout());
        taxPanel.setBackground(Color.WHITE);
        
        JLabel taxLabel = new JLabel("Tax (10%):");
        taxLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JLabel taxValue = new JLabel("₱" + String.format("%.2f", tax));
        taxValue.setFont(new Font("Monospaced", Font.PLAIN, 12));
        taxValue.setHorizontalAlignment(SwingConstants.RIGHT);
        
        taxPanel.add(taxLabel, BorderLayout.WEST);
        taxPanel.add(taxValue, BorderLayout.EAST);
        
        // Total
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(Color.WHITE);
        
        JLabel totalLabel = new JLabel("TOTAL:");
        totalLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        
        JLabel totalValue = new JLabel("₱" + String.format("%.2f", totalAmount));
        totalValue.setFont(new Font("Monospaced", Font.BOLD, 14));
        totalValue.setHorizontalAlignment(SwingConstants.RIGHT);
        
        totalPanel.add(totalLabel, BorderLayout.WEST);
        totalPanel.add(totalValue, BorderLayout.EAST);
        
        // Tender
        JPanel tenderPanel = new JPanel(new BorderLayout());
        tenderPanel.setBackground(Color.WHITE);
        
        JLabel tenderLabel = new JLabel("Tendered:");
        tenderLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JLabel tenderValue = new JLabel("₱" + String.format("%.2f", tenderAmount));
        tenderValue.setFont(new Font("Monospaced", Font.PLAIN, 12));
        tenderValue.setHorizontalAlignment(SwingConstants.RIGHT);
        
        tenderPanel.add(tenderLabel, BorderLayout.WEST);
        tenderPanel.add(tenderValue, BorderLayout.EAST);
        
        // Change
        JPanel changePanel = new JPanel(new BorderLayout());
        changePanel.setBackground(Color.WHITE);
        
        JLabel changeLabel = new JLabel("Change:");
        changeLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JLabel changeValue = new JLabel("₱" + String.format("%.2f", change));
        changeValue.setFont(new Font("Monospaced", Font.PLAIN, 12));
        changeValue.setHorizontalAlignment(SwingConstants.RIGHT);
        
        changePanel.add(changeLabel, BorderLayout.WEST);
        changePanel.add(changeValue, BorderLayout.EAST);
        
        // Loyalty points earned
        JPanel pointsPanel = new JPanel(new BorderLayout());
        pointsPanel.setBackground(Color.WHITE);
        JLabel pointsLabel = new JLabel("Points Earned:");
        pointsLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JLabel pointsValue = new JLabel(String.format("%d", pointsEarned));
        pointsValue.setFont(new Font("Monospaced", Font.PLAIN, 12));
        pointsValue.setHorizontalAlignment(SwingConstants.RIGHT);
        pointsPanel.add(pointsLabel, BorderLayout.WEST);
        pointsPanel.add(pointsValue, BorderLayout.EAST);
        
        // Add all totals
        totalsPanel.add(subtotalPanel);
        totalsPanel.add(taxPanel);
        totalsPanel.add(totalPanel);
        totalsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        totalsPanel.add(tenderPanel);
        totalsPanel.add(changePanel);
        totalsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        totalsPanel.add(pointsPanel);
        
        // Separator
        JSeparator separator3 = new JSeparator();
        separator3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        
        // Footer
        JLabel thanksLabel = new JLabel("Thank you for shopping with us!");
        thanksLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        thanksLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton printButton = new JButton("Print");
        printButton.setBackground(PRIMARY_COLOR);
        printButton.setForeground(Color.WHITE);
        printButton.addActionListener(e -> {
            // --- Use admin-configured printer and receipt settings ---
            try {
                com.pos.dao.PrinterConfigDAO printerConfigDAO = new com.pos.dao.PrinterConfigDAO();
                com.pos.dao.ReceiptDesignDAO receiptDesignDAO = new com.pos.dao.ReceiptDesignDAO();
                com.pos.model.PrinterConfig printerConfig = printerConfigDAO.getCurrentConfig();
                com.pos.model.ReceiptDesign receiptDesign = receiptDesignDAO.getCurrentDesign();
                if (printerConfig == null || receiptDesign == null) {
                    JOptionPane.showMessageDialog(this, "Printer or receipt design settings not configured.", "Print Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String currentReceiptNumber = generateReceiptNumber(); // Generate a new receipt number

                // Get values from the receipt preview labels
                double receiptSubtotal = Double.parseDouble(subtotalVal.getText().replace("₱", "").trim());
                double receiptTax = Double.parseDouble(taxVal.getText().replace("₱", "").trim());

                com.pos.utils.ReceiptPrinter printer =
                    (isMember && customerName != null && !customerName.trim().isEmpty())
                        ? new com.pos.utils.ReceiptPrinter(
                            printerConfig,
                            receiptDesign,
                            username,
                            receiptItems,
                            receiptSubtotal,
                            receiptTax,
                            totalAmount,
                            tenderAmount,
                            change,
                            pointsEarned,
                            currentReceiptNumber,
                            customerName,
                            pointsAfter
                        )
                        : new com.pos.utils.ReceiptPrinter(
                            printerConfig,
                            receiptDesign,
                            username,
                            receiptItems,
                            receiptSubtotal,
                            receiptTax,
                            totalAmount,
                            tenderAmount,
                            change,
                            pointsEarned,
                            currentReceiptNumber
                        );
                printer.print();
                JOptionPane.showMessageDialog(this, "Receipt sent to printer.", "Print Receipt", JOptionPane.INFORMATION_MESSAGE);
                receiptDialog.dispose();
            } catch (javax.print.PrintException ex) {
                JOptionPane.showMessageDialog(this, "Error printing receipt: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> receiptDialog.dispose());
        
        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);
        
        // Add all components to receipt panel
        receiptPanel.add(storeLabel);
        receiptPanel.add(addressLabel);
        receiptPanel.add(phoneLabel);
        receiptPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        receiptPanel.add(dateLabel);
        receiptPanel.add(cashierLabel);
        receiptPanel.add(receiptLabel);
        if (memberInfoPanel != null) {
            receiptPanel.add(memberInfoPanel);
        }
        receiptPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        receiptPanel.add(separator1);
        receiptPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        receiptPanel.add(headerPanel);
        receiptPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        receiptPanel.add(itemsPanel);
        receiptPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        receiptPanel.add(separator2);
        receiptPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        receiptPanel.add(totalsPanel);
        receiptPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        receiptPanel.add(separator3);
        receiptPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        receiptPanel.add(thanksLabel);
        receiptPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        receiptPanel.add(buttonPanel);
        
        // Create a scroll pane for the receipt
        JScrollPane scrollPane = new JScrollPane(receiptPanel);
        scrollPane.setBorder(null);
        
        receiptDialog.add(scrollPane);
        receiptDialog.setVisible(true);
    }
    
    // Generate a random receipt number
    private String generateReceiptNumber() {
        return "R" + String.format("%08d", (int)(Math.random() * 10000000));
    }
    
    // Award loyalty points for the purchase and return points earned
    private int awardLoyaltyPoints(double totalAmount) {
        // Calculate points (1 point per 10 pesos spent)
        int pointsEarned = (int)(totalAmount / 10);
        
        if (pointsEarned > 0) {
            JOptionPane.showMessageDialog(this,
                "You earned " + pointsEarned + " loyalty points for this purchase!" +
                (memberLoggedIn ? "\nTotal points: " + (memberPoints + pointsEarned) : ""),
                "Loyalty Program", JOptionPane.INFORMATION_MESSAGE);
        }
        
        return pointsEarned;
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            stopClock();
            this.dispose();
            
            try {
                // Try to instantiate LoginScreen by reflection to avoid import issues
                Class<?> loginScreenClass = Class.forName("com.pos.ui.auth.LoginScreen");
                JFrame loginScreen = (JFrame) loginScreenClass.getDeclaredConstructor().newInstance();
                loginScreen.setVisible(true);
            } catch (Exception e) {
                System.err.println("Error returning to login screen: " + e.getMessage());
                JOptionPane.showMessageDialog(null, 
                    "Error returning to login. Please restart the application.", 
                    "Logout Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
    }
    
    private void startClock() {
        updateClock(); // Update immediately
        
        // Update every second
        clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();
    }
    
    private void stopClock() {
        if (clockTimer != null && clockTimer.isRunning()) {
            clockTimer.stop();
        }
    }
    
    private void updateClock() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
        String time = dateFormat.format(new Date());
        clockLabel.setText(time);
    }
    
    // Add this new method to handle quantity adjustment
    private void adjustItemQuantity(int row) {
        String itemName = (String) cartTableModel.getValueAt(row, 0);
        String priceStr = (String) cartTableModel.getValueAt(row, 1).toString().replace("₱", "");
        int currentQty = Integer.parseInt(cartTableModel.getValueAt(row, 2).toString());
        
        // Create quantity adjustment dialog
        JDialog dialog = new JDialog(this, "Adjust Quantity", true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel itemLabel = new JLabel("Item: " + itemName);
        
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton decreaseBtn = new JButton("-");
        JTextField quantityField = new JTextField(String.valueOf(currentQty), 3);
        quantityField.setHorizontalAlignment(JTextField.CENTER);
        JButton increaseBtn = new JButton("+");
        
        decreaseBtn.addActionListener(e -> {
            int qty = Integer.parseInt(quantityField.getText());
            if (qty > 1) {
                quantityField.setText(String.valueOf(qty - 1));
            }
        });
        
        increaseBtn.addActionListener(e -> {
            int qty = Integer.parseInt(quantityField.getText());
            quantityField.setText(String.valueOf(qty + 1));
        });
        
        quantityPanel.add(decreaseBtn);
        quantityPanel.add(quantityField);
        quantityPanel.add(increaseBtn);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel");
        JButton applyBtn = new JButton("Apply");
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        applyBtn.addActionListener(e -> {
            try {
                int newQty = Integer.parseInt(quantityField.getText());
                if (newQty <= 0) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Quantity must be greater than zero.", 
                        "Invalid Quantity", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Update the quantity in the table
                double price = Double.parseDouble(priceStr);
                double newTotal = price * newQty;
                
                cartTableModel.setValueAt(String.valueOf(newQty), row, 2);
                cartTableModel.setValueAt("₱" + String.format("%.2f", newTotal), row, 3);
                
                updateTotal();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please enter a valid number.", 
                    "Invalid Input", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(applyBtn);
        
        contentPanel.add(itemLabel, BorderLayout.NORTH);
        contentPanel.add(quantityPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(contentPanel);
        dialog.setVisible(true);
    }
    
    // Method to lookup a member
    private void lookupMember(String memberInput) {
        if (memberInput == null || memberInput.trim().isEmpty()) {
            resetMemberStatus();
            if (memberDetailsPanel != null) memberDetailsPanel.setVisible(false);
            return;
        }
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM customers WHERE (id = ? OR name = ? OR membership_id = ?) AND is_active = true")) {
            stmt.setString(1, memberInput);
            stmt.setString(2, memberInput);
            stmt.setString(3, memberInput);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                memberLoggedIn = true;
                memberID = rs.getString("id");
                memberName = rs.getString("name");
                memberPoints = rs.getInt("points");
                membershipLevel = rs.getString("membership_level");
                // Update the UI
                memberNameLabel.setText("Customer: " + memberName);
                memberLevelLabel.setText("Level: " + membershipLevel);
                memberPointsLabel.setText("Points: " + memberPoints);
                if (pointsLabel != null) pointsLabel.setText("Points: " + memberPoints);
                if (memberDetailsPanel != null) memberDetailsPanel.setVisible(true);
                usePointsCheckbox.setEnabled(memberPoints > 0);
                updateTotal();
                JOptionPane.showMessageDialog(this, "Customer selected: " + memberName + "\nPoints Available: " + memberPoints, "Customer Selected", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No customer found with ID, Name, or Membership ID: " + memberInput, "Customer Not Found", JOptionPane.WARNING_MESSAGE);
                resetMemberStatus();
                if (memberDetailsPanel != null) memberDetailsPanel.setVisible(false);
            }
        } catch (SQLException e) {
            Logger.getLogger(CashierDashboard.class.getName()).log(Level.SEVERE, "Error looking up customer", e);
            JOptionPane.showMessageDialog(this,
                "Error looking up customer: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void resetMemberStatus() {
        memberLoggedIn = false;
        memberName = "";
        memberID = "";  // Don't set WALKIN_CUSTOMER_ID here
        memberPoints = 0;
        discountRate = 0.0;
        usePointsForDiscount = false;
        if (usePointsCheckbox != null) {
            usePointsCheckbox.setSelected(false);
            usePointsCheckbox.setEnabled(false);
        }
        if (memberDetailsPanel != null) {
            memberDetailsPanel.setVisible(false);
        }
    }

    private String generateTransactionId() {
        return "T" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private void showMemberLookupDialog(JTextField memberField) {
        JDialog dialog = new JDialog(this, "Member Lookup", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Search field for filtering
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 30));
        panel.add(searchField, BorderLayout.NORTH);

        // Table for members
        String[] columns = {"Membership ID", "Name", "Points", "Level"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load all active members
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT membership_id, name, points, membership_level FROM customers WHERE is_active = true ORDER BY name")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getString("membership_id"),
                    rs.getString("name"),
                    rs.getInt("points"),
                    rs.getString("membership_level")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialog, "Error loading members: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        // Filter table as user types
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                String text = searchField.getText().trim().toLowerCase();
                for (int i = 0; i < model.getRowCount(); i++) {
                    boolean match = false;
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Object val = model.getValueAt(i, j);
                        if (val != null && val.toString().toLowerCase().contains(text)) {
                            match = true;
                            break;
                        }
                    }
                    table.setRowHeight(i, match ? 30 : 0);
                }
            }
        });

        // Scan/enter membership ID
        JTextField scanField = new JTextField();
        scanField.setPreferredSize(new Dimension(200, 30));
        scanField.setToolTipText("Scan or enter Membership ID");
        JPanel scanPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        scanPanel.add(new JLabel("Scan/Enter ID:"));
        scanPanel.add(scanField);
        panel.add(scanPanel, BorderLayout.SOUTH);

        // Handle scan/enter
        scanField.addActionListener(e -> {
            String input = scanField.getText().trim();
            for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getValueAt(i, 0).toString().equalsIgnoreCase(input)) {
                    table.setRowSelectionInterval(i, i);
                    selectMemberFromTable(table, model, i, memberField, dialog);
                    return;
                }
            }
            JOptionPane.showMessageDialog(dialog, "No member found with ID: " + input, "Not Found", JOptionPane.WARNING_MESSAGE);
        });

        // Double-click or Enter to select
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        selectMemberFromTable(table, model, row, memberField, dialog);
                    }
                }
            }
        });
        table.getInputMap().put(javax.swing.KeyStroke.getKeyStroke("ENTER"), "selectRow");
        table.getActionMap().put("selectRow", new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    selectMemberFromTable(table, model, row, memberField, dialog);
                }
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void selectMemberFromTable(JTable table, DefaultTableModel model, int row, JTextField memberField, JDialog dialog) {
        String membershipId = model.getValueAt(row, 0).toString();
        memberField.setText(membershipId);
        lookupMember(membershipId);
        dialog.dispose();
    }

    private String getMembershipIdFromCustomerId(String customerId) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT membership_id FROM customers WHERE id = ?")) {
            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("membership_id");
            }
        } catch (SQLException e) {
            Logger.getLogger(CashierDashboard.class.getName()).log(Level.WARNING, "Error getting membership_id", e);
        }
        return null;
    }
} 