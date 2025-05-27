package com.pos.db;

import com.pos.util.ConfigLoader;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database connection manager using HikariCP connection pooling.
 */
public class DatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static DatabaseConnection instance;
    private HikariDataSource dataSource;
    private boolean initialized = false;

    private DatabaseConnection() {
        initializeDataSource();
    }

    private void initializeDataSource() {
        try {
            ConfigLoader config = ConfigLoader.getInstance();
            
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(config.getProperty("db.url"));
            hikariConfig.setUsername(config.getProperty("db.username"));
            hikariConfig.setPassword(config.getProperty("db.password"));
            hikariConfig.setDriverClassName(config.getProperty("db.driver"));
            
            // Connection pool settings
            hikariConfig.setMinimumIdle(2);
            hikariConfig.setMaximumPoolSize(10);
            hikariConfig.setIdleTimeout(300000);
            hikariConfig.setMaxLifetime(1800000);
            hikariConfig.setConnectionTimeout(30000);
            hikariConfig.setAutoCommit(true);
            
            // Connection test query
            hikariConfig.setConnectionTestQuery("SELECT 1");
            
            // Performance settings
            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
            
            // Initialize connection pool
            dataSource = new HikariDataSource(hikariConfig);
            initialized = true;
            LOGGER.info("Database connection pool initialized successfully");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database connection pool: " + e.getMessage(), e);
            initialized = false;
            throw new RuntimeException("Failed to initialize database connection pool", e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Gets a connection from the connection pool.
     * @return A database connection
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        if (!initialized || dataSource == null || dataSource.isClosed()) {
            initializeDataSource();
            if (!initialized) {
                throw new SQLException("Database connection pool is not initialized");
            }
        }
        
        try {
            Connection connection = dataSource.getConnection();
            if (connection == null || !connection.isValid(2)) {
                throw new SQLException("Invalid database connection obtained from pool");
            }
            LOGGER.fine("Database connection obtained from pool");
            return connection;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to obtain database connection: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Closes the connection pool and releases all resources.
     */
    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            initialized = false;
            LOGGER.info("Database connection pool closed");
        }
    }

    /**
     * Tests the database connection.
     * @return true if the connection is valid, false otherwise
     */
    public boolean testConnection() {
        if (!initialized) {
            return false;
        }
        
        try (Connection conn = getConnection()) {
            return conn != null && conn.isValid(5);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection test failed: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Gets the current status of the connection pool.
     * @return A string containing pool statistics
     */
    public String getPoolStats() {
        if (!initialized || dataSource == null || dataSource.isClosed()) {
            return "Connection pool is not initialized";
        }
        
        return String.format(
            "Pool Stats - Active: %d, Idle: %d, Total: %d, Waiting: %d",
            dataSource.getHikariPoolMXBean().getActiveConnections(),
            dataSource.getHikariPoolMXBean().getIdleConnections(),
            dataSource.getHikariPoolMXBean().getTotalConnections(),
            dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection()
        );
    }
} 