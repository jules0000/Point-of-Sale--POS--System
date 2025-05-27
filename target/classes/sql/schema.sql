-- Create database if not exists
CREATE DATABASE IF NOT EXISTS pos_db;
USE pos_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role ENUM('ADMIN', 'CASHIER') NOT NULL,
    active BOOLEAN DEFAULT true,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Inventory table
CREATE TABLE IF NOT EXISTS inventory (
    id VARCHAR(36) PRIMARY KEY,
    barcode VARCHAR(50) UNIQUE,
    item_name VARCHAR(100) NOT NULL,
    description TEXT,
    category VARCHAR(50),
    price DECIMAL(10,2) NOT NULL,
    cost_price DECIMAL(10,2),
    quantity INT NOT NULL DEFAULT 0,
    reorder_level INT DEFAULT 10,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Customers table
CREATE TABLE IF NOT EXISTS customers (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_number VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    points DECIMAL(10,2) DEFAULT 0.00,
    total_spent DECIMAL(10,2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
    id VARCHAR(36) PRIMARY KEY,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cashier_id VARCHAR(36) NOT NULL,
    customer_id VARCHAR(36),
    total_amount DECIMAL(10,2) NOT NULL,
    points_earned DECIMAL(10,2) DEFAULT 0.00,
    points_redeemed DECIMAL(10,2) DEFAULT 0.00,
    payment_method VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'Completed',
    notes TEXT,
    FOREIGN KEY (cashier_id) REFERENCES users(id),
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- Transaction items table
CREATE TABLE IF NOT EXISTS transaction_items (
    id VARCHAR(36) PRIMARY KEY,
    transaction_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    FOREIGN KEY (transaction_id) REFERENCES transactions(id),
    FOREIGN KEY (product_id) REFERENCES inventory(id)
);

-- Returns table
CREATE TABLE IF NOT EXISTS returns (
    id VARCHAR(36) PRIMARY KEY,
    transaction_id VARCHAR(36) NOT NULL,
    return_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cashier_id VARCHAR(36) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    reason TEXT,
    status VARCHAR(20) DEFAULT 'Completed',
    FOREIGN KEY (transaction_id) REFERENCES transactions(id),
    FOREIGN KEY (cashier_id) REFERENCES users(id)
);

-- Return items table
CREATE TABLE IF NOT EXISTS return_items (
    id VARCHAR(36) PRIMARY KEY,
    return_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (return_id) REFERENCES returns(id),
    FOREIGN KEY (product_id) REFERENCES inventory(id)
);

-- Audit log table
CREATE TABLE IF NOT EXISTS audit_log (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    action_details TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- MEMBERSHIP/LOYALTY TABLE
CREATE TABLE IF NOT EXISTS `members` (
    id VARCHAR(36) PRIMARY KEY,
    member_code VARCHAR(20) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    contact_no VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    points DECIMAL(10,2) DEFAULT 0.00,
    total_spent DECIMAL(10,2) DEFAULT 0.00,
    membership_level ENUM('Regular', 'Silver', 'Gold', 'Platinum') DEFAULT 'Regular',
    join_date DATE NOT NULL,
    valid_until DATE NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert default admin user (password: admin)
INSERT INTO users (id, username, password_hash, full_name, role, email, active) 
VALUES (
    UUID(), 
    'admin',
    'admin',
    'System Administrator',
    'ADMIN',
    'admin@pos.com',
    true
) ON DUPLICATE KEY UPDATE username = username;

-- Insert default cashier user (password: cashier)
INSERT INTO users (id, username, password_hash, full_name, role, email, active) 
VALUES (
    UUID(), 
    'cashier',
    'cashier',
    'Default Cashier',
    'CASHIER',
    'cashier@pos.com',
    true
) ON DUPLICATE KEY UPDATE username = username; 