package com.pos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.pos.db.DatabaseConnection;
import com.pos.model.PrinterConfig;

public class PrinterConfigDAO {
    private final DatabaseConnection dbConnection;
    
    public PrinterConfigDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public PrinterConfig getCurrentConfig() {
        String query = "SELECT * FROM printer_config ORDER BY id DESC LIMIT 1";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return mapResultSetToPrinterConfig(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean saveConfig(PrinterConfig config) {
        String query = "INSERT INTO printer_config (printer_name, paper_size, auto_print, copies, paper_type) " +
                      "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, config.getPrinterName());
            stmt.setString(2, config.getPaperSize());
            stmt.setBoolean(3, config.isAutoPrint());
            stmt.setInt(4, config.getCopies());
            stmt.setString(5, config.getPaperType());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                javax.swing.JOptionPane.showMessageDialog(null, "PrinterConfigDAO SQL Error: " + e.getMessage(), "DB Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            });
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateConfig(PrinterConfig config) {
        String query = "UPDATE printer_config SET printer_name = ?, paper_size = ?, auto_print = ?, " +
                      "copies = ?, paper_type = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, config.getPrinterName());
            stmt.setString(2, config.getPaperSize());
            stmt.setBoolean(3, config.isAutoPrint());
            stmt.setInt(4, config.getCopies());
            stmt.setString(5, config.getPaperType());
            stmt.setInt(6, config.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                javax.swing.JOptionPane.showMessageDialog(null, "PrinterConfigDAO SQL Error: " + e.getMessage(), "DB Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            });
            e.printStackTrace();
            return false;
        }
    }
    
    private PrinterConfig mapResultSetToPrinterConfig(ResultSet rs) throws SQLException {
        PrinterConfig config = new PrinterConfig();
        config.setId(rs.getInt("id"));
        config.setPrinterName(rs.getString("printer_name"));
        config.setPaperSize(rs.getString("paper_size"));
        config.setAutoPrint(rs.getBoolean("auto_print"));
        config.setCopies(rs.getInt("copies"));
        config.setPaperType(rs.getString("paper_type"));
        return config;
    }
} 