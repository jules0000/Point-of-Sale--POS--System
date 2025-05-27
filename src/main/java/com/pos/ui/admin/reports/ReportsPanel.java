package com.pos.ui.admin.reports;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.pos.db.DatabaseConnection;

public class ReportsPanel extends JPanel {
    private final JTabbedPane mainTabs;
    // Sales sub-tabs
    private JTabbedPane salesTabs;
    private JTable dailySalesTable, productSalesTable;
    private DefaultTableModel dailySalesModel, productSalesModel;
    private JComboBox<String> dailyTimePeriodCombo, productTimePeriodCombo;
    private JLabel dailyTotalLabel, productTotalLabel;
    // Inventory sub-tabs
    private JTabbedPane inventoryTabs;
    private JTable stockSummaryTable, lowStockTable, valuationTable, deadStockTable;
    private DefaultTableModel stockSummaryModel, lowStockModel, valuationModel, deadStockModel;
    private JLabel stockSummaryTotalLabel, lowStockTotalLabel, valuationTotalLabel, deadStockTotalLabel;
    // EOD
    private JTable eodTable;
    private DefaultTableModel eodModel;
    private JLabel eodTotalLabel;
    private JPanel eodPanel;
    // Date range for custom filters
    private Date customStartDate, customEndDate;
    
