package com.pos.ui.auth;

import com.pos.db.UserDAO;
import com.pos.model.User;
import com.pos.ui.admin.AdminDashboard;
import com.pos.ui.cashier.CashierDashboard;
import com.pos.util.PasswordUtils;
import com.pos.util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginScreen extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private static final Logger LOGGER = Logger.getLogger(LoginScreen.class.getName());
    
    public LoginScreen() {
        setTitle("POS System Login");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main container 
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);
        
        // North panel with logo and title
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Try to load the logo image
        JLabel logoLabel = new JLabel();
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/pos-logo.png"));
            if (icon.getIconWidth() > 0) {
                // Resize if needed
                Image img = icon.getImage();
                Image resizedImg = img.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(resizedImg));
            } else {
                // Fallback text
                logoLabel.setText("QUICKVEND");
                logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
                logoLabel.setForeground(new Color(33, 150, 243));
            }
        } catch (Exception e) {
            System.err.println("Error loading logo: " + e.getMessage());
            // Fallback text
            logoLabel.setText("QUICKVEND");
            logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
            logoLabel.setForeground(new Color(33, 150, 243));
        }
        
        JLabel titleLabel = new JLabel("Login to QuickVend POS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        northPanel.add(logoLabel);
        northPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        northPanel.add(titleLabel);
        
        // Center panel with login form
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        
        // Username field
        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel usernameLabel = new JLabel("Username");
        usernameField = new JTextField(20);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);
        
        // Password field
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel passwordLabel = new JLabel("Password");
        passwordField = new JPasswordField(20);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);
        
        // Login button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(120, 30));
        loginButton.setBackground(new Color(33, 150, 243));
        loginButton.setForeground(Color.WHITE);
        
        buttonPanel.add(loginButton);
        
        // Add all panels to the center panel
        centerPanel.add(usernamePanel);
        centerPanel.add(passwordPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(buttonPanel);
        
        // Add login button action
        loginButton.addActionListener(e -> handleLogin());
        
        // Add enter key listener to password field
        passwordField.addActionListener(e -> handleLogin());
        
        // Footer
        JLabel versionLabel = new JLabel("POS System v1.0", JLabel.CENTER);
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        versionLabel.setForeground(Color.GRAY);
        
        // Add everything to the content pane
        contentPane.add(northPanel, BorderLayout.NORTH);
        contentPane.add(centerPanel, BorderLayout.CENTER);
        contentPane.add(versionLabel, BorderLayout.SOUTH);
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        LOGGER.info("Login attempt: username=" + username + ", password=" + password);

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            Optional<User> userOpt = userDAO.getUserByUsername(username);
            
            if (!userOpt.isPresent()) {
                LOGGER.info("User not found for username: " + username);
                showError("Invalid username or password");
                return;
            }
            
            User user = userOpt.get();
            LOGGER.info("User found: " + user.toString());
            LOGGER.info("User role: " + user.getRole());
            LOGGER.info("User active: " + user.isActive());
            
            if (!user.isActive()) {
                LOGGER.info("User is not active: " + username);
                showError("This account has been deactivated");
                return;
            }

            String hash = user.getPasswordHash();
            boolean verified = PasswordUtils.verifyPassword(password, hash);
            LOGGER.info("Password hash from DB: " + hash);
            LOGGER.info("Password verification result: " + verified);

            if (!verified) {
                showError("Invalid username or password");
                return;
            }

            // Login successful
            LOGGER.info("Login successful for user: " + username);
            LOGGER.info("Creating session...");
            SessionManager.getInstance().createSession(user, "127.0.0.1");
            LOGGER.info("Opening dashboard...");
            openDashboard(user);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during login: " + e.getMessage(), e);
            showError("An error occurred during login");
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(LoginScreen.this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
    
    private void openDashboard(User user) {
        try {
            LOGGER.info("Opening dashboard for user: " + user.getUsername());
            LOGGER.info("User role: " + user.getRole());
            
            if (user.getRole() == User.Role.ADMIN) {
                LOGGER.info("Opening admin dashboard...");
                AdminDashboard adminDashboard = new AdminDashboard(user.getUsername());
                adminDashboard.setVisible(true);
            } else if (user.getRole() == User.Role.CASHIER) {
                LOGGER.info("Opening cashier dashboard...");
                CashierDashboard cashierDashboard = new CashierDashboard(user.getUsername());
                cashierDashboard.setVisible(true);
            } else {
                LOGGER.warning("Unknown user role: " + user.getRole());
                showError("Invalid user role");
                return;
            }
            
            LOGGER.info("Closing login screen...");
            LoginScreen.this.dispose();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error opening dashboard: " + e.getMessage(), e);
            showError("Error opening dashboard: " + e.getMessage());
        }
    }
} 