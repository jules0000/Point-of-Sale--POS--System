package com.pos.db;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pos.model.User;
import com.pos.util.PasswordUtils;
import javax.swing.JOptionPane;

/**
 * Data Access Object for User entities
 */
public class UserDAO implements BaseDAO<User> {
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    private final DatabaseConnection dbConnection;
    
    public UserDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Validate user credentials for login
     * 
     * @param username Username
     * @param password Plain text password
     * @return true if valid, false otherwise
     */
    public boolean authenticateUser(String username, String password) {
        try {
            Optional<User> userOpt = getUserByUsername(username);
            
            if (!userOpt.isPresent()) {
                LOGGER.info("User not found: " + username);
                return false;
            }
            
            User user = userOpt.get();
            if (!user.isActive()) {
                LOGGER.info("User is not active: " + username);
                return false;
            }
            
            LOGGER.info("Attempting to verify password for user: " + username);
            LOGGER.info("Stored hash: " + user.getPasswordHash());
            
            boolean verified = PasswordUtils.verifyPassword(password, user.getPasswordHash());
            LOGGER.info("Password verification result: " + verified);
            
            if (verified) {
                updateLastLogin(user.getId());
            }
            
            return verified;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during authentication: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Get all users
     * 
     * @return List of all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY username";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting users: " + e.getMessage(), e);
        }
        
        return users;
    }
    
    /**
     * Get user by ID
     * 
     * @param id User ID
     * @return User object if found, null otherwise
     */
    public User getUserById(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting user by ID: " + e.getMessage(), e);
        }
        
        return null;
    }
    
    /**
     * Get user by username
     * 
     * @param username Username
     * @return Optional containing the user if found
     */
    public Optional<User> getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting user by username: " + e.getMessage(), e);
        }
        
        return Optional.empty();
    }
    
    /**
     * Insert a new user
     * 
     * @param user User to insert
     * @param password Plaintext password to hash
     * @return true if successful, false otherwise
     */
    public boolean createUser(User user, String password) {
        String sql = "INSERT INTO users (id, username, password_hash, full_name, email, role, active) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String id = UUID.randomUUID().toString();
            user.setId(id);
            stmt.setString(1, id);
            stmt.setString(2, user.getUsername());
            stmt.setString(3, PasswordUtils.hashPassword(password));
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getRole().getValue());
            stmt.setBoolean(7, user.isActive());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry
                JOptionPane.showMessageDialog(null,
                    "Username already exists. Please choose a different username.",
                    "Duplicate Username",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                LOGGER.log(Level.SEVERE, "Error creating user: " + e.getMessage(), e);
                LOGGER.severe("SQLState: " + e.getSQLState() + ", ErrorCode: " + e.getErrorCode());
                LOGGER.severe("Attempted to insert: username=" + user.getUsername() + ", role=" + user.getRole().getValue() + ", email=" + user.getEmail());
            }
            return false;
        }
    }
    
    /**
     * Update an existing user
     * 
     * @param user User to update
     * @return true if successful, false otherwise
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, full_name = ?, email = ?, role = ?, active = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getFullName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole().name());
            stmt.setBoolean(5, user.isActive());
            stmt.setString(6, user.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating user", e);
            return false;
        }
    }
    
    /**
     * Update a user's password
     * 
     * @param userId User ID
     * @param newPassword New plaintext password to hash
     * @return true if successful, false otherwise
     */
    public boolean updateUserPassword(String userId, String newPassword) {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, PasswordUtils.hashPassword(newPassword));
            stmt.setString(2, userId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating user password", e);
            return false;
        }
    }
    
    /**
     * Delete a user
     * 
     * @param userId User ID
     * @return true if successful, false otherwise
     */
    public boolean deleteUser(String userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting user", e);
            return false;
        }
    }
    
    /**
     * Reset admin password to default
     */
    public boolean resetAdminPassword() {
        String sql = "UPDATE users SET password_hash = ? WHERE username = 'admin'";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Generate new hash for password 'admin'
            String newHash = PasswordUtils.hashPassword("admin");
            stmt.setString(1, newHash);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error resetting admin password: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public User save(User user) throws SQLException {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID().toString());
        }
        
        String sql = "INSERT INTO users (id, username, password_hash, full_name, email, role, active) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getId());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getRole().getValue());
            stmt.setBoolean(7, user.isActive());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            
            return user;
        }
    }

    @Override
    public Optional<User> findById(String id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    @Override
    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, password_hash = ?, full_name = ?, " +
                    "email = ?, role = ?, active = ?, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getRole().name());
            stmt.setBoolean(6, user.isActive());
            stmt.setString(7, user.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }
        }
    }

    @Override
    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteAll() throws SQLException {
        String sql = "DELETE FROM users";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(sql);
        }
    }

    /**
     * Update user's last login time
     * 
     * @param userId User ID
     * @return true if successful, false otherwise
     */
    private boolean updateLastLogin(String userId) {
        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating last login: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Map a ResultSet row to a User object
     * 
     * @param rs ResultSet
     * @return User object
     * @throws SQLException if a database access error occurs
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getString("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setRole(User.Role.fromString(rs.getString("role")));
        user.setActive(rs.getBoolean("active"));
        
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin.toLocalDateTime());
        }
        
        return user;
    }
} 