    public ReportsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        mainTabs = new JTabbedPane();
        setupSalesTabs();
        setupInventoryTabs();
        setupEODTab();
        mainTabs.addTab("Sales Reports", salesTabs);
        mainTabs.addTab("Inventory Reports", inventoryTabs);
        mainTabs.addTab("End of Day Report", eodPanel);
        add(mainTabs, BorderLayout.CENTER);
    }

    // --- Sales Reports Tabs ---
    private void setupSalesTabs() {
        salesTabs = new JTabbedPane();
        salesTabs.addTab("Daily/Weekly/Monthly", createDailySalesPanel());
        salesTabs.addTab("Product-wise Sales", createProductSalesPanel());
    }
    private JPanel createDailySalesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dailyTimePeriodCombo = new JComboBox<>(new String[]{
            "Today", "Yesterday", "This Week", "Last Week", "This Month", "Last Month", "Custom Range"
        });
        JButton filterBtn = new JButton("Generate Report");
        filterPanel.add(new JLabel("Time Period:"));
        filterPanel.add(dailyTimePeriodCombo);
        filterPanel.add(filterBtn);
        panel.add(filterPanel, BorderLayout.NORTH);
        dailySalesModel = new DefaultTableModel();
        dailySalesTable = new JTable(dailySalesModel);
        JScrollPane scrollPane = new JScrollPane(dailySalesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        dailyTotalLabel = new JLabel("Total: ₱0.00");
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(dailyTotalLabel);
        panel.add(totalPanel, BorderLayout.SOUTH);
        filterBtn.addActionListener(e -> generateSalesReport((String) dailyTimePeriodCombo.getSelectedItem(), dailySalesModel, dailyTotalLabel));
        // Initial load
        generateSalesReport("Today", dailySalesModel, dailyTotalLabel);
        return panel;
    }
    private JPanel createProductSalesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        productTimePeriodCombo = new JComboBox<>(new String[]{
            "Today", "Yesterday", "This Week", "Last Week", "This Month", "Last Month", "Custom Range"
        });
        JButton filterBtn = new JButton("Generate Report");
        filterPanel.add(new JLabel("Time Period:"));
        filterPanel.add(productTimePeriodCombo);
        filterPanel.add(filterBtn);
        panel.add(filterPanel, BorderLayout.NORTH);
        productSalesModel = new DefaultTableModel();
        productSalesTable = new JTable(productSalesModel);
        JScrollPane scrollPane = new JScrollPane(productSalesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        productTotalLabel = new JLabel("Total: ₱0.00");
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(productTotalLabel);
        panel.add(totalPanel, BorderLayout.SOUTH);
        filterBtn.addActionListener(e -> generateProductPerformanceReport((String) productTimePeriodCombo.getSelectedItem(), productSalesModel, productTotalLabel));
        // Initial load
        generateProductPerformanceReport("Today", productSalesModel, productTotalLabel);
        return panel;
    }
    
    // --- Inventory Reports Tabs ---
    private void setupInventoryTabs() {
        inventoryTabs = new JTabbedPane();
        inventoryTabs.addTab("Stock Summary", createStockSummaryPanel());
        inventoryTabs.addTab("Low Stock", createLowStockPanel());
        inventoryTabs.addTab("Valuation", createValuationPanel());
        inventoryTabs.addTab("Dead Stock", createDeadStockPanel());
    }
    private JPanel createStockSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        stockSummaryModel = new DefaultTableModel();
        stockSummaryTable = new JTable(stockSummaryModel);
        JScrollPane scrollPane = new JScrollPane(stockSummaryTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        stockSummaryTotalLabel = new JLabel("Total: ₱0.00");
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(stockSummaryTotalLabel);
        panel.add(totalPanel, BorderLayout.SOUTH);
        generateInventoryReport(stockSummaryModel, stockSummaryTotalLabel);
        return panel;
    }
    private JPanel createLowStockPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        lowStockModel = new DefaultTableModel();
        lowStockTable = new JTable(lowStockModel);
        JScrollPane scrollPane = new JScrollPane(lowStockTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        lowStockTotalLabel = new JLabel("Total: 0");
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(lowStockTotalLabel);
        panel.add(totalPanel, BorderLayout.SOUTH);
        generateLowStockReport(lowStockModel, lowStockTotalLabel);
        return panel;
    }
    private JPanel createValuationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        valuationModel = new DefaultTableModel();
        valuationTable = new JTable(valuationModel);
        JScrollPane scrollPane = new JScrollPane(valuationTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        valuationTotalLabel = new JLabel("Total Value: ₱0.00");
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(valuationTotalLabel);
        panel.add(totalPanel, BorderLayout.SOUTH);
        generateInventoryValuationReport(valuationModel, valuationTotalLabel);
        return panel;
    }
    private JPanel createDeadStockPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        deadStockModel = new DefaultTableModel();
        deadStockTable = new JTable(deadStockModel);
        JScrollPane scrollPane = new JScrollPane(deadStockTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        deadStockTotalLabel = new JLabel("Total Dead Stock: 0");
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(deadStockTotalLabel);
        panel.add(totalPanel, BorderLayout.SOUTH);
        generateDeadStockReport(deadStockModel, deadStockTotalLabel);
        return panel;
    }

    // --- End of Day Report Tab ---
    private void setupEODTab() {
        eodPanel = new JPanel(new BorderLayout());
        eodModel = new DefaultTableModel();
        eodTable = new JTable(eodModel);
        JScrollPane scrollPane = new JScrollPane(eodTable);
        eodPanel.add(scrollPane, BorderLayout.CENTER);
        eodTotalLabel = new JLabel("Total Sales: ₱0.00");
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(eodTotalLabel);
        eodPanel.add(totalPanel, BorderLayout.SOUTH);
        generateEODReport(eodModel, eodTotalLabel);
    }

    // --- Report Generation Methods (adapted for each model/label) ---
    private void generateSalesReport(String timePeriod, DefaultTableModel model, JLabel totalLabel) {
        String[] columns = {"Sale ID", "Date/Time", "Cashier", "Items", "Total", "Payment Method"};
        model.setDataVector(new Object[][]{}, columns);
        String dateFilter = getDateFilterClause(timePeriod);
        String sql = "SELECT t.id, t.transaction_date, u.username, " +
                    "COUNT(ti.id) as item_count, t.total_amount, t.payment_method " +
                    "FROM transactions t " +
                    "LEFT JOIN users u ON t.cashier_id = u.id " +
                    "LEFT JOIN transaction_items ti ON t.id = ti.transaction_id " +
                    "WHERE t.status = 'Completed' " + dateFilter + " " +
                    "GROUP BY t.id " +
                    "ORDER BY t.transaction_date DESC";
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String saleId = rs.getString("id");
                String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(rs.getTimestamp("transaction_date"));
                String cashier = rs.getString("username");
                if (cashier == null || cashier.trim().isEmpty()) cashier = "Unknown";
                int itemCount = rs.getInt("item_count");
                java.math.BigDecimal saleTotal = rs.getBigDecimal("total_amount");
                String paymentMethod = rs.getString("payment_method");
                total = total.add(saleTotal);
                Object[] row = {saleId, dateTime, cashier, itemCount, String.format("₱%.2f", saleTotal), paymentMethod};
                model.addRow(row);
            }
            totalLabel.setText(String.format("Total Sales: ₱%.2f", total));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void generateProductPerformanceReport(String timePeriod, DefaultTableModel model, JLabel totalLabel) {
        String[] columns = {"Product ID", "Name", "Category", "Units Sold", "Total Revenue", "Total Cost", "Profit", "Profit Margin %", "Stock Level"};
        model.setDataVector(new Object[][]{}, columns);
        String dateFilter = getDateFilterClause(timePeriod, "t");
        String sql = "SELECT " +
                "i.id, " +
                "i.item_name as name, " +
                "i.category, " +
                "COALESCE(SUM(ti.quantity), 0) as units_sold, " +
                "COALESCE(SUM(ti.quantity * ti.unit_price), 0) as revenue, " +
                "COALESCE(SUM(ti.quantity * i.cost_price), 0) as cost, " +
                "i.quantity as stock " +
                "FROM inventory i " +
                "LEFT JOIN transaction_items ti ON i.id = ti.item_id " +
                "LEFT JOIN transactions t ON ti.transaction_id = t.id AND t.status = 'Completed' " + dateFilter + " " +
                "GROUP BY i.id " +
                "ORDER BY revenue DESC";
        java.math.BigDecimal totalRevenue = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalProfit = java.math.BigDecimal.ZERO;
        int totalUnitsSold = 0;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String productId = rs.getString("id");
                String name = rs.getString("name");
                String category = rs.getString("category");
                int unitsSold = rs.getInt("units_sold");
                java.math.BigDecimal revenue = rs.getBigDecimal("revenue");
                java.math.BigDecimal cost = rs.getBigDecimal("cost");
                int stockLevel = rs.getInt("stock");
                java.math.BigDecimal profit = revenue.subtract(cost);
                double profitMargin = revenue.compareTo(java.math.BigDecimal.ZERO) > 0 ?
                        profit.divide(revenue, 4, java.math.RoundingMode.HALF_UP).multiply(new java.math.BigDecimal(100)).doubleValue() : 0.0;
                Object[] row = {productId, name, category, unitsSold, String.format("₱%.2f", revenue), String.format("₱%.2f", cost), String.format("₱%.2f", profit), String.format("%.2f%%", profitMargin), stockLevel};
                model.addRow(row);
                totalRevenue = totalRevenue.add(revenue);
                totalProfit = totalProfit.add(profit);
                totalUnitsSold += unitsSold;
            }
            totalLabel.setText(String.format("Total Units Sold: %d | Total Revenue: ₱%.2f | Total Profit: ₱%.2f", totalUnitsSold, totalRevenue, totalProfit));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error generating product performance report: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void generateInventoryReport(DefaultTableModel model, JLabel totalLabel) {
        String[] columns = {"ID", "Barcode", "Product Name", "Category", "Price", "Stock", "Stock Value"};
        model.setDataVector(new Object[][]{}, columns);
        String sql = "SELECT * FROM inventory ORDER BY category, item_name";
        java.math.BigDecimal totalValue = java.math.BigDecimal.ZERO;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("id");
                String barcode = rs.getString("barcode");
                String name = rs.getString("item_name");
                String category = rs.getString("category");
                java.math.BigDecimal price = rs.getBigDecimal("price");
                int stock = rs.getInt("quantity");
                java.math.BigDecimal stockValue = price.multiply(new java.math.BigDecimal(stock));
                Object[] row = {id, barcode, name, category, "₱" + price, stock, "₱" + stockValue};
                model.addRow(row);
                totalValue = totalValue.add(stockValue);
            }
            totalLabel.setText("Total Inventory Value: ₱" + totalValue);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error generating inventory report: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void generateLowStockReport(DefaultTableModel model, JLabel totalLabel) {
        String[] columns = {"ID", "Barcode", "Product Name", "Category", "Current Stock", "Threshold", "Status"};
        model.setDataVector(new Object[][]{}, columns);
        String sql = "SELECT *, CASE WHEN reorder_level IS NULL OR reorder_level = 0 THEN 10 ELSE reorder_level END AS threshold " +
                     "FROM inventory WHERE quantity <= (CASE WHEN reorder_level IS NULL OR reorder_level = 0 THEN 10 ELSE reorder_level END) " +
                     "ORDER BY (quantity / (CASE WHEN reorder_level IS NULL OR reorder_level = 0 THEN 10 ELSE reorder_level END))";
        int lowStockCount = 0;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("id");
                String barcode = rs.getString("barcode");
                String name = rs.getString("item_name");
                String category = rs.getString("category");
                int stock = rs.getInt("quantity");
                int threshold = rs.getInt("threshold");
                String status;
                if (stock == 0) {
                    status = "OUT OF STOCK";
                } else {
                    double ratio = (double) stock / threshold;
                    if (ratio <= 0.3) {
                        status = "CRITICAL";
                    } else if (ratio <= 0.7) {
                        status = "LOW";
                    } else {
                        status = "REORDER SOON";
                    }
                }
                Object[] row = {id, barcode, name, category, stock, threshold, status};
                model.addRow(row);
                lowStockCount++;
            }
            totalLabel.setText("Total Low Stock Items: " + lowStockCount);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error generating low stock report: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void generateInventoryValuationReport(DefaultTableModel model, JLabel totalLabel) {
        String[] columns = {"ID", "Product Name", "Category", "Current Stock", "Unit Cost", "Total Value"};
        model.setDataVector(new Object[][]{}, columns);
        String sql = "SELECT id, item_name, category, quantity, cost_price FROM inventory ORDER BY category, item_name";
        java.math.BigDecimal totalValue = java.math.BigDecimal.ZERO;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("item_name");
                String category = rs.getString("category");
                int stock = rs.getInt("quantity");
                java.math.BigDecimal unitCost = rs.getBigDecimal("cost_price");
                java.math.BigDecimal value = unitCost.multiply(new java.math.BigDecimal(stock));
                Object[] row = {id, name, category, stock, "₱" + unitCost, "₱" + value};
                model.addRow(row);
                totalValue = totalValue.add(value);
            }
            totalLabel.setText("Total Inventory Valuation: ₱" + totalValue);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error generating inventory valuation report: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void generateDeadStockReport(DefaultTableModel model, JLabel totalLabel) {
        String[] columns = {"ID", "Product Name", "Category", "Current Stock", "Last Sold Date", "Days Since Last Sale"};
        model.setDataVector(new Object[][]{}, columns);
        String sql = "SELECT i.id, i.item_name, i.category, i.quantity, " +
                "MAX(t.transaction_date) as last_sold " +
                "FROM inventory i " +
                "LEFT JOIN transaction_items ti ON i.id = ti.item_id " +
                "LEFT JOIN transactions t ON ti.transaction_id = t.id AND t.status = 'Completed' " +
                "GROUP BY i.id, i.item_name, i.category, i.quantity " +
                "HAVING last_sold IS NULL OR DATEDIFF(CURRENT_DATE, last_sold) > 30 " +
                "ORDER BY last_sold IS NULL DESC, last_sold ASC";
        int deadStockCount = 0;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("item_name");
                String category = rs.getString("category");
                int stock = rs.getInt("quantity");
                Date lastSold = rs.getTimestamp("last_sold");
                String lastSoldStr = lastSold == null ? "Never" : new SimpleDateFormat("yyyy-MM-dd").format(lastSold);
                long daysSince = lastSold == null ? -1 : (new Date().getTime() - lastSold.getTime()) / (1000 * 60 * 60 * 24);
                Object[] row = {id, name, category, stock, lastSoldStr, daysSince == -1 ? "Never" : daysSince};
                model.addRow(row);
                deadStockCount++;
            }
            // If no results, try showing all items for debug
            if (deadStockCount == 0) {
                // Optionally, you can comment this block out in production
                String sqlAll = "SELECT i.id, i.item_name, i.category, i.quantity, " +
                        "MAX(t.transaction_date) as last_sold " +
                        "FROM inventory i " +
                        "LEFT JOIN transaction_items ti ON i.id = ti.item_id " +
                        "LEFT JOIN transactions t ON ti.transaction_id = t.id AND t.status = 'Completed' " +
                        "GROUP BY i.id, i.item_name, i.category, i.quantity " +
                        "ORDER BY last_sold IS NULL DESC, last_sold ASC";
                try (PreparedStatement stmt2 = conn.prepareStatement(sqlAll);
                     ResultSet rs2 = stmt2.executeQuery()) {
                    while (rs2.next()) {
                        String id = rs2.getString("id");
                        String name = rs2.getString("item_name");
                        String category = rs2.getString("category");
                        int stock = rs2.getInt("quantity");
                        Date lastSold = rs2.getTimestamp("last_sold");
                        String lastSoldStr = lastSold == null ? "Never" : new SimpleDateFormat("yyyy-MM-dd").format(lastSold);
                        long daysSince = lastSold == null ? -1 : (new Date().getTime() - lastSold.getTime()) / (1000 * 60 * 60 * 24);
                        Object[] row = {id, name, category, stock, lastSoldStr, daysSince == -1 ? "Never" : daysSince};
                        model.addRow(row);
                        deadStockCount++;
                    }
                }
            }
            totalLabel.setText("Total Dead Stock: " + deadStockCount);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error generating dead stock report: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void generateEODReport(DefaultTableModel model, JLabel totalLabel) {
        String[] columns = {"Sale ID", "Date/Time", "Cashier", "Total", "Payment Method"};
        model.setDataVector(new Object[][]{}, columns);
        String sql = "SELECT t.id, t.transaction_date, u.username, t.total_amount, t.payment_method " +
                "FROM transactions t " +
                "LEFT JOIN users u ON t.cashier_id = u.id " +
                "WHERE t.status = 'Completed' AND DATE(t.transaction_date) = CURRENT_DATE " +
                "ORDER BY t.transaction_date DESC";
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String saleId = rs.getString("id");
                String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(rs.getTimestamp("transaction_date"));
                String cashier = rs.getString("username");
                if (cashier == null || cashier.trim().isEmpty()) cashier = "Unknown";
                java.math.BigDecimal saleTotal = rs.getBigDecimal("total_amount");
                String paymentMethod = rs.getString("payment_method");
                total = total.add(saleTotal);
                Object[] row = {saleId, dateTime, cashier, String.format("₱%.2f", saleTotal), paymentMethod};
                model.addRow(row);
            }
            totalLabel.setText(String.format("Total Sales: ₱%.2f", total));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error generating EOD report: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private String getDateFilterClause(String timePeriod) {
        return getDateFilterClause(timePeriod, null);
    }
    private String getDateFilterClause(String timePeriod, String alias) {
        if (alias == null || alias.isEmpty()) alias = "t";
        switch (timePeriod) {
            case "Today":
                return "AND DATE(" + alias + ".transaction_date) = CURRENT_DATE";
            case "Yesterday":
                return "AND DATE(" + alias + ".transaction_date) = DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY)";
            case "This Week":
                return "AND YEARWEEK(" + alias + ".transaction_date, 1) = YEARWEEK(CURRENT_DATE, 1)";
            case "Last Week":
                return "AND YEARWEEK(" + alias + ".transaction_date, 1) = YEARWEEK(DATE_SUB(CURRENT_DATE, INTERVAL 1 WEEK), 1)";
            case "This Month":
                return "AND YEAR(" + alias + ".transaction_date) = YEAR(CURRENT_DATE) " +
                        "AND MONTH(" + alias + ".transaction_date) = MONTH(CURRENT_DATE)";
            case "Last Month":
                return "AND YEAR(" + alias + ".transaction_date) = YEAR(DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH)) " +
                        "AND MONTH(" + alias + ".transaction_date) = MONTH(DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH))";
            case "Custom Range":
                if (customStartDate != null && customEndDate != null) {
                    return String.format(
                            "AND DATE(" + alias + ".transaction_date) BETWEEN '%s' AND '%s'",
                            new SimpleDateFormat("yyyy-MM-dd").format(customStartDate),
                            new SimpleDateFormat("yyyy-MM-dd").format(customEndDate)
                    );
                }
            default:
                return ""; // All time - no filter
        }
    }
} 