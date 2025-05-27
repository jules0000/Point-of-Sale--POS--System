package com.pos.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.pos.model.Product;

public class ProductDAO implements BaseDAO<Product> {
    private static final Logger LOGGER = Logger.getLogger(ProductDAO.class.getName());
    private final DatabaseConnection dbConnection;

    public ProductDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public Product save(Product product) throws SQLException {
        String sql = "INSERT INTO inventory (id, item_name, description, barcode, category, cost_price, price, quantity, reorder_level, is_active, image_path, image) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, product.getId());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getDescription());
            stmt.setString(4, product.getBarcode());
            stmt.setString(5, product.getCategory());
            stmt.setBigDecimal(6, product.getCostPrice());
            stmt.setBigDecimal(7, product.getSellingPrice());
            stmt.setInt(8, product.getCurrentStock());
            stmt.setInt(9, product.getLowStockThreshold());
            stmt.setBoolean(10, product.isActive());
            stmt.setString(11, product.getImagePath());
            stmt.setBytes(12, product.getImage());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating product failed, no rows affected.");
            }
        }
        return product;
    }

    @Override
    public Optional<Product> findById(String id) throws SQLException {
        String sql = "SELECT * FROM inventory WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProduct(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Product> findAll() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM inventory ORDER BY item_name";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        }
        return products;
    }

    public List<Product> getAllProducts() throws SQLException {
        return findAll();
    }

    @Override
    public void update(Product product) throws SQLException {
        String sql = "UPDATE inventory SET item_name = ?, description = ?, barcode = ?, category = ?, " +
                    "cost_price = ?, price = ?, quantity = ?, reorder_level = ?, is_active = ?, image_path = ?, image = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setString(3, product.getBarcode());
            stmt.setString(4, product.getCategory());
            stmt.setBigDecimal(5, product.getCostPrice());
            stmt.setBigDecimal(6, product.getSellingPrice());
            stmt.setInt(7, product.getCurrentStock());
            stmt.setInt(8, product.getLowStockThreshold());
            stmt.setBoolean(9, product.isActive());
            stmt.setString(10, product.getImagePath());
            stmt.setBytes(11, product.getImage());
            stmt.setString(12, product.getId());
            
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM inventory WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteAll() throws SQLException {
        String sql = "DELETE FROM inventory";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(sql);
        }
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getString("id"));
        product.setName(rs.getString("item_name"));
        product.setDescription(rs.getString("description"));
        product.setBarcode(rs.getString("barcode"));
        product.setCategory(rs.getString("category"));
        product.setCostPrice(rs.getBigDecimal("cost_price"));
        product.setSellingPrice(rs.getBigDecimal("price"));
        product.setCurrentStock(rs.getInt("quantity"));
        product.setLowStockThreshold(rs.getInt("reorder_level"));
        product.setActive(rs.getBoolean("is_active"));
        product.setImagePath(rs.getString("image_path"));
        product.setImage(rs.getBytes("image"));
        return product;
    }
} 