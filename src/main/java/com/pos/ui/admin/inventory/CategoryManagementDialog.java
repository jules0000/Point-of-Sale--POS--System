package com.pos.ui.admin.inventory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.pos.db.DatabaseConnection;

public class CategoryManagementDialog extends JDialog {
    private DefaultListModel<String> categoryListModel;
    private JList<String> categoryList;
    private JTextField newCategoryField;
    private JTextField newDescriptionField;
    private JButton addButton, deleteButton;
    private java.util.List<String> categoryIds = new java.util.ArrayList<>();

    public CategoryManagementDialog(Window owner) {
        super(owner, "Manage Categories", ModalityType.APPLICATION_MODAL);
        setSize(400, 400);
        setLocationRelativeTo(owner);
        setResizable(false);

        categoryListModel = new DefaultListModel<>();
        categoryList = new JList<>(categoryListModel);
        JScrollPane scrollPane = new JScrollPane(categoryList);

        loadCategories();

        newCategoryField = new JTextField();
        newDescriptionField = new JTextField();
        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");

        addButton.addActionListener(e -> addCategory());
        deleteButton.addActionListener(e -> deleteCategory());

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(newCategoryField);
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(newDescriptionField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        setLayout(new BorderLayout(10, 10));
        add(new JLabel("Categories (Name - Description):"), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.EAST);
    }

    private void loadCategories() {
        categoryListModel.clear();
        categoryIds.clear();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name, description FROM categories ORDER BY name ASC")) {
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String desc = rs.getString("description");
                categoryIds.add(id);
                categoryListModel.addElement(name + " - " + (desc == null ? "" : desc));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generateNewCategoryId() {
        // Find max numeric part and increment
        int max = 0;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM categories")) {
            while (rs.next()) {
                String id = rs.getString("id");
                if (id != null && id.matches("C\\d+")) {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > max) max = num;
                }
            }
        } catch (SQLException e) {}
        return String.format("C%03d", max + 1);
    }

    private void addCategory() {
        String name = newCategoryField.getText().trim();
        String desc = newDescriptionField.getText().trim();
        if (name.isEmpty()) return;
        String newId = generateNewCategoryId();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO categories (id, name, description, created_at, updated_at) VALUES (?, ?, ?, NOW(), NOW())")) {
            stmt.setString(1, newId);
            stmt.setString(2, name);
            stmt.setString(3, desc);
            stmt.executeUpdate();
            loadCategories();
            newCategoryField.setText("");
            newDescriptionField.setText("");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding category: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCategory() {
        int idx = categoryList.getSelectedIndex();
        if (idx == -1) return;
        String id = categoryIds.get(idx);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected category?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM categories WHERE id = ?")) {
            stmt.setString(1, id);
            stmt.executeUpdate();
            loadCategories();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting category: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM categories ORDER BY name ASC")) {
            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            // ignore for static use
        }
        return categories;
    }
} 