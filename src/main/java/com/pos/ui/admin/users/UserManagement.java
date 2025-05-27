package com.pos.ui.admin.users;

import com.pos.db.UserDAO;
import com.pos.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserManagement extends JPanel {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, resetPasswordButton;
    private List<User> users = new ArrayList<>();
    private final UserDAO userDAO;
    
    public UserManagement() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        userDAO = new UserDAO(); // Initialize DAO
        
        // Initialize table
        String[] columns = {"Username", "Full Name", "Role", "Active"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getTableHeader().setReorderingAllowed(false);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = new JButton("Add User");
        editButton = new JButton("Edit User");
        deleteButton = new JButton("Delete User");
        resetPasswordButton = new JButton("Reset Password");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(resetPasswordButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        addButton.addActionListener(e -> showAddUserDialog());
        editButton.addActionListener(e -> editUser());
        deleteButton.addActionListener(e -> deleteUser());
        resetPasswordButton.addActionListener(e -> resetPassword());
        
        // Load initial data
        loadUsers();
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        panel.add(titleLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JScrollPane createTablePanel() {
        // Create table model
        tableModel = new DefaultTableModel(
            new Object[][] {},
            new String[] {"ID", "Username", "Full Name", "Email", "Role", "Active"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) return Boolean.class;
                return String.class;
            }
        };
        
        // Create table
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setRowHeight(30);
        userTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        userTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        userTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Username
        userTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Full Name
        userTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Email
        userTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Role
        userTable.getColumnModel().getColumn(5).setPreferredWidth(60);  // Active
        
        return new JScrollPane(userTable);
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        addButton = new JButton("Add User");
        editButton = new JButton("Edit User");
        deleteButton = new JButton("Delete User");
        resetPasswordButton = new JButton("Reset Password");
        
        // Style buttons
        addButton.setBackground(new Color(76, 175, 80));
        addButton.setForeground(Color.WHITE);
        
        editButton.setBackground(new Color(33, 150, 243));
        editButton.setForeground(Color.WHITE);
        
        deleteButton.setBackground(new Color(244, 67, 54));
        deleteButton.setForeground(Color.WHITE);
        
        // Add action listeners
        addButton.addActionListener(e -> showAddUserDialog());
        editButton.addActionListener(e -> editUser());
        deleteButton.addActionListener(e -> deleteUser());
        resetPasswordButton.addActionListener(e -> resetPassword());
        
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(resetPasswordButton);
        
        return panel;
    }
    
    private void loadUsers() {
        try {
            users = userDAO.findAll();
        tableModel.setRowCount(0);
        
        for (User user : users) {
                tableModel.addRow(new Object[]{
                user.getUsername(),
                user.getFullName(),
                    user.getRole().getValue(),
                    user.isActive() ? "Yes" : "No"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading users: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddUserDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Add User", true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create form fields
        JTextField usernameField = new JTextField(20);
        JTextField fullNameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Cashier", "Admin"});
        JCheckBox activeCheckbox = new JCheckBox("Active", true);
        
        // Add form fields to panel
        addFormField(panel, "Username:", usernameField);
        addFormField(panel, "Full Name:", fullNameField);
        addFormField(panel, "Password:", passwordField);
        addFormField(panel, "Role:", roleCombo);
        panel.add(activeCheckbox);
        
        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String fullName = fullNameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String role = (String) roleCombo.getSelectedItem();
            boolean isActive = activeCheckbox.isSelected();
            
            // Validate input
            if (username.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please fill in all required fields",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                User newUser = new User();
                newUser.setId(UUID.randomUUID().toString());
                newUser.setUsername(username);
                newUser.setFullName(fullName);
                newUser.setRole(User.Role.fromString(role));
                newUser.setActive(isActive);
                
                userDAO.save(newUser);
                loadUsers(); // Refresh table
                dialog.dispose();
                
                JOptionPane.showMessageDialog(this,
                        "User created successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error creating user: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user to edit",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        User selectedUser = users.get(selectedRow);
        
        // Don't allow editing the default admin user
        if ("admin".equals(selectedUser.getUsername()) && selectedUser.getRole() == User.Role.ADMIN) {
            JOptionPane.showMessageDialog(this,
                    "Cannot edit the default Admin user",
                    "Edit Restricted",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Edit User", true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create form fields
        JTextField usernameField = new JTextField(selectedUser.getUsername(), 20);
        usernameField.setEnabled(false); // Username cannot be changed
        JTextField fullNameField = new JTextField(selectedUser.getFullName(), 20);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Cashier", "Admin"});
        roleCombo.setSelectedItem(selectedUser.getRole().getValue());
        JCheckBox activeCheckbox = new JCheckBox("Active", selectedUser.isActive());
        
        // Add form fields to panel
        addFormField(panel, "Username:", usernameField);
        addFormField(panel, "Full Name:", fullNameField);
        addFormField(panel, "Role:", roleCombo);
        panel.add(activeCheckbox);
        
        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            String fullName = fullNameField.getText().trim();
            String role = (String) roleCombo.getSelectedItem();
            boolean isActive = activeCheckbox.isSelected();
            
            // Validate input
            if (fullName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please fill in all required fields",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                selectedUser.setFullName(fullName);
                selectedUser.setRole(User.Role.fromString(role));
                selectedUser.setActive(isActive);
                
                userDAO.update(selectedUser);
                loadUsers(); // Refresh table
                dialog.dispose();
                
                JOptionPane.showMessageDialog(this,
                        "User updated successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error updating user: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
        }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user to delete",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        User selectedUser = users.get(selectedRow);
        
        // Don't allow deleting the default admin user
        if ("admin".equals(selectedUser.getUsername()) && selectedUser.getRole() == User.Role.ADMIN) {
            JOptionPane.showMessageDialog(this,
                    "Cannot delete the default Admin user",
                    "Delete Restricted",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this user?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                userDAO.delete(selectedUser.getId());
                loadUsers(); // Refresh table
                
                JOptionPane.showMessageDialog(this,
                        "User deleted successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting user: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void resetPassword() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user to reset password",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        User selectedUser = users.get(selectedRow);
        
        // Don't allow resetting password for the default admin user
        if ("admin".equals(selectedUser.getUsername()) && selectedUser.getRole() == User.Role.ADMIN) {
            JOptionPane.showMessageDialog(this,
                    "Cannot reset password for the default Admin user",
                    "Reset Password Restricted",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Reset Password", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create form fields
        JPasswordField newPasswordField = new JPasswordField(20);
        JPasswordField confirmPasswordField = new JPasswordField(20);
        
        // Add form fields to panel
        addFormField(panel, "New Password:", newPasswordField);
        addFormField(panel, "Confirm Password:", confirmPasswordField);
        
        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            // Validate input
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please fill in all required fields",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog,
                        "Passwords do not match",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                selectedUser.setPasswordHash(newPassword);
                userDAO.update(selectedUser);
                dialog.dispose();
                
                JOptionPane.showMessageDialog(this,
                        "Password reset successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error resetting password: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void addFormField(JPanel panel, String label, JComponent field) {
        JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel labelComponent = new JLabel(label);
        labelComponent.setPreferredSize(new java.awt.Dimension(100, 25));
        fieldPanel.add(labelComponent);
        fieldPanel.add(field);
        panel.add(fieldPanel);
    }
} 