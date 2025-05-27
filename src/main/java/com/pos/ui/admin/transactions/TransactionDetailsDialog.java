package com.pos.ui.admin.transactions;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.pos.db.DatabaseConnection;

public class TransactionDetailsDialog extends JDialog {
    private String transactionId;
    private DefaultTableModel itemsTableModel;
    
    public TransactionDetailsDialog(Window owner, String transactionId) {
        super(owner, "Transaction Details", ModalityType.APPLICATION_MODAL);
        this.transactionId = transactionId;
        
        setSize(600, 500);
        setLocationRelativeTo(owner);
        
        // Create main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Add components
        contentPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        contentPanel.add(createItemsPanel(), BorderLayout.CENTER);
        contentPanel.add(createTotalsPanel(), BorderLayout.SOUTH);
        
        setContentPane(contentPanel);
        
        // Load transaction data
        loadTransactionData();
        
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 5));
        panel.setBorder(new CompoundBorder(
            new TitledBorder("Transaction Information"),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        // These labels will be populated when loading data
        panel.add(new JLabel("Transaction ID:"));
        panel.add(new JLabel(transactionId));
        
        panel.add(new JLabel("Date/Time:"));
        panel.add(new JLabel());
        
        panel.add(new JLabel("Cashier:"));
        panel.add(new JLabel());
        
        panel.add(new JLabel("Customer:"));
        panel.add(new JLabel());
        
        panel.add(new JLabel("Payment Method:"));
        panel.add(new JLabel());
        
        panel.add(new JLabel("Status:"));
        panel.add(new JLabel());
        
        return panel;
    }
    
    private JPanel createItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new CompoundBorder(
            new TitledBorder("Items"),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        // Create table
        String[] columns = {"Item", "Quantity", "Unit Price", "Discount", "Total"};
        itemsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable itemsTable = new JTable(itemsTableModel);
        itemsTable.setRowHeight(25);
        
        // Set column widths
        itemsTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        itemsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        itemsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        itemsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        itemsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(itemsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTotalsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 5));
        panel.setBorder(new CompoundBorder(
            new TitledBorder("Totals"),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        // These labels will be populated when loading data
        panel.add(new JLabel("Subtotal:"));
        panel.add(new JLabel());
        
        panel.add(new JLabel("Tax (10%):"));
        panel.add(new JLabel());
        
        panel.add(new JLabel("Total Discount:"));
        panel.add(new JLabel());
        
        panel.add(new JLabel("Total:"));
        panel.add(new JLabel());
        
        return panel;
    }
    
    private void loadTransactionData() {
        String sql = "SELECT t.*, u.username as cashier, m.name as customer " +
                    "FROM transactions t " +
                    "LEFT JOIN users u ON t.cashier_id = u.id " +
                    "LEFT JOIN customers m ON t.customer_id = m.id " +
                    "WHERE t.id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, transactionId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Update header panel
                JPanel headerPanel = (JPanel) getContentPane().getComponent(0);
                
                // Date/Time
                ((JLabel)headerPanel.getComponent(3)).setText(
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(rs.getTimestamp("transaction_date"))
                );
                
                // Cashier
                ((JLabel)headerPanel.getComponent(5)).setText(rs.getString("cashier"));
                
                // Customer
                String customer = rs.getString("customer");
                ((JLabel)headerPanel.getComponent(7)).setText(customer != null ? customer : "Walk-in");
                
                // Payment Method
                ((JLabel)headerPanel.getComponent(9)).setText(rs.getString("payment_method"));
                
                // Status
                ((JLabel)headerPanel.getComponent(11)).setText(rs.getString("status"));
                
                // Load items
                loadTransactionItems();
                
                // Update totals
                updateTotals(rs.getDouble("total_amount"));
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading transaction details: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadTransactionItems() {
        String sql = "SELECT i.item_name, ti.quantity, ti.price, ti.discount, " +
                    "(ti.quantity * ti.price - ti.discount) as total " +
                    "FROM transaction_items ti " +
                    "JOIN inventory i ON ti.item_id = i.id " +
                    "WHERE ti.transaction_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, transactionId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getString("item_name"),
                    rs.getInt("quantity"),
                    String.format("₱%.2f", rs.getDouble("price")),
                    String.format("₱%.2f", rs.getDouble("discount")),
                    String.format("₱%.2f", rs.getDouble("total"))
                };
                
                itemsTableModel.addRow(row);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading transaction items: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTotals(double total) {
        JPanel totalsPanel = (JPanel) getContentPane().getComponent(2);
        
        double subtotal = total / 1.1; // Remove 10% tax
        double tax = total - subtotal;
        
        // Calculate total discount
        double totalDiscount = 0;
        for (int i = 0; i < itemsTableModel.getRowCount(); i++) {
            String discountStr = ((String)itemsTableModel.getValueAt(i, 3))
                .replace("₱", "");
            totalDiscount += Double.parseDouble(discountStr);
        }
        
        // Update labels
        ((JLabel)totalsPanel.getComponent(1)).setText(String.format("₱%.2f", subtotal));
        ((JLabel)totalsPanel.getComponent(3)).setText(String.format("₱%.2f", tax));
        ((JLabel)totalsPanel.getComponent(5)).setText(String.format("₱%.2f", totalDiscount));
        ((JLabel)totalsPanel.getComponent(7)).setText(String.format("₱%.2f", total));
    }
} 