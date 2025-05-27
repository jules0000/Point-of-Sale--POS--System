package com.pos.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class SplashScreen extends JWindow {
    private static final long serialVersionUID = 1L;
    private JProgressBar progressBar;
    private Timer timer;
    private int progress = 0;

    public SplashScreen() {
        // Create the splash screen window
        setSize(400, 300);
        setLocationRelativeTo(null);

// Custom panel with dark blue to silver gradient
JPanel mainPanel = new JPanel(new BorderLayout()) {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();

        // Gradient colors: Dark navy blue to silver-gray
        Color color1 = new Color(10, 25, 49);   // Dark navy blue
        Color color2 = new Color(192, 192, 192); // Silver

        GradientPaint gp = new GradientPaint(0, 0, color1, 0, height, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, width, height);
    }
};
mainPanel.setOpaque(false);
mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

// Add title label
JLabel titleLabel = new JLabel("QuickVend POS", JLabel.CENTER);
titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
titleLabel.setForeground(Color.WHITE);
mainPanel.add(titleLabel, BorderLayout.CENTER);

// Add subtitle label
JLabel subtitleLabel = new JLabel("Loading...", JLabel.CENTER);
subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
subtitleLabel.setForeground(Color.WHITE);
mainPanel.add(subtitleLabel, BorderLayout.SOUTH);

// Add progress bar
progressBar = new JProgressBar(0, 100);
progressBar.setStringPainted(true);
progressBar.setForeground(new Color(40, 167, 69)); // Success green
progressBar.setBackground(Color.WHITE);
mainPanel.add(progressBar, BorderLayout.SOUTH);

        // Add the main panel to the window
        add(mainPanel);

        // Create and start the timer for progress bar
        timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progress += 2;
                progressBar.setValue(progress);
                
                if (progress >= 100) {
                    timer.stop();
                    dispose();
                    // Launch the login screen
                    SwingUtilities.invokeLater(() -> {
                        try {
                            Class<?> loginScreenClass = Class.forName("com.pos.ui.auth.LoginScreen");
                            JFrame loginScreen = (JFrame) loginScreenClass.getDeclaredConstructor().newInstance();
                            loginScreen.setVisible(true);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null,
                                "Error launching login screen: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                            System.exit(1);
                        }
                    });
                }
            }
        });
        timer.start();
    }
} 