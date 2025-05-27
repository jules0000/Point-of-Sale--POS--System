package com.pos.ui.admin.transactions;

import com.pos.db.DatabaseConnection;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;

public class TransactionPanel extends JPanel {
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterComboBox;
    private JTextField searchField;
    private JLabel totalLabel;
    
    public TransactionPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create components
        createTopPanel();
        createTablePanel();
        createBottomPanel();
        
        // Load initial data
        loadTransactions("All");
    }
    
    private void createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchTransactions());
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        String[] filters = {"All", "Today", "This Week", "This Month"};
        filterComboBox = new JComboBox<>(filters);
        filterComboBox.addActionListener(e -> loadTransactions((String)filterComboBox.getSelectedItem()));
        
        filterPanel.add(new JLabel("Filter:"));
        filterPanel.add(filterComboBox);
        
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(filterPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
    }
    
    private void createTablePanel() {
        String[] columns = {
            "Transaction ID", "Date/Time", "Cashier", "Customer", 
            "Items", "Total", "Payment Method", "Status"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        transactionTable = new JTable(tableModel);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.setRowHeight(25);
        
        // Set column widths
        transactionTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        transactionTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        transactionTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        transactionTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        transactionTable.getColumnModel().getColumn(4).setPreferredWidth(50);
        transactionTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        transactionTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        transactionTable.getColumnModel().getColumn(7).setPreferredWidth(80);
        
        // Add double-click listener to show transaction details
        transactionTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showTransactionDetails();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        // Total sales label
        totalLabel = new JLabel("Total Sales: ₱0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bottomPanel.add(totalLabel, BorderLayout.WEST);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton viewButton = new JButton("View Details");
        viewButton.addActionListener(e -> showTransactionDetails());
        
        JButton exportButton = new JButton("Export");
        exportButton.addActionListener(e -> exportTransactions());
        
        buttonPanel.add(viewButton);
        buttonPanel.add(exportButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void loadTransactions(String filter) {
        tableModel.setRowCount(0);
        double total = 0.0;
        
        String sql = "SELECT t.id, t.transaction_date, u.username as cashier, " +
                    "m.name as customer, COUNT(ti.id) as item_count, " +
                    "t.total_amount, t.payment_method, t.status " +
                    "FROM transactions t " +
                    "LEFT JOIN users u ON t.cashier_id = u.id " +
                    "LEFT JOIN customers m ON t.customer_id = m.id " +
                    "LEFT JOIN transaction_items ti ON t.id = ti.transaction_id " +
                    "WHERE 1=1 ";
        
        // Add date filter
        switch (filter) {
            case "Today":
                sql += "AND DATE(t.transaction_date) = CURRENT_DATE ";
                break;
            case "This Week":
                sql += "AND YEARWEEK(t.transaction_date, 1) = YEARWEEK(CURRENT_DATE, 1) ";
                break;
            case "This Month":
                sql += "AND YEAR(t.transaction_date) = YEAR(CURRENT_DATE) " +
                       "AND MONTH(t.transaction_date) = MONTH(CURRENT_DATE) ";
                break;
        }
        
        sql += "GROUP BY t.id ORDER BY t.transaction_date DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String transactionId = rs.getString("id");
                String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(rs.getTimestamp("transaction_date"));
                String cashier = rs.getString("cashier");
                String customer = rs.getString("customer");
                int itemCount = rs.getInt("item_count");
                double amount = rs.getDouble("total_amount");
                String paymentMethod = rs.getString("payment_method");
                String status = rs.getString("status");
                
                total += amount;
                
                Object[] row = {
                    transactionId,
                    dateTime,
                    cashier,
                    customer != null ? customer : "Walk-in",
                    itemCount,
                    String.format("₱%.2f", amount),
                    paymentMethod,
                    status
                };
                
                tableModel.addRow(row);
            }
            
            totalLabel.setText(String.format("Total Sales: ₱%.2f", total));
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading transactions: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchTransactions() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadTransactions((String)filterComboBox.getSelectedItem());
            return;
        }
        
        tableModel.setRowCount(0);
        double total = 0.0;
        
        String sql = "SELECT t.id, t.transaction_date, u.username as cashier, " +
                    "m.name as customer, COUNT(ti.id) as item_count, " +
                    "t.total_amount, t.payment_method, t.status " +
                    "FROM transactions t " +
                    "LEFT JOIN users u ON t.cashier_id = u.id " +
                    "LEFT JOIN customers m ON t.customer_id = m.id " +
                    "LEFT JOIN transaction_items ti ON t.id = ti.transaction_id " +
                    "WHERE t.id LIKE ? OR u.username LIKE ? OR m.name LIKE ? " +
                    "GROUP BY t.id ORDER BY t.transaction_date DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String pattern = "%" + searchTerm + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String transactionId = rs.getString("id");
                String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(rs.getTimestamp("transaction_date"));
                String cashier = rs.getString("cashier");
                String customer = rs.getString("customer");
                int itemCount = rs.getInt("item_count");
                double amount = rs.getDouble("total_amount");
                String paymentMethod = rs.getString("payment_method");
                String status = rs.getString("status");
                
                total += amount;
                
                Object[] row = {
                    transactionId,
                    dateTime,
                    cashier,
                    customer != null ? customer : "Walk-in",
                    itemCount,
                    String.format("₱%.2f", amount),
                    paymentMethod,
                    status
                };
                
                tableModel.addRow(row);
            }
            
            totalLabel.setText(String.format("Total Sales: ₱%.2f", total));
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error searching transactions: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showTransactionDetails() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a transaction to view details.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String transactionId = (String) tableModel.getValueAt(selectedRow, 0);
        new TransactionDetailsDialog(SwingUtilities.getWindowAncestor(this), transactionId);
    }
    
    private void exportTransactions() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Transactions");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filePath = file.getPath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                file = new File(filePath + ".csv");
            }
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                // Write headers
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    writer.write(tableModel.getColumnName(i));
                    if (i < tableModel.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
                
                // Write data
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        String value = tableModel.getValueAt(i, j).toString();
                        // Remove currency symbol and quotes for CSV
                        value = value.replace("₱", "").replace("\"", "\"\"");
                        writer.write("\"" + value + "\"");
                        if (j < tableModel.getColumnCount() - 1) {
                            writer.write(",");
                        }
                    }
                    writer.newLine();
                }
                
                JOptionPane.showMessageDialog(this,
                    "Transactions exported successfully to:\n" + file.getAbsolutePath(),
                    "Export Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                    "Error exporting transactions: " + e.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 