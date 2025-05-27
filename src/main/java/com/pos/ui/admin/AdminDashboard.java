package com.pos.ui.admin;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.pos.db.DatabaseConnection;
import com.pos.db.PosDAO;
import com.pos.db.UserDAO;
import com.pos.model.User;
import com.pos.ui.admin.inventory.InventoryManagement;
import com.pos.ui.admin.reports.ReportsPanel;
import com.pos.ui.admin.settings.PrinterSettingsPanel;
import com.pos.util.FormatUtils;

public class AdminDashboard extends JFrame {
    private JPanel contentPanel;
    private String username;
    private JTable inventoryTable;
    private DefaultTableModel tableModel; // Add this field
    
    // Color scheme - same as CashierDashboard for consistency
    private final Color PRIMARY_COLOR = new Color(0, 123, 255); // Blue
    private final Color SECONDARY_COLOR = new Color(23, 162, 184); // Teal
    private final Color ACCENT_COLOR = new Color(255, 193, 7); // Yellow
    private final Color SUCCESS_COLOR = new Color(40, 167, 69); // Green
    private final Color DANGER_COLOR = new Color(220, 53, 69); // Red
    private final Color DARK_GRAY = new Color(33, 37, 41); // Dark Gray
    private final Color LIGHT_GRAY = new Color(248, 249, 250); // Light Gray
    private final Color WHITE = Color.WHITE;
    
    // Add fields for dashboard statistics
    private JLabel totalSalesLabel;
    private JLabel itemsInStockLabel;
    private JLabel lowStockItemsLabel;
    private JLabel transactionsLabel;
    private JPanel salesChartPanel;
    private JPanel productChartPanel;
    private static final int DASHBOARD_UPDATE_INTERVAL = 30000; // 30 seconds
    private Timer statsUpdateTimer;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private PrinterSettingsPanel printerSettingsPanel;
    private javax.swing.Timer ordersRefreshTimer;
    private JComboBox<String> periodSelector;
    
    public AdminDashboard(String username) {
        this.username = username;
        
        // Initialize panels
        printerSettingsPanel = new PrinterSettingsPanel();
        
        setTitle("QuickVend POS System - Admin Dashboard");
        setSize(1280, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_GRAY);
        
        // Create sidebar
        JPanel sidebarPanel = createSidebar();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        
        // Create content panel (initially shows dashboard)
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(WHITE);
        
        // Initial content is the dashboard summary
        showDashboardSummary();
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Set as content pane
        setContentPane(mainPanel);
        
        // Start the timer for updating statistics
        startStatsUpdateTimer();
    }
    
