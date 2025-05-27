package com.pos.ui.admin.users;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.pos.db.UserDAO;
import com.pos.model.User;

public class UserDialog extends JDialog {
    private JTextField usernameField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JComboBox<User.Role> roleComboBox;
    private JCheckBox activeCheckbox;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    
    private User user;
    private boolean userSaved = false;
    private UserDAO userDAO;
    
    public UserDialog(Window owner, User user) {
        super(owner, user == null ? "Add New User" : "Edit User", ModalityType.APPLICATION_MODAL);
        this.user = user;
        this.userDAO = new UserDAO();
        
        setSize(500, 650);
        setLocationRelativeTo(owner);
        setResizable(true);
        setMinimumSize(new Dimension(450, 550));
        
        // Main container with BorderLayout
        JPanel mainContainer = new JPanel(new BorderLayout(10, 10));
        mainContainer.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        
        // Create fields panel
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 15));
        fieldsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Username
        fieldsPanel.add(new JLabel("Username:"));
        usernameField = new JTextField(20);
        fieldsPanel.add(usernameField);
        
        // Full Name
        fieldsPanel.add(new JLabel("Full Name:"));
        fullNameField = new JTextField(20);
        fieldsPanel.add(fullNameField);
        
        // Email
        fieldsPanel.add(new JLabel("Email:"));
        emailField = new JTextField(20);
        fieldsPanel.add(emailField);
        
        // Role
        fieldsPanel.add(new JLabel("Role:"));
        roleComboBox = new JComboBox<>(User.Role.values());
        fieldsPanel.add(roleComboBox);
        
        // Active
        fieldsPanel.add(new JLabel("Active:"));
        activeCheckbox = new JCheckBox();
        activeCheckbox.setSelected(true);
        fieldsPanel.add(activeCheckbox);
        
        // Password
        fieldsPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(20);
        fieldsPanel.add(passwordField);
        
        // Confirm Password
        fieldsPanel.add(new JLabel("Confirm Password:"));
        confirmPasswordField = new JPasswordField(20);
        fieldsPanel.add(confirmPasswordField);
        
        formPanel.add(fieldsPanel);
        formPanel.add(Box.createVerticalGlue());
        
        // Scroll pane for form
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        
        // Buttons panel - keep outside scroll pane
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save");
        
        // Style buttons
        saveButton.setBackground(new Color(76, 175, 80));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        
        cancelButton.setFocusPainted(false);
        
        // Add button actions
        cancelButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> {
            if (validateForm()) {
                saveUser();
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        // Add components to main container
        mainContainer.add(scrollPane, BorderLayout.CENTER);
        mainContainer.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainContainer);
        
        // Initialize form with user data if editing
        if (user != null) {
            populateForm();
        }
    }
    
    private void populateForm() {
        usernameField.setText(user.getUsername());
        fullNameField.setText(user.getFullName());
        emailField.setText(user.getEmail());
        roleComboBox.setSelectedItem(user.getRole());
        activeCheckbox.setSelected(user.isActive());
        
        // Disable username field if editing
        usernameField.setEnabled(false);
        
        // Hide password fields when editing
        passwordField.setEnabled(false);
        confirmPasswordField.setEnabled(false);
    }
    
    private boolean validateForm() {
        // Validate username
        if (usernameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            usernameField.requestFocus();
            return false;
        }
        
        // Validate full name
        if (fullNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Full name is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            fullNameField.requestFocus();
            return false;
        }
        
        // Validate email
        if (emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            emailField.requestFocus();
            return false;
        }
        
        // Validate password for new users
        if (user == null) {
            char[] password = passwordField.getPassword();
            char[] confirm = confirmPasswordField.getPassword();
            
            if (password.length == 0) {
                JOptionPane.showMessageDialog(this, "Password is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
                passwordField.requestFocus();
                return false;
            }
            
            if (!new String(password).equals(new String(confirm))) {
                JOptionPane.showMessageDialog(this, "Passwords do not match", "Validation Error", JOptionPane.ERROR_MESSAGE);
                confirmPasswordField.requestFocus();
                return false;
            }
        }
        
        return true;
    }
    
    private void saveUser() {
        try {
            if (user == null) {
                // Create new user
                user = new User();
                user.setUsername(usernameField.getText().trim());
                user.setFullName(fullNameField.getText().trim());
                user.setEmail(emailField.getText().trim());
                user.setRole(User.Role.fromString(roleComboBox.getSelectedItem().toString()));
                user.setActive(activeCheckbox.isSelected());
                
                if (userDAO.createUser(user, new String(passwordField.getPassword()))) {
                    JOptionPane.showMessageDialog(this, "User created successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    userSaved = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create user", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Update existing user
                user.setFullName(fullNameField.getText().trim());
                user.setEmail(emailField.getText().trim());
                user.setRole(User.Role.fromString(roleComboBox.getSelectedItem().toString()));
                user.setActive(activeCheckbox.isSelected());
                
                if (userDAO.updateUser(user)) {
                    JOptionPane.showMessageDialog(this, "User updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    userSaved = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update user", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isUserSaved() {
        return userSaved;
    }
} 