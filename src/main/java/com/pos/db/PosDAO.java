package com.pos.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object (DAO) for the POS system
 * Handles all database queries and updates
 */
public class PosDAO {
    private static PosDAO instance;
    private final DatabaseConnection dbConnection;
    
    private PosDAO() {
        dbConnection = DatabaseConnection.getInstance();
    }
    
    public static synchronized PosDAO getInstance() {
        if (instance == null) {
            instance = new PosDAO();
        }
        return instance;
    }
    
    /**
     * Get dashboard statistics
     * @return Map containing statistics (totalSales, itemsInStock, lowStockItems, transactions)
     */
    public Map<String, Object> getDashboardStats() throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        
        try (Connection conn = dbConnection.getConnection()) {
            // Get today's total sales
            String salesQuery = "SELECT COALESCE(SUM(total_amount), 0) AS total_sales FROM transactions WHERE DATE(transaction_date) = CURDATE()";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(salesQuery)) {
                if (rs.next()) {
                    stats.put("totalSales", rs.getDouble("total_sales"));
                }
            }
            
            // Get total items in stock
            String stockQuery = "SELECT COUNT(*) AS total_items FROM inventory WHERE quantity > 0";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(stockQuery)) {
                if (rs.next()) {
                    stats.put("itemsInStock", rs.getInt("total_items"));
                }
            }
            
            // Get low stock items count
            String lowStockQuery = "SELECT COUNT(*) AS low_stock FROM inventory WHERE quantity > 0 AND quantity <= 10";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(lowStockQuery)) {
                if (rs.next()) {
                    stats.put("lowStockItems", rs.getInt("low_stock"));
                }
            }
            
            // Get today's transaction count
            String transactionQuery = "SELECT COUNT(*) AS total_transactions FROM transactions WHERE DATE(transaction_date) = CURDATE()";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(transactionQuery)) {
                if (rs.next()) {
                    stats.put("transactions", rs.getInt("total_transactions"));
                }
            }
        }
        
        return stats;
    }
    
    /**
     * Get sales data for charting
     * @return Map of day -> sales amount for the last 7 days
     */
    public Map<String, Double> getSalesData() throws SQLException {
        Map<String, Double> salesData = new HashMap<>();
        
        try (Connection conn = dbConnection.getConnection()) {
            String query = "SELECT DATE_FORMAT(transaction_date, '%a') AS day, SUM(total_amount) AS daily_sales " +
                           "FROM transactions " +
                           "WHERE transaction_date >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
                           "GROUP BY DATE(transaction_date) " +
                           "ORDER BY transaction_date";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    salesData.put(rs.getString("day"), rs.getDouble("daily_sales"));
                }
            }
        }
        
        return salesData;
    }
    
    /**
     * Get product category distribution data
     * @return Map of category -> sales percentage
     */
    public Map<String, Integer> getProductCategoryData() throws SQLException {
        Map<String, Integer> categoryData = new HashMap<>();
        
        try (Connection conn = dbConnection.getConnection()) {
            String query = "SELECT category, COUNT(*) * 100 / (SELECT COUNT(*) FROM inventory) AS percentage " +
                           "FROM inventory " +
                           "GROUP BY category";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    categoryData.put(rs.getString("category"), rs.getInt("percentage"));
                }
            }
        }
        
        return categoryData;
    }
    
    /**
     * Get inventory items
     * @return List of inventory items as Maps
     */
    public List<Map<String, Object>> getInventoryItems() throws SQLException {
        List<Map<String, Object>> items = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection()) {
            String query = "SELECT * FROM inventory ORDER BY item_name";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", rs.getString("id"));
                    item.put("name", rs.getString("item_name"));
                    item.put("category", rs.getString("category"));
                    item.put("price", rs.getDouble("price"));
                    item.put("quantity", rs.getInt("quantity"));
                    
                    // Determine status based on quantity
                    String status = "In Stock";
                    int quantity = rs.getInt("quantity");
                    if (quantity <= 0) {
                        status = "Out of Stock";
                    } else if (quantity <= 10) {
                        status = "Low Stock";
                    }
                    item.put("status", status);
                    
                    items.add(item);
                }
            }
        }
        
        return items;
    }
    
    /**
     * Add a new inventory item
     * @param item Map containing item details
     * @return true if successful, false otherwise
     */
    public boolean addInventoryItem(Map<String, Object> item) {
        try (Connection conn = dbConnection.getConnection()) {
            String query = "INSERT INTO inventory (id, item_name, category, price, quantity, description, barcode, is_active) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, (String) item.get("id"));
                pstmt.setString(2, (String) item.get("name"));
                pstmt.setString(3, (String) item.get("category"));
                pstmt.setDouble(4, (Double) item.get("price"));
                pstmt.setInt(5, (Integer) item.get("quantity"));
                pstmt.setString(6, (String) item.get("description"));
                pstmt.setString(7, (String) item.get("barcode"));
                pstmt.setBoolean(8, item.get("is_active") != null ? (Boolean) item.get("is_active") : true);
                
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error adding inventory item: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update an existing inventory item
     * @param item Map containing updated item details
     * @return true if successful, false otherwise
     */
    public boolean updateInventoryItem(Map<String, Object> item) {
        try (Connection conn = dbConnection.getConnection()) {
            String query = "UPDATE inventory SET item_name = ?, category = ?, price = ?, " +
                           "quantity = ?, description = ?, barcode = ? WHERE id = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, (String) item.get("name"));
                pstmt.setString(2, (String) item.get("category"));
                pstmt.setDouble(3, (Double) item.get("price"));
                pstmt.setInt(4, (Integer) item.get("quantity"));
                pstmt.setString(5, (String) item.get("description"));
                pstmt.setString(6, (String) item.get("barcode"));
                pstmt.setString(7, (String) item.get("id"));
                
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating inventory item: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete an inventory item
     * @param itemId ID of the item to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteInventoryItem(String itemId) {
        try (Connection conn = dbConnection.getConnection()) {
            String query = "DELETE FROM inventory WHERE id = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, itemId);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting inventory item: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get all users
     * @return List of users as Maps
     */
    public List<Map<String, Object>> getUsers() {
        List<Map<String, Object>> users = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection()) {
            String query = "SELECT * FROM users ORDER BY username";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    Map<String, Object> user = new HashMap<>();
                    user.put("id", rs.getString("id"));
                    user.put("username", rs.getString("username"));
                    user.put("fullName", rs.getString("full_name"));
                    user.put("role", rs.getString("role"));
                    user.put("email", rs.getString("email"));
                    user.put("active", rs.getBoolean("active"));
                    
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving users: " + e.getMessage());
        }
        
        return users;
    }
    
    /**
     * Get all customers
     * @return List of customers as Maps
     */
    public List<Map<String, Object>> getCustomers() {
        List<Map<String, Object>> customers = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection()) {
            String query = "SELECT m.*, SUM(t.total_amount) AS total_spent " +
                          "FROM customers m LEFT JOIN transactions t ON m.id = t.customer_id " +
                          "GROUP BY m.id ORDER BY m.name";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    Map<String, Object> customer = new HashMap<>();
                    customer.put("id", rs.getString("id"));
                    customer.put("name", rs.getString("name"));
                    customer.put("level", rs.getString("level"));
                    customer.put("points", rs.getInt("points"));
                    customer.put("totalSpent", rs.getDouble("total_spent"));
                    customer.put("joinDate", rs.getDate("join_date"));
                    
                    customers.add(customer);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving customers: " + e.getMessage());
        }
        
        return customers;
    }
    
    /**
     * Get customer by ID
     * @param customerId Customer ID
     * @return Customer data as Map, or null if not found
     */
    public Map<String, Object> getCustomerById(String customerId) {
        try (Connection conn = dbConnection.getConnection()) {
            String query = "SELECT * FROM customers WHERE id = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, customerId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Map<String, Object> customer = new HashMap<>();
                        customer.put("id", rs.getString("id"));
                        customer.put("name", rs.getString("name"));
                        customer.put("level", rs.getString("level"));
                        customer.put("points", rs.getInt("points"));
                        customer.put("email", rs.getString("email"));
                        customer.put("phone", rs.getString("phone"));
                        customer.put("address", rs.getString("address"));
                        customer.put("joinDate", rs.getDate("join_date"));
                        
                        return customer;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving customer: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Update customer points
     * @param customerId Customer ID to update
     * @param points New points value
     * @return true if successful, false otherwise
     */
    public boolean updateCustomerPoints(String customerId, int points) {
        try (Connection conn = dbConnection.getConnection()) {
            String query = "UPDATE customers SET points = ? WHERE id = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, points);
                pstmt.setString(2, customerId);
                
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating customer points: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get profit report for a specific time period
     * @param startDate Start date in format 'YYYY-MM-DD'
     * @param endDate End date in format 'YYYY-MM-DD'
     * @return Map containing profit details
     */
    public Map<String, Object> getProfitReport(String startDate, String endDate) throws SQLException {
        Map<String, Object> report = new HashMap<>();
        
        try (Connection conn = dbConnection.getConnection()) {
            String query = "SELECT " +
                          "SUM(t.total_amount) as total_sales, " +
                          "SUM(ti.quantity * i.cost_price) as total_cost, " +
                          "SUM(t.total_amount - (ti.quantity * i.cost_price)) as gross_profit, " +
                          "COUNT(DISTINCT t.id) as total_transactions " +
                          "FROM transactions t " +
                          "JOIN transaction_items ti ON t.id = ti.transaction_id " +
                          "JOIN inventory i ON ti.item_id = i.id " +
                          "WHERE t.transaction_date BETWEEN ? AND ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, startDate);
                pstmt.setString(2, endDate);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        report.put("totalSales", rs.getDouble("total_sales"));
                        report.put("totalCost", rs.getDouble("total_cost"));
                        report.put("grossProfit", rs.getDouble("gross_profit"));
                        report.put("totalTransactions", rs.getInt("total_transactions"));
                        
                        // Calculate profit margin
                        double totalSales = rs.getDouble("total_sales");
                        double grossProfit = rs.getDouble("gross_profit");
                        double profitMargin = totalSales > 0 ? (grossProfit / totalSales) * 100 : 0;
                        report.put("profitMargin", profitMargin);
                    }
                }
            }
        }
        
        return report;
    }
    
    /**
     * Get transaction report with detailed items
     * @param startDate Start date in format 'YYYY-MM-DD'
     * @param endDate End date in format 'YYYY-MM-DD'
     * @return List of transaction details
     */
    public List<Map<String, Object>> getTransactionReport(String startDate, String endDate) throws SQLException {
        List<Map<String, Object>> transactions = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection()) {
            String query = "SELECT t.*, " +
                          "u.username as cashier_name, " +
                          "m.name as customer_name, " +
                          "GROUP_CONCAT(CONCAT(i.item_name, ' (', ti.quantity, ')') SEPARATOR ', ') as items " +
                          "FROM transactions t " +
                          "LEFT JOIN users u ON t.cashier_id = u.id " +
                          "LEFT JOIN customers m ON t.customer_id = m.id " +
                          "JOIN transaction_items ti ON t.id = ti.transaction_id " +
                          "JOIN inventory i ON ti.item_id = i.id " +
                          "WHERE t.transaction_date BETWEEN ? AND ? " +
                          "GROUP BY t.id " +
                          "ORDER BY t.transaction_date DESC";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, startDate);
                pstmt.setString(2, endDate);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> transaction = new HashMap<>();
                        transaction.put("id", rs.getString("id"));
                        transaction.put("date", rs.getTimestamp("transaction_date"));
                        transaction.put("totalAmount", rs.getDouble("total_amount"));
                        transaction.put("paymentMethod", rs.getString("payment_method"));
                        transaction.put("cashierName", rs.getString("cashier_name"));
                        transaction.put("customerName", rs.getString("customer_name"));
                        transaction.put("items", rs.getString("items"));
                        transactions.add(transaction);
                    }
                }
            }
        }
        
        return transactions;
    }
    
    /**
     * Get sales report by category
     * @param startDate Start date in format 'YYYY-MM-DD'
     * @param endDate End date in format 'YYYY-MM-DD'
     * @return List of category sales details
     */
    public List<Map<String, Object>> getCategorySalesReport(String startDate, String endDate) throws SQLException {
        List<Map<String, Object>> categorySales = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection()) {
            String query = "SELECT i.category, " +
                          "COUNT(DISTINCT t.id) as transaction_count, " +
                          "SUM(ti.quantity) as total_quantity, " +
                          "SUM(ti.quantity * ti.price) as total_sales " +
                          "FROM transactions t " +
                          "JOIN transaction_items ti ON t.id = ti.transaction_id " +
                          "JOIN inventory i ON ti.item_id = i.id " +
                          "WHERE t.transaction_date BETWEEN ? AND ? " +
                          "GROUP BY i.category " +
                          "ORDER BY total_sales DESC";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, startDate);
                pstmt.setString(2, endDate);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> category = new HashMap<>();
                        category.put("category", rs.getString("category"));
                        category.put("transactionCount", rs.getInt("transaction_count"));
                        category.put("totalQuantity", rs.getInt("total_quantity"));
                        category.put("totalSales", rs.getDouble("total_sales"));
                        categorySales.add(category);
                    }
                }
            }
        }
        
        return categorySales;
    }
    
    /**
     * Get top selling products report
     * @param startDate Start date in format 'YYYY-MM-DD'
     * @param endDate End date in format 'YYYY-MM-DD'
     * @param limit Number of top products to return
     * @return List of top selling products
     */
    public List<Map<String, Object>> getTopProductsReport(String startDate, String endDate, int limit) throws SQLException {
        List<Map<String, Object>> topProducts = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection()) {
            String query = "SELECT i.id, i.item_name, i.category, " +
                          "SUM(ti.quantity) as total_quantity, " +
                          "SUM(ti.quantity * ti.price) as total_sales " +
                          "FROM transactions t " +
                          "JOIN transaction_items ti ON t.id = ti.transaction_id " +
                          "JOIN inventory i ON ti.item_id = i.id " +
                          "WHERE t.transaction_date BETWEEN ? AND ? " +
                          "GROUP BY i.id, i.item_name, i.category " +
                          "ORDER BY total_sales DESC " +
                          "LIMIT ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, startDate);
                pstmt.setString(2, endDate);
                pstmt.setInt(3, limit);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> product = new HashMap<>();
                        product.put("id", rs.getString("id"));
                        product.put("name", rs.getString("item_name"));
                        product.put("category", rs.getString("category"));
                        product.put("totalQuantity", rs.getInt("total_quantity"));
                        product.put("totalSales", rs.getDouble("total_sales"));
                        topProducts.add(product);
                    }
                }
            }
        }
        
        return topProducts;
    }
    
    /**
     * Get daily sales report
     * @param date Date in format 'YYYY-MM-DD'
     * @return Map containing daily sales details
     */
    public Map<String, Object> getDailySalesReport(String date) throws SQLException {
        Map<String, Object> report = new HashMap<>();
        
        try (Connection conn = dbConnection.getConnection()) {
            String query = "SELECT " +
                          "COUNT(DISTINCT t.id) as transaction_count, " +
                          "SUM(t.total_amount) as total_sales, " +
                          "AVG(t.total_amount) as average_sale, " +
                          "MIN(t.total_amount) as min_sale, " +
                          "MAX(t.total_amount) as max_sale " +
                          "FROM transactions t " +
                          "WHERE DATE(t.transaction_date) = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, date);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        report.put("date", date);
                        report.put("transactionCount", rs.getInt("transaction_count"));
                        report.put("totalSales", rs.getDouble("total_sales"));
                        report.put("averageSale", rs.getDouble("average_sale"));
                        report.put("minSale", rs.getDouble("min_sale"));
                        report.put("maxSale", rs.getDouble("max_sale"));
                    }
                }
            }
        }
        
        return report;
    }
    
    /**
     * Get weekly sales report
     * @param startDate Start date of the week in format 'YYYY-MM-DD'
     * @return List of daily sales for the week
     */
    public List<Map<String, Object>> getWeeklySalesReport(String startDate) throws SQLException {
        List<Map<String, Object>> weeklyReport = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection()) {
            String query = "SELECT " +
                          "DATE(t.transaction_date) as sale_date, " +
                          "COUNT(DISTINCT t.id) as transaction_count, " +
                          "SUM(t.total_amount) as total_sales " +
                          "FROM transactions t " +
                          "WHERE t.transaction_date BETWEEN ? AND DATE_ADD(?, INTERVAL 6 DAY) " +
                          "GROUP BY DATE(t.transaction_date) " +
                          "ORDER BY sale_date";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, startDate);
                pstmt.setString(2, startDate);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> dailySales = new HashMap<>();
                        dailySales.put("date", rs.getDate("sale_date"));
                        dailySales.put("transactionCount", rs.getInt("transaction_count"));
                        dailySales.put("totalSales", rs.getDouble("total_sales"));
                        weeklyReport.add(dailySales);
                    }
                }
            }
        }
        
        return weeklyReport;
    }
    
    /**
     * Get monthly sales report
     * @param year Year (e.g., 2024)
     * @param month Month (1-12)
     * @return List of daily sales for the month
     */
    public List<Map<String, Object>> getMonthlySalesReport(int year, int month) throws SQLException {
        List<Map<String, Object>> monthlyReport = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection()) {
            String query = "SELECT " +
                          "DATE(t.transaction_date) as sale_date, " +
                          "COUNT(DISTINCT t.id) as transaction_count, " +
                          "SUM(t.total_amount) as total_sales " +
                          "FROM transactions t " +
                          "WHERE YEAR(t.transaction_date) = ? AND MONTH(t.transaction_date) = ? " +
                          "GROUP BY DATE(t.transaction_date) " +
                          "ORDER BY sale_date";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, year);
                pstmt.setInt(2, month);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> dailySales = new HashMap<>();
                        dailySales.put("date", rs.getDate("sale_date"));
                        dailySales.put("transactionCount", rs.getInt("transaction_count"));
                        dailySales.put("totalSales", rs.getDouble("total_sales"));
                        monthlyReport.add(dailySales);
                    }
                }
            }
        }
        
        return monthlyReport;
    }
} 