    // Method to start timer for updating dashboard statistics
    private void startStatsUpdateTimer() {
        statsUpdateTimer = new Timer();
        statsUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Update dashboard statistics only if the dashboard is currently showing
                if (totalSalesLabel != null && itemsInStockLabel != null && 
                    lowStockItemsLabel != null && transactionsLabel != null) {
                    SwingUtilities.invokeLater(() -> {
                        updateDashboardStats();
                    });
                }
            }
        }, 5000, 5000); // Update every 5 seconds
    }
    
    // Method to update dashboard statistics
    private void updateDashboardStats() {
        try {
            Map<String, Object> stats = PosDAO.getInstance().getDashboardStats();
            if (stats.containsKey("totalSales")) {
                totalSalesLabel.setText(FormatUtils.formatCurrency(BigDecimal.valueOf((Double) stats.get("totalSales"))));
            }
            if (stats.containsKey("itemsInStock")) {
                itemsInStockLabel.setText(String.valueOf(stats.get("itemsInStock")));
            }
            if (stats.containsKey("lowStockItems")) {
                lowStockItemsLabel.setText(String.valueOf(stats.get("lowStockItems")));
            }
            if (stats.containsKey("transactions")) {
                transactionsLabel.setText(String.valueOf(stats.get("transactions")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateSalesChart() {
        try {
            Map<String, Double> salesData = PosDAO.getInstance().getSalesData();
            Map<String, Number> chartData = new LinkedHashMap<>();
            for (Map.Entry<String, Double> entry : salesData.entrySet()) {
                chartData.put(entry.getKey(), entry.getValue());
            }
            if (salesChartPanel instanceof ChartPanel) {
                ((ChartPanel) salesChartPanel).updateData(chartData);
                salesChartPanel.repaint();
            }
        } catch (Exception e) {
            // Optionally log error
        }
    }
    
    private void updateProductChart() {
        try (java.sql.Connection conn = com.pos.db.DatabaseConnection.getInstance().getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(
                "SELECT i.item_name, COALESCE(SUM(ti.quantity), 0) as units_sold " +
                "FROM inventory i " +
                "LEFT JOIN transaction_items ti ON i.id = ti.item_id " +
                "LEFT JOIN transactions t ON ti.transaction_id = t.id AND t.status = 'Completed' " +
                "WHERE t.transaction_date >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
                "GROUP BY i.id, i.item_name " +
                "ORDER BY units_sold DESC " +
                "LIMIT 5"
             )) {
            java.util.Map<String, Number> chartData = new java.util.LinkedHashMap<>();
            java.sql.ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                chartData.put(rs.getString("item_name"), rs.getInt("units_sold"));
            }
            if (productChartPanel instanceof ChartPanel) {
                ((ChartPanel) productChartPanel).updateData(chartData);
                productChartPanel.repaint();
            }
        } catch (Exception e) {
            // Optionally log error
        }
    }
    
    // Replace the existing DashboardChartPanel class with a new ChartPanel class
    private class ChartPanel extends JPanel {
        private Map<String, Number> data = new HashMap<>();
        private final String title;
        private final Color color;
        private final String chartType; // "line", "bar", or "pie"

        public ChartPanel(String title, Color color, String chartType) {
            this.title = title;
            this.color = color;
            this.chartType = chartType;
            setBackground(WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)
            ));
        }

        public void updateData(Map<String, Number> newData) {
            this.data = newData;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw title
            g2d.setColor(DARK_GRAY);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString(title, 15, 20);

            if (data.isEmpty()) {
                g2d.drawString("No data available", getWidth() / 2 - 50, getHeight() / 2);
                g2d.dispose();
                return;
            }

            int padding = 40;
            int chartWidth = getWidth() - 2 * padding;
            int chartHeight = getHeight() - 3 * padding;
            int baselineY = getHeight() - padding;

            switch (chartType) {
                case "line":
                drawLineChart(g2d, chartWidth, chartHeight, baselineY, padding);
                    break;
                case "bar":
                    drawBarChart(g2d, chartWidth, chartHeight, baselineY, padding);
                    break;
                case "pie":
                    drawPieChart(g2d, chartWidth, chartHeight, baselineY, padding);
                    break;
            }

            g2d.dispose();
        }

        private void drawLineChart(Graphics2D g2d, int chartWidth, int chartHeight, int baselineY, int padding) {
            // Draw axes
            g2d.setColor(DARK_GRAY);
            g2d.drawLine(padding, baselineY, padding + chartWidth, baselineY); // X-axis
            g2d.drawLine(padding, padding, padding, baselineY); // Y-axis

            double maxValue = data.values().stream().mapToDouble(Number::doubleValue).max().orElse(1.0);

            int[] xPoints = new int[data.size()];
            int[] yPoints = new int[data.size()];
            int i = 0;
            for (Map.Entry<String, Number> entry : data.entrySet()) {
                double value = entry.getValue().doubleValue();
                xPoints[i] = padding + (chartWidth * i) / (data.size() - 1);
                yPoints[i] = baselineY - (int) ((value / maxValue) * chartHeight);
                g2d.setColor(color);
                g2d.fillOval(xPoints[i] - 4, yPoints[i] - 4, 8, 8);
                g2d.setColor(DARK_GRAY);
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                g2d.drawString(String.format("%.0f", value), xPoints[i] - 10, yPoints[i] - 10);
                g2d.drawString(entry.getKey(), xPoints[i] - 10, baselineY + 15);
                i++;
            }

            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(2));
            for (i = 0; i < xPoints.length - 1; i++) {
                g2d.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
            }
        }

        private void drawBarChart(Graphics2D g2d, int chartWidth, int chartHeight, int baselineY, int padding) {
            int barCount = data.size();
            int barWidth = Math.max(1, (chartWidth - (barCount + 1) * 10) / barCount);
            double maxValue = data.values().stream().mapToDouble(Number::doubleValue).max().orElse(1.0);

            int x = padding;
            for (Map.Entry<String, Number> entry : data.entrySet()) {
                double value = entry.getValue().doubleValue();
                int barHeight = (int) ((value / maxValue) * chartHeight);
                g2d.setColor(color);
                g2d.fillRect(x, baselineY - barHeight, barWidth, barHeight);
                g2d.setColor(DARK_GRAY);
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                String label = entry.getKey();
                if (label.length() > 10) {
                    label = label.substring(0, 7) + "...";
                }
                g2d.drawString(label, x, baselineY + 15);
                x += barWidth + 10;
            }
        }

        private void drawPieChart(Graphics2D g2d, int chartWidth, int chartHeight, int baselineY, int padding) {
            int diameter = Math.min(chartWidth, chartHeight) - 60;
            int x = (getWidth() - diameter) / 2;
            int y = 40;
            int total = data.values().stream().mapToInt(Number::intValue).sum();
            int startAngle = 0;
            Color[] pieColors = {PRIMARY_COLOR, SECONDARY_COLOR, ACCENT_COLOR, SUCCESS_COLOR, DANGER_COLOR, DARK_GRAY};
            int colorIdx = 0;
            for (Map.Entry<String, Number> entry : data.entrySet()) {
                int value = entry.getValue().intValue();
                int angle = (int) Math.round(360.0 * value / total);
                g2d.setColor(pieColors[colorIdx % pieColors.length]);
                g2d.fill(new Arc2D.Double(x, y, diameter, diameter, startAngle, angle, Arc2D.PIE));
                startAngle += angle;
                colorIdx++;
            }
            int legendY = y + diameter + 20;
            int legendX = x;
            colorIdx = 0;
            for (Map.Entry<String, Number> entry : data.entrySet()) {
                g2d.setColor(pieColors[colorIdx % pieColors.length]);
                g2d.fillRect(legendX, legendY, 16, 16);
                g2d.setColor(DARK_GRAY);
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                g2d.drawString(entry.getKey() + " (" + entry.getValue() + "%)", legendX + 22, legendY + 13);
                legendY += 22;
                colorIdx++;
            }
        }
    }
    
    private JPanel createSidebar() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(250, 0));
        sidebarPanel.setBackground(DARK_GRAY);
        sidebarPanel.setBorder(new EmptyBorder(20, 15, 20, 15));
        
        // Logo and branding panel
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBackground(DARK_GRAY);
        brandPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        brandPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Logo image
        JLabel logoLabel = new JLabel();
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            URL logoUrl = getClass().getResource("/icons/pos-logo.png");
            if (logoUrl != null) {
                ImageIcon icon = new ImageIcon(logoUrl);
                Image img = icon.getImage();
                Image resizedImg = img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(resizedImg));
            }
        } catch (Exception e) {
            System.out.println("Warning: Could not load logo image");
        }
        
        JLabel titleLabel = new JLabel("QUICKVEND");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("CLOUD - BASED POS SYSTEM");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        subtitleLabel.setForeground(LIGHT_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        brandPanel.add(logoLabel);
        brandPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        brandPanel.add(titleLabel);
        brandPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        brandPanel.add(subtitleLabel);
        
        // User info panel
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        userPanel.setBackground(new Color(44, 49, 54)); // Slightly lighter than sidebar
        userPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userPanel.setMaximumSize(new Dimension(250, 60));
        
        JLabel userIcon = new JLabel("\uD83D\uDC64"); // Unicode user icon
        userIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        userIcon.setForeground(WHITE);
        
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBackground(new Color(44, 49, 54));
        
        JLabel adminLabel = new JLabel("Admin");
        adminLabel.setFont(new Font("Arial", Font.BOLD, 14));
        adminLabel.setForeground(WHITE);
        
        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        usernameLabel.setForeground(LIGHT_GRAY);
        
        userInfoPanel.add(adminLabel);
        userInfoPanel.add(usernameLabel);
        
        userPanel.add(userIcon);
        userPanel.add(userInfoPanel);
        
        // Menu Sections
        JLabel menuHeaderLabel = new JLabel("MAIN MENU");
        menuHeaderLabel.setFont(new Font("Arial", Font.BOLD, 12));
        menuHeaderLabel.setForeground(LIGHT_GRAY);
        menuHeaderLabel.setBorder(new EmptyBorder(15, 10, 10, 0));
        menuHeaderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Menu buttons
        JButton dashboardBtn = createMenuButton("Dashboard", "/icons/dashboard.png");
        JButton salesBtn = createMenuButton("Sales/Checkout", "/icons/sales.png");
        JButton inventoryBtn = createMenuButton("Inventory", "/icons/inventory.png");
        JButton userMgmtBtn = createMenuButton("User Management", "/icons/users.png");
        JButton membersBtn = createMenuButton("Members", "/icons/users.png");
        JButton reportsBtn = createMenuButton("Reports", "/icons/reports.png");
        JButton settingsBtn = createMenuButton("Settings", "/icons/logout.png");
        JButton logoutBtn = createMenuButton("Logout", "/icons/logout.png");
        
        // Set active button style
        dashboardBtn.setBackground(PRIMARY_COLOR);
        dashboardBtn.setForeground(WHITE);
        userMgmtBtn.setBackground(PRIMARY_COLOR);
        userMgmtBtn.setForeground(WHITE);
        membersBtn.setBackground(PRIMARY_COLOR);
        membersBtn.setForeground(WHITE);
        
        // Add button actions
        dashboardBtn.addActionListener(e -> showDashboardSummary());
        salesBtn.addActionListener(e -> showSalesModule());
        inventoryBtn.addActionListener(e -> showInventoryModule());
        userMgmtBtn.addActionListener(e -> showUserManagementModule());
        membersBtn.addActionListener(e -> showMemberManagementModule());
        reportsBtn.addActionListener(e -> showReportsModule());
        settingsBtn.addActionListener(e -> showSettingsModule());
        logoutBtn.addActionListener(e -> logout());
        
        // Add components to sidebar
        sidebarPanel.add(brandPanel);
        sidebarPanel.add(userPanel);
        sidebarPanel.add(menuHeaderLabel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(dashboardBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(salesBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(inventoryBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(userMgmtBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(membersBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(reportsBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(settingsBtn);
        sidebarPanel.add(Box.createVerticalGlue()); // Push logout to bottom
        sidebarPanel.add(logoutBtn);
        
        return sidebarPanel;
    }
    
    private JButton createMenuButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(LIGHT_GRAY);
        button.setBackground(DARK_GRAY);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(250, 45));
        button.setPreferredSize(new Dimension(250, 45));
        
        if (iconPath != null && !iconPath.isEmpty()) {
            try {
                URL iconUrl = getClass().getResource(iconPath);
                if (iconUrl != null) {
                    ImageIcon icon = new ImageIcon(iconUrl);
                    Image img = icon.getImage();
                    Image resizedImg = img.getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                    button.setIcon(new ImageIcon(resizedImg));
                    button.setIconTextGap(10);
                }
            } catch (Exception e) {
                System.out.println("Warning: Could not load icon: " + iconPath);
            }
        }
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                if (button.getBackground() != PRIMARY_COLOR) {
                    button.setBackground(new Color(70, 70, 70));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent evt) {
                if (button.getBackground() != PRIMARY_COLOR) {
                    button.setBackground(DARK_GRAY);
                }
            }
        });
        
        return button;
    }
    
    // Show different modules
    private void showDashboardSummary() {
        contentPanel.removeAll();
        
        // Create header
        JPanel headerPanel = createModuleHeader("Dashboard Overview");
        
        // Create dashboard panel
        JPanel dashboardPanel = createDashboardPanel();
        
        // Add to content panel
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(dashboardPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
        // Update charts with fast data
        updateSalesChart();
        updateProductChart();
    }
    
    private void showSalesModule() {
        contentPanel.removeAll();
        
        // Create header
        JPanel headerPanel = createModuleHeader("Sales & Checkout");
        
        // Create sales module content
        JPanel salesPanel = new JPanel(new BorderLayout(20, 20));
        salesPanel.setBackground(WHITE);
        salesPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create the order panel
        JPanel orderPanel = new JPanel(new BorderLayout());
        orderPanel.setBackground(WHITE);
        orderPanel.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY));
        
        JPanel orderHeader = new JPanel(new BorderLayout());
        orderHeader.setBackground(PRIMARY_COLOR);
        orderHeader.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JLabel orderTitle = new JLabel("Current Orders");
        orderTitle.setFont(new Font("Arial", Font.BOLD, 16));
        orderTitle.setForeground(WHITE);
        
        orderHeader.add(orderTitle, BorderLayout.WEST);
        
        // Create table for orders
        String[] columns = {"Order ID", "Customer", "Items", "Total", "Status"};
        Object[][] data = {
            {"ORD-001", "John Smith", "3", "₱350.00", "Completed"},
            {"ORD-002", "Maria Garcia", "5", "₱720.50", "Processing"},
            {"ORD-003", "Robert Chen", "2", "₱150.25", "Pending"},
            {"ORD-004", "Sarah Johnson", "7", "₱980.00", "Completed"},
            {"ORD-005", "Walking Customer", "1", "₱100.00", "Completed"}
        };
        
        JTable orderTable = new JTable(data, columns);
        orderTable.setRowHeight(40);
        orderTable.setShowGrid(false);
        orderTable.setBackground(WHITE);
        
        JScrollPane tableScrollPane = new JScrollPane(orderTable);
        tableScrollPane.setBorder(null);
        
        orderPanel.add(orderHeader, BorderLayout.NORTH);
        orderPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Create action panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionPanel.setBackground(WHITE);
        actionPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton newOrderBtn = new JButton("New Order");
        newOrderBtn.setBackground(SUCCESS_COLOR);
        newOrderBtn.setForeground(WHITE);
        newOrderBtn.setFocusPainted(false);
        newOrderBtn.setBorderPainted(false);
        newOrderBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newOrderBtn.addActionListener(e -> showNewOrderDialog());
        
        JButton viewDetailsBtn = new JButton("View Details");
        viewDetailsBtn.setBackground(PRIMARY_COLOR);
        viewDetailsBtn.setForeground(WHITE);
        viewDetailsBtn.setFocusPainted(false);
        viewDetailsBtn.setBorderPainted(false);
        viewDetailsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        actionPanel.add(viewDetailsBtn);
        actionPanel.add(newOrderBtn);
        
        salesPanel.add(orderPanel, BorderLayout.CENTER);
        salesPanel.add(actionPanel, BorderLayout.SOUTH);
        
        // Add to content panel
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(salesPanel, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
        
        // Add timer for real-time refresh
        if (ordersRefreshTimer != null && ordersRefreshTimer.isRunning()) {
            ordersRefreshTimer.stop();
        }
        ordersRefreshTimer = new javax.swing.Timer(3000, e -> refreshOrdersTable(orderTable));
        ordersRefreshTimer.start();
    }
    
    private void refreshOrdersTable(JTable orderTable) {
        // Fetch real-time sales transactions from the database
        String[] columns = {"Order ID", "Customer", "Items", "Total", "Status"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        try (java.sql.Connection conn = com.pos.db.DatabaseConnection.getInstance().getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(
                "SELECT t.transaction_no, " +
                "COALESCE(c.name, 'Walking Customer') AS customer, " +
                "COUNT(ti.id) AS items, " +
                "t.total_amount, " +
                "t.status " +
                "FROM transactions t " +
                "LEFT JOIN customers c ON t.member_id = c.id " +
                "LEFT JOIN transaction_items ti ON t.id = ti.transaction_id " +
                "GROUP BY t.id " +
                "ORDER BY t.transaction_date DESC " +
                "LIMIT 100"
             )) {
            java.sql.ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getString("transaction_no"),
                    rs.getString("customer"),
                    rs.getInt("items"),
                    "₱" + rs.getBigDecimal("total_amount"),
                    rs.getString("status")
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            // Optionally log or show error
        }
        orderTable.setModel(model);
        orderTable.repaint();
    }
    
    private void showNewOrderDialog() {
        JDialog dialog = new JDialog(this, "New Order", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Customer selection
        JLabel customerLabel = new JLabel("Customer:");
        JComboBox<String> customerCombo = new JComboBox<>(new String[]{"Walking Customer", "John Smith", "Maria Garcia", "Robert Chen", "Sarah Johnson"});
        customerCombo.setSelectedIndex(0); // Default to Walking Customer
        
        panel.add(customerLabel);
        panel.add(customerCombo);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Add more order fields as needed...
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton createBtn = new JButton("Create Order");
        JButton cancelBtn = new JButton("Cancel");
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        createBtn.addActionListener(e -> {
            // Here you would create the order, using the selected customer
            String selectedCustomer = (String) customerCombo.getSelectedItem();
            // Add order to your order list/table, using 'Walking Customer' if selected
            JOptionPane.showMessageDialog(dialog, "Order created for: " + selectedCustomer);
            dialog.dispose();
        });
        buttonPanel.add(cancelBtn);
        buttonPanel.add(createBtn);
        
        panel.add(buttonPanel);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showInventoryModule() {
        contentPanel.removeAll();
        
        // Create header
        JPanel headerPanel = createModuleHeader("Inventory Management");
        
        // Create inventory management panel
        InventoryManagement inventoryPanel = new InventoryManagement();
        
        // Add to content panel
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(inventoryPanel, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showUserManagementModule() {
        contentPanel.removeAll();
        
        // Create header
        JPanel headerPanel = createModuleHeader("User Management");
        
        // Create main panel
        JPanel userPanel = new JPanel(new BorderLayout(10, 10));
        userPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        userPanel.setBackground(WHITE);
        
        // Create table model
        String[] columns = {"ID", "Username", "Full Name", "Role", "Status", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only allow editing of the Actions column
            }
        };
        
        // Create table
        JTable userTable = new JTable(model);
        userTable.setRowHeight(35);
        userTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        userTable.getColumnModel().getColumn(5).setCellEditor(
            new ButtonEditor(new JCheckBox(), model, this, "user"));
        
        // Add search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(WHITE);
        
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(PRIMARY_COLOR);
        searchButton.setForeground(WHITE);
        
        JComboBox<String> roleFilter = new JComboBox<>(new String[]{"All Roles", "Admin", "Cashier"});
        
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        searchPanel.add(new JLabel("Role: "));
        searchPanel.add(roleFilter);
        
        // Add action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(WHITE);
        
        JButton addButton = new JButton("Add New User");
        addButton.setBackground(SUCCESS_COLOR);
        addButton.setForeground(WHITE);
        
        actionPanel.add(addButton);
        
        // Create top panel for search and actions
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(WHITE);
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(actionPanel, BorderLayout.EAST);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Add components to user panel
        userPanel.add(topPanel, BorderLayout.NORTH);
        userPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add action listeners
        addButton.addActionListener(e -> showAddUserDialog(model));
        
        searchButton.addActionListener(e -> filterUsers(model, searchField.getText(), 
            (String)roleFilter.getSelectedItem()));
        
        roleFilter.addActionListener(e -> filterUsers(model, searchField.getText(), 
            (String)roleFilter.getSelectedItem()));
        
        // Load initial data
        loadUsers(model);
        
        // Add to content panel
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(userPanel, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showAddUserDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Add New User", true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create form fields
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField fullNameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Cashier", "Admin"});
        
        // Add form fields to panel
        panel.add(createFormField("Username:", usernameField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Password:", passwordField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Full Name:", fullNameField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Email:", emailField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Role:", roleCombo));
        
        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.setBackground(SUCCESS_COLOR);
        saveButton.setForeground(WHITE);

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonPanel);

        // Add action listeners
        cancelButton.addActionListener(e -> dialog.dispose());
        
        saveButton.addActionListener(e -> {
            try {
                // Validate input
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String fullName = fullNameField.getText().trim();
                String email = emailField.getText().trim();
                String role = (String) roleCombo.getSelectedItem();
                
                if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please fill in all fields",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

                // Create user
                User user = new User();
                user.setUsername(username);
                user.setFullName(fullName);
                user.setEmail(email);
                user.setRole(User.Role.fromString(role));
                user.setActive(true);
                
                UserDAO userDAO = new UserDAO();
                if (userDAO.createUser(user, password)) {
            JOptionPane.showMessageDialog(dialog,
                        "User created successfully",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
                    loadUsers(model);
            dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Failed to create user",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error creating user: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private JPanel createFormField(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        JLabel lbl = new JLabel(label);
        lbl.setPreferredSize(new Dimension(100, 25));
        panel.add(lbl, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }
    
    private void loadUsers(DefaultTableModel model) {
        try {
            UserDAO userDAO = new UserDAO();
            List<User> users = userDAO.getAllUsers();
            
            model.setRowCount(0);
            for (User user : users) {
            model.addRow(new Object[]{
                    user.getId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getRole().toString(),
                    user.isActive() ? "Active" : "Inactive",
                    "Edit"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading users: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void filterUsers(DefaultTableModel model, String searchTerm, String roleFilter) {
        try {
            UserDAO userDAO = new UserDAO();
            List<User> users = userDAO.getAllUsers();
            
            model.setRowCount(0);
            for (User user : users) {
                // Apply filters
                boolean matchesSearch = searchTerm.isEmpty() ||
                    user.getUsername().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    user.getFullName().toLowerCase().contains(searchTerm.toLowerCase());
                    
                boolean matchesRole = roleFilter.equals("All Roles") ||
                    user.getRole().toString().equals(roleFilter.toUpperCase());
                    
                if (matchesSearch && matchesRole) {
                    model.addRow(new Object[]{
                        user.getId(),
                        user.getUsername(),
                        user.getFullName(),
                        user.getRole().toString(),
                        user.isActive() ? "Active" : "Inactive",
                        "Edit"
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error filtering users: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Clean up timer when frame is closed
    @Override
    public void dispose() {
        if (statsUpdateTimer != null) {
            statsUpdateTimer.cancel();
        }
        super.dispose();
    }

    // Inner class for rendering buttons in table
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(PRIMARY_COLOR);
            setForeground(WHITE);
            setFocusPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Actions");
            return this;
        }
    }

    // Inner class for handling button clicks in table
    private class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private DefaultTableModel model;
        private AdminDashboard dashboard;
        private String type;
        private JTable currentTable;

        public ButtonEditor(JCheckBox checkBox, DefaultTableModel model, 
                          AdminDashboard dashboard, String type) {
            super(checkBox);
            this.model = model;
            this.dashboard = dashboard;
            this.type = type;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            currentTable = table;
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int viewRow = currentTable.getSelectedRow();
                int modelRow = currentTable.convertRowIndexToModel(viewRow);
                if (type.equals("user")) {
                    String userId = (String) model.getValueAt(modelRow, 0);
                    String username = (String) model.getValueAt(modelRow, 1);
                    String fullName = (String) model.getValueAt(modelRow, 2);
                    String role = (String) model.getValueAt(modelRow, 3);
                    boolean isActive = model.getValueAt(modelRow, 4).equals("Active");
                    showEditUserDialog(userId, username, fullName, role, isActive, model);
                } else if (type.equals("member")) {
                    String actions = (String) model.getValueAt(modelRow, 7);
                    if ("Edit/Delete".equals(actions) || "Edit/Delete".equals(label)) {
                        // Show popup menu for Edit/Delete
                        JPopupMenu popup = new JPopupMenu();
                        JMenuItem editItem = new JMenuItem("Edit");
                        JMenuItem deleteItem = new JMenuItem("Delete");
                        editItem.addActionListener(e -> dashboard.showEditMemberDialog(model, modelRow));
                        deleteItem.addActionListener(e -> dashboard.deleteMember(model, modelRow));
                        popup.add(editItem);
                        popup.add(deleteItem);
                        popup.show(button, button.getWidth()/2, button.getHeight()/2);
                    } else {
                        dashboard.showEditMemberDialog(model, modelRow);
                    }
                }
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    private void showEditUserDialog(String userId, String username, String fullName, 
                                  String role, boolean isActive, DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Edit User", true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create form fields
        JTextField usernameField = new JTextField(username, 20);
        usernameField.setEnabled(false); // Username cannot be changed
        JTextField fullNameField = new JTextField(fullName, 20);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Cashier", "Admin"});
        roleCombo.setSelectedItem(role);
        JCheckBox activeCheck = new JCheckBox("Active", isActive);
        
        // Add form fields to panel
        panel.add(createFormField("Username:", usernameField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Full Name:", fullNameField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Role:", roleCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Status:", activeCheck));
        
        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.setBackground(SUCCESS_COLOR);
        saveButton.setForeground(WHITE);
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonPanel);
        
        // Add action listeners
        cancelButton.addActionListener(e -> dialog.dispose());
        
        saveButton.addActionListener(e -> {
            try {
                // Get user from database
                UserDAO userDAO = new UserDAO();
                User user = userDAO.getUserById(userId);
                
                if (user != null) {
                    // Update user fields
                    user.setFullName(fullNameField.getText().trim());
                    user.setRole(User.Role.fromString(roleCombo.getSelectedItem().toString()));
                    user.setActive(activeCheck.isSelected());
                    
                    // Save changes
                    if (userDAO.updateUser(user)) {
                        JOptionPane.showMessageDialog(dialog,
                            "User updated successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        loadUsers(model);
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                            "Failed to update user",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error updating user: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private JPanel createModuleHeader(String title) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(DARK_GRAY);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        return headerPanel;
    }
    
    private JPanel createDashboardPanel() {
        JPanel dashboardPanel = new JPanel(new BorderLayout(20, 20));
        dashboardPanel.setBackground(WHITE);
        // Stats cards panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setBackground(WHITE);
        // Total Sales Card
        JPanel salesCard = createStatsCard("Total Sales", "₱0.00", PRIMARY_COLOR);
        totalSalesLabel = (JLabel) ((JPanel)salesCard.getComponent(1)).getComponent(0);
        // Items in Stock Card
        JPanel stockCard = createStatsCard("Items in Stock", "0", SECONDARY_COLOR);
        itemsInStockLabel = (JLabel) ((JPanel)stockCard.getComponent(1)).getComponent(0);
        // Low Stock Items Card
        JPanel lowStockCard = createStatsCard("Low Stock Items", "0", ACCENT_COLOR);
        lowStockItemsLabel = (JLabel) ((JPanel)lowStockCard.getComponent(1)).getComponent(0);
        // Transactions Card
        JPanel transactionsCard = createStatsCard("Transactions", "0", SUCCESS_COLOR);
        transactionsLabel = (JLabel) ((JPanel)transactionsCard.getComponent(1)).getComponent(0);
        statsPanel.add(salesCard);
        statsPanel.add(stockCard);
        statsPanel.add(lowStockCard);
        statsPanel.add(transactionsCard);
        // Charts panel
        JPanel chartsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        chartsPanel.setBackground(WHITE);
        // Sales Chart
        salesChartPanel = createChartPanel("Sales Overview", PRIMARY_COLOR);
        // Products Chart
        productChartPanel = createChartPanel("Top Products", SECONDARY_COLOR);
        // Category Pie Chart
        JPanel categoryPieChartPanel = new CategoryPieChartPanel();
        chartsPanel.add(salesChartPanel);
        chartsPanel.add(productChartPanel);
        chartsPanel.add(categoryPieChartPanel);
        // Period selector
        periodSelector = new JComboBox<>(new String[]{"Daily", "Weekly", "Monthly"});
        periodSelector.addActionListener(e -> updateDashboardStats());
        JPanel periodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        periodPanel.setBackground(WHITE);
        periodPanel.add(new JLabel("View:"));
        periodPanel.add(periodSelector);
        dashboardPanel.add(periodPanel, BorderLayout.NORTH);
        dashboardPanel.add(statsPanel, BorderLayout.NORTH);
        dashboardPanel.add(chartsPanel, BorderLayout.CENTER);
        return dashboardPanel;
    }
    
    private JPanel createStatsCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 5, 0, 0, color),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(DARK_GRAY);
        
        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        valuePanel.setBackground(WHITE);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(color);
        
        valuePanel.add(valueLabel);
        
        card.add(titleLabel);
        card.add(valuePanel);
        
        return card;
    }
    
    private JPanel createChartPanel(String title, Color color) {
        if (title.contains("Products")) {
            return new ChartPanel(title, color, "bar");
        } else if (title.contains("Category")) {
            return new ChartPanel(title, color, "pie");
        } else {
            return new ChartPanel(title, color, "line");
        }
    }
    
    private void showReportsModule() {
        contentPanel.removeAll();
        
        // Create header
        JPanel headerPanel = createModuleHeader("Reports & Analytics");
        
        // Create reports panel
        ReportsPanel reportsPanel = new ReportsPanel();
        
        // Add to content panel
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(reportsPanel, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showSettingsModule() {
        contentPanel.removeAll();
        
        // Create settings panel with tabs
        JTabbedPane settingsTabs = new JTabbedPane();
        settingsTabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Add printer settings tab
        settingsTabs.addTab("Printer & Receipt Settings", printerSettingsPanel);
        
        // Add other settings tabs here if needed
        
        contentPanel.add(createModuleHeader("Settings"), BorderLayout.NORTH);
        contentPanel.add(settingsTabs, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void logout() {
        // Clean up resources
        dispose();
        
        // Show login screen
        SwingUtilities.invokeLater(() -> {
            try {
                Class<?> loginScreenClass = Class.forName("com.pos.ui.auth.LoginScreen");
                JFrame loginScreen = (JFrame) loginScreenClass.getDeclaredConstructor().newInstance();
                loginScreen.setVisible(true);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | 
                     NoSuchMethodException | InvocationTargetException e) {
                JOptionPane.showMessageDialog(null,
                    "Error returning to login screen: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void showMemberManagementModule() {
        contentPanel.removeAll();
        
        // Create header
        JPanel headerPanel = createModuleHeader("Loyalty Membership Management");
        
        // Create main panel
        JPanel memberPanel = new JPanel(new BorderLayout(10, 10));
        memberPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        memberPanel.setBackground(WHITE);
        
        // Create table model
        String[] columns = {"Membership ID", "Name", "Phone", "Email", "Points", "Total Spent", "Status", "Actions", "ID"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only allow editing of the Actions column
            }
        };
        
        // Create table
        JTable memberTable = new JTable(model);
        memberTable.setRowHeight(35);
        memberTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        memberTable.getColumnModel().getColumn(7).setCellEditor(
            new ButtonEditor(new JCheckBox(), model, this, "member"));
        
        // Add search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(WHITE);
        
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(PRIMARY_COLOR);
        searchButton.setForeground(WHITE);
        
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"All", "Active", "Inactive"});
        
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        searchPanel.add(new JLabel("Status: "));
        searchPanel.add(statusFilter);
        
        // Add action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(WHITE);
        
        JButton addButton = new JButton("Add New Member");
        addButton.setBackground(SUCCESS_COLOR);
        addButton.setForeground(WHITE);
        
        actionPanel.add(addButton);
        
        // Create top panel for search and actions
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(WHITE);
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(actionPanel, BorderLayout.EAST);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(memberTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Add components to member panel
        memberPanel.add(topPanel, BorderLayout.NORTH);
        memberPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add action listeners
        addButton.addActionListener(e -> showAddMemberDialog(model));
        
        searchButton.addActionListener(e -> filterMembers(model, searchField.getText(), 
            (String)statusFilter.getSelectedItem()));
        
        statusFilter.addActionListener(e -> filterMembers(model, searchField.getText(), 
            (String)statusFilter.getSelectedItem()));
        
        // Load initial data
        loadMembers(model);
        
        // Add to content panel
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(memberPanel, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void loadMembers(DefaultTableModel model) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM customers WHERE is_active = true ORDER BY name")) {
            
            ResultSet rs = stmt.executeQuery();
            model.setRowCount(0); // Clear existing data
            
            while (rs.next()) {
                Object[] row = {
                    rs.getString("membership_id"),
                    rs.getString("name"),
                    rs.getString("contact_number"),
                    rs.getString("email"),
                    rs.getBigDecimal("points"),
                    FormatUtils.formatCurrency(rs.getBigDecimal("total_spent")),
                    rs.getBoolean("is_active") ? "Active" : "Inactive",
                    "Edit/Delete",
                    rs.getString("id")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, "Error loading members", e);
            JOptionPane.showMessageDialog(this,
                "Error loading members: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void filterMembers(DefaultTableModel model, String searchTerm, String status) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            StringBuilder sql = new StringBuilder(
                "SELECT * FROM customers WHERE 1=1");
            
            if (!searchTerm.isEmpty()) {
                sql.append(" AND (name LIKE ? OR contact_number LIKE ? OR email LIKE ? OR membership_id LIKE ?)");
            }
            
            if (!status.equals("All")) {
                sql.append(" AND is_active = ?");
            }
            
            sql.append(" ORDER BY name");
            
            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                int paramIndex = 1;
                
                if (!searchTerm.isEmpty()) {
                    String searchPattern = "%" + searchTerm + "%";
                    stmt.setString(paramIndex++, searchPattern);
                    stmt.setString(paramIndex++, searchPattern);
                    stmt.setString(paramIndex++, searchPattern);
                    stmt.setString(paramIndex++, searchPattern);
                }
                
                if (!status.equals("All")) {
                    stmt.setBoolean(paramIndex, status.equals("Active"));
                }
                
                ResultSet rs = stmt.executeQuery();
                model.setRowCount(0); // Clear existing data
                
                while (rs.next()) {
                    Object[] row = {
                        rs.getString("membership_id"),
                        rs.getString("name"),
                        rs.getString("contact_number"),
                        rs.getString("email"),
                        rs.getBigDecimal("points"),
                        FormatUtils.formatCurrency(rs.getBigDecimal("total_spent")),
                        rs.getBoolean("is_active") ? "Active" : "Inactive",
                        "Edit/Delete",
                        rs.getString("id")
                    };
                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, "Error filtering members", e);
            JOptionPane.showMessageDialog(this,
                "Error filtering members: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddMemberDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Add New Member", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create form fields
        JTextField membershipIdField = new JTextField(generateMembershipId(), 20);
        membershipIdField.setEditable(true);
        JTextField nameField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField addressField = new JTextField(20);
        JComboBox<String> levelCombo = new JComboBox<>(new String[]{"Regular", "Silver", "Gold", "Platinum"});
        
        // Add form fields to panel
        panel.add(createFormField("Membership ID:", membershipIdField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Name:", nameField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Phone:", phoneField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Email:", emailField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Address:", addressField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Level:", levelCombo));
        
        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        saveButton.setBackground(SUCCESS_COLOR);
        saveButton.setForeground(WHITE);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonPanel);
        
        // Add action listeners
        saveButton.addActionListener(e -> {
            try {
                String membershipId = membershipIdField.getText().trim();
                String name = nameField.getText().trim();
                String phone = phoneField.getText().trim();
                String email = emailField.getText().trim();
                String address = addressField.getText().trim();
                String level = (String) levelCombo.getSelectedItem();
                if (name.isEmpty() || membershipId.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Name and Membership ID are required", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try (Connection conn = DatabaseConnection.getInstance().getConnection();
                     PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO customers (id, membership_id, name, contact_number, email, address, points, total_spent, membership_level, join_date, valid_until, is_active) " +
                         "VALUES (?, ?, ?, ?, ?, ?, 0, 0, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR), true)")
                ) {
                    stmt.setString(1, UUID.randomUUID().toString());
                    stmt.setString(2, membershipId);
                    stmt.setString(3, name);
                    stmt.setString(4, phone);
                    stmt.setString(5, email);
                    stmt.setString(6, address);
                    stmt.setString(7, level);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, "Member added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadMembers(model);
                }
            } catch (SQLException ex) {
                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, "Error adding member", ex);
                JOptionPane.showMessageDialog(dialog,
                    "Error adding member: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dialog.dispose());
        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Add this method to generate a unique membership ID
    private String generateMembershipId() {
        // Generate a random 7-digit number as a string (ensure uniqueness in production)
        return String.valueOf(1000000 + (int)(Math.random() * 9000000));
    }

    // Add this method to handle editing a member
    private void showEditMemberDialog(DefaultTableModel model, int row) {
        String membershipId = (String) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 1);
        String phone = (String) model.getValueAt(row, 2);
        String email = (String) model.getValueAt(row, 3);
        String status = (String) model.getValueAt(row, 6);

        JDialog dialog = new JDialog(this, "Edit Member", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField membershipIdField = new JTextField(membershipId, 20);
        JTextField nameField = new JTextField(name, 20);
        JTextField phoneField = new JTextField(phone, 20);
        JTextField emailField = new JTextField(email, 20);
        JCheckBox activeCheck = new JCheckBox("Active", status.equals("Active"));

        panel.add(createFormField("Membership ID:", membershipIdField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Name:", nameField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Phone:", phoneField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Email:", emailField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Status:", activeCheck));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        saveButton.setBackground(SUCCESS_COLOR);
        saveButton.setForeground(WHITE);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonPanel);

        saveButton.addActionListener(e -> {
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE customers SET membership_id = ?, name = ?, contact_number = ?, email = ?, is_active = ? WHERE id = ?")) {
                stmt.setString(1, membershipIdField.getText().trim());
                stmt.setString(2, nameField.getText().trim());
                stmt.setString(3, phoneField.getText().trim());
                stmt.setString(4, emailField.getText().trim());
                stmt.setBoolean(5, activeCheck.isSelected());
                String id = (String) model.getValueAt(row, 8);
                stmt.setString(6, id);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Member updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadMembers(model);
            } catch (SQLException ex) {
                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, "Error updating member", ex);
                JOptionPane.showMessageDialog(dialog,
                    "Error updating member: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dialog.dispose());
        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Pie chart panel for product categories
    private class CategoryPieChartPanel extends JPanel {
        private Map<String, Integer> categoryData;
        public CategoryPieChartPanel() {
            setPreferredSize(new Dimension(300, 300));
            setBackground(WHITE);
            fetchCategoryData();
        }
        private void fetchCategoryData() {
            try {
                categoryData = com.pos.db.PosDAO.getInstance().getProductCategoryData();
            } catch (Exception e) {
                categoryData = new HashMap<>();
            }
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();
            int diameter = Math.min(width, height) - 60;
            int x = (width - diameter) / 2;
            int y = 40;
            // Draw title
            g2d.setColor(DARK_GRAY);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("Top Products by Category", x, 30);
            if (categoryData == null || categoryData.isEmpty()) {
                g2d.drawString("No data available", x, y + diameter / 2);
                g2d.dispose();
                return;
            }
            int total = categoryData.values().stream().mapToInt(Integer::intValue).sum();
            int startAngle = 0;
            Color[] pieColors = {PRIMARY_COLOR, SECONDARY_COLOR, ACCENT_COLOR, SUCCESS_COLOR, DANGER_COLOR, DARK_GRAY};
            int colorIdx = 0;
            for (Map.Entry<String, Integer> entry : categoryData.entrySet()) {
                int value = entry.getValue();
                int angle = (int) Math.round(360.0 * value / total);
                g2d.setColor(pieColors[colorIdx % pieColors.length]);
                g2d.fill(new Arc2D.Double(x, y, diameter, diameter, startAngle, angle, Arc2D.PIE));
                startAngle += angle;
                colorIdx++;
            }
            // Draw legend
            int legendY = y + diameter + 20;
            int legendX = x;
            colorIdx = 0;
            for (Map.Entry<String, Integer> entry : categoryData.entrySet()) {
                g2d.setColor(pieColors[colorIdx % pieColors.length]);
                g2d.fillRect(legendX, legendY, 16, 16);
                g2d.setColor(DARK_GRAY);
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                g2d.drawString(entry.getKey() + " (" + entry.getValue() + "%)", legendX + 22, legendY + 13);
                legendY += 22;
                colorIdx++;
            }
            g2d.dispose();
        }
    }

    public void deleteMember(DefaultTableModel model, int row) {
        String memberId = (String) model.getValueAt(row, 8); // ID column
        String memberName = (String) model.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete member '" + memberName + "'?\nThis action cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "UPDATE customers SET is_active = false WHERE id = ?")) {
            stmt.setString(1, memberId);
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(this, "Member deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMembers(model);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete member.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, "Error deleting member", e);
            JOptionPane.showMessageDialog(this, "Error deleting member: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 