package com.pos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.pos.db.DatabaseConnection;
import com.pos.model.ReceiptDesign;

public class ReceiptDesignDAO {
    private final DatabaseConnection dbConnection;
    
    public ReceiptDesignDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public ReceiptDesign getCurrentDesign() {
        String query = "SELECT * FROM receipt_design ORDER BY id DESC LIMIT 1";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return mapResultSetToReceiptDesign(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean saveDesign(ReceiptDesign design) {
        String query = "INSERT INTO receipt_design (header_text, footer_text, show_logo, logo_path, show_date_time, show_cashier_name, show_tax_details, font_family, font_size, show_border, location, contact_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, design.getHeaderText());
            stmt.setString(2, design.getFooterText());
            stmt.setBoolean(3, design.isShowLogo());
            stmt.setString(4, design.getLogoPath());
            stmt.setBoolean(5, design.isShowDateTime());
            stmt.setBoolean(6, design.isShowCashierName());
            stmt.setBoolean(7, design.isShowTaxDetails());
            stmt.setString(8, design.getFontFamily());
            stmt.setInt(9, design.getFontSize());
            stmt.setBoolean(10, design.isShowBorder());
            stmt.setString(11, design.getLocation());
            stmt.setString(12, design.getContactNumber());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                javax.swing.JOptionPane.showMessageDialog(null, "ReceiptDesignDAO SQL Error: " + e.getMessage(), "DB Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            });
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateDesign(ReceiptDesign design) {
        String query = "UPDATE receipt_design SET header_text = ?, footer_text = ?, show_logo = ?, logo_path = ?, show_date_time = ?, show_cashier_name = ?, show_tax_details = ?, font_family = ?, font_size = ?, show_border = ?, location = ?, contact_number = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, design.getHeaderText());
            stmt.setString(2, design.getFooterText());
            stmt.setBoolean(3, design.isShowLogo());
            stmt.setString(4, design.getLogoPath());
            stmt.setBoolean(5, design.isShowDateTime());
            stmt.setBoolean(6, design.isShowCashierName());
            stmt.setBoolean(7, design.isShowTaxDetails());
            stmt.setString(8, design.getFontFamily());
            stmt.setInt(9, design.getFontSize());
            stmt.setBoolean(10, design.isShowBorder());
            stmt.setString(11, design.getLocation());
            stmt.setString(12, design.getContactNumber());
            stmt.setInt(13, design.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                javax.swing.JOptionPane.showMessageDialog(null, "ReceiptDesignDAO SQL Error: " + e.getMessage(), "DB Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            });
            e.printStackTrace();
            return false;
        }
    }
    
    private ReceiptDesign mapResultSetToReceiptDesign(ResultSet rs) throws SQLException {
        ReceiptDesign design = new ReceiptDesign();
        design.setId(rs.getInt("id"));
        design.setHeaderText(rs.getString("header_text"));
        design.setFooterText(rs.getString("footer_text"));
        design.setShowLogo(rs.getBoolean("show_logo"));
        design.setLogoPath(rs.getString("logo_path"));
        design.setShowDateTime(rs.getBoolean("show_date_time"));
        design.setShowCashierName(rs.getBoolean("show_cashier_name"));
        design.setShowTaxDetails(rs.getBoolean("show_tax_details"));
        design.setFontFamily(rs.getString("font_family"));
        design.setFontSize(rs.getInt("font_size"));
        design.setShowBorder(rs.getBoolean("show_border"));
        design.setLocation(rs.getString("location"));
        design.setContactNumber(rs.getString("contact_number"));
        return design;
    }
} 