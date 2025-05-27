-- Drop and recreate database
DROP DATABASE IF EXISTS pos_db;
CREATE DATABASE pos_db;
USE pos_db;

-- Users table
CREATE TABLE IF NOT EXISTS `users` (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('Admin', 'Cashier') NOT NULL,
    email VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Inventory table
CREATE TABLE IF NOT EXISTS `inventory` (
    id VARCHAR(36) PRIMARY KEY,
    item_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT,
    barcode VARCHAR(50) UNIQUE,
    cost_price DECIMAL(10,2) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    reorder_level INT DEFAULT 10,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert default admin user (password: admin)
INSERT INTO users (id, username, password_hash, full_name, role, email, active)
VALUES ('1', 'admin', '$2a$10$aAnNmAzditmUCjEMLqSB2u5OMJTzYWrIqopq/T5DzkG55PMy2wTBi', 'System Administrator', 'Admin', 'admin@pos.com', true);

-- Insert default cashier user (password: cashier)
INSERT INTO users (id, username, password_hash, full_name, role, email, active)
VALUES ('2', 'cashier', '$2a$10$HUqmTZbJcdwPkqJTsUFSoe5JJM5tMgN/SYZakzD9GQYQdUVb8oz0O', 'Default Cashier', 'Cashier', 'cashier@pos.com', true);

-- Insert sample inventory items
INSERT INTO `inventory` (id, item_name, category, description, barcode, cost_price, price, quantity, reorder_level) VALUES
('I001', 'Bread', 'Food', 'Fresh bread', '1234567890', 1.50, 2.50, 100, 20),
('I002', 'Milk', 'Food', 'Fresh milk', '1234567891', 2.00, 3.00, 80, 15),
('I003', 'Coffee', 'Beverages', 'Ground coffee', '1234567892', 4.00, 5.75, 50, 10),
('I004', 'Soap', 'Household', 'Bath soap', '1234567893', 1.50, 2.25, 60, 10),
('I005', 'Paper Towels', 'Household', 'Kitchen paper towels', '1234567894', 3.00, 4.75, 40, 10),
('I006', 'Juice', 'Beverages', 'Orange juice', '1234567895', 2.50, 3.50, 30, 10),
('I007', 'Eggs', 'Food', 'Dozen eggs', '1234567896', 3.00, 4.25, 50, 10),
('I008', 'Water', 'Beverages', 'Bottled water', '1234567897', 0.50, 1.00, 200, 20); 