-- POS System Full Schema
-- Drop and recreate database
DROP DATABASE IF EXISTS pos_db;
CREATE DATABASE pos_db;
USE pos_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role ENUM('Admin', 'Cashier') NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Members/Loyalty table
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
    image_path VARCHAR(255) DEFAULT NULL,
    image LONGBLOB,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Categories table
CREATE TABLE IF NOT EXISTS `categories` (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Transactions table
CREATE TABLE IF NOT EXISTS `transactions` (
    id VARCHAR(36) PRIMARY KEY,
    transaction_no VARCHAR(20) UNIQUE NOT NULL,
    cashier_id VARCHAR(36) NOT NULL,
    member_id VARCHAR(36),
    subtotal DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    tax_amount DECIMAL(10,2) DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL,
    points_earned DECIMAL(10,2) DEFAULT 0.00,
    points_redeemed DECIMAL(10,2) DEFAULT 0.00,
    payment_method ENUM('Cash', 'Card', 'GCash', 'Maya') NOT NULL,
    amount_tendered DECIMAL(10,2) NOT NULL,
    change_amount DECIMAL(10,2) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('Completed', 'Voided', 'Refunded') DEFAULT 'Completed',
    void_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cashier_id) REFERENCES users(id),
    FOREIGN KEY (member_id) REFERENCES members(id)
);

-- Transaction items table
CREATE TABLE IF NOT EXISTS `transaction_items` (
    id VARCHAR(36) PRIMARY KEY,
    transaction_id VARCHAR(36) NOT NULL,
    item_id VARCHAR(36) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transaction_id) REFERENCES transactions(id),
    FOREIGN KEY (item_id) REFERENCES inventory(id)
);

-- Returns table
CREATE TABLE IF NOT EXISTS `returns` (
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
CREATE TABLE IF NOT EXISTS `return_items` (
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
CREATE TABLE IF NOT EXISTS `audit_log` (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    action_details TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Settings table
CREATE TABLE IF NOT EXISTS `settings` (
    id VARCHAR(36) PRIMARY KEY,
    setting_key VARCHAR(50) UNIQUE NOT NULL,
    setting_value TEXT NOT NULL,
    setting_type VARCHAR(20) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Points history table
CREATE TABLE IF NOT EXISTS `points_history` (
    id VARCHAR(36) PRIMARY KEY,
    member_id VARCHAR(36) NOT NULL,
    transaction_id VARCHAR(36) NOT NULL,
    points_before DECIMAL(10,2) NOT NULL,
    points_change DECIMAL(10,2) NOT NULL,
    points_after DECIMAL(10,2) NOT NULL,
    type ENUM('Earned', 'Redeemed', 'Expired', 'Adjusted') NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES members(id),
    FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);

-- Inventory history table
CREATE TABLE IF NOT EXISTS `inventory_history` (
    id VARCHAR(36) PRIMARY KEY,
    item_id VARCHAR(36) NOT NULL,
    previous_quantity INT NOT NULL,
    new_quantity INT NOT NULL,
    change_type ENUM('Purchase', 'Sale', 'Adjustment', 'Return') NOT NULL,
    reference_id VARCHAR(36),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES inventory(id)
);

-- Member points history
CREATE TABLE IF NOT EXISTS `member_points_history` (
    id VARCHAR(36) PRIMARY KEY,
    member_id VARCHAR(36) NOT NULL,
    points_change INT NOT NULL,
    transaction_id VARCHAR(36),
    reason VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES members(id),
    FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);

-- Reports table
CREATE TABLE IF NOT EXISTS `reports` (
    id VARCHAR(36) PRIMARY KEY,
    report_name VARCHAR(100) NOT NULL,
    report_type VARCHAR(50) NOT NULL,
    description TEXT,
    query_template TEXT NOT NULL,
    parameters JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    is_active BOOLEAN DEFAULT true,
    display_order INT DEFAULT 0,
    category VARCHAR(50),
    UNIQUE KEY unique_report_name (report_name)
);

-- Printer config table
CREATE TABLE IF NOT EXISTS `printer_config` (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    printer_name VARCHAR(255) NOT NULL,
    paper_size VARCHAR(50) NOT NULL,
    auto_print BOOLEAN DEFAULT false,
    copies INTEGER DEFAULT 1,
    paper_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Receipt design table
CREATE TABLE IF NOT EXISTS `receipt_design` (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    header_text TEXT,
    footer_text TEXT,
    show_logo BOOLEAN DEFAULT false,
    logo_path VARCHAR(255),
    show_date_time BOOLEAN DEFAULT true,
    show_cashier_name BOOLEAN DEFAULT true,
    show_tax_details BOOLEAN DEFAULT true,
    font_family VARCHAR(100) DEFAULT 'Arial',
    font_size INTEGER DEFAULT 12,
    show_border BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- Indexes for performance
CREATE INDEX idx_transactions_date ON transactions(transaction_date);
CREATE INDEX idx_transactions_cashier ON transactions(cashier_id);
CREATE INDEX idx_transactions_member ON transactions(member_id);
CREATE INDEX idx_transaction_items_transaction ON transaction_items(transaction_id);
CREATE INDEX idx_points_history_member ON points_history(member_id);
CREATE INDEX idx_points_history_transaction ON points_history(transaction_id);
CREATE INDEX idx_members_code ON members(member_code);
CREATE INDEX idx_members_name ON members(full_name);
CREATE INDEX idx_settings_key ON settings(setting_key);
CREATE INDEX idx_inventory_category ON inventory(category);
CREATE INDEX idx_inventory_name ON inventory(item_name);
CREATE INDEX idx_inventory_barcode ON inventory(barcode);
CREATE INDEX idx_inventory_quantity ON inventory(quantity);
CREATE INDEX idx_inventory_price ON inventory(price, cost_price);
CREATE INDEX idx_inventory_reorder ON inventory(reorder_level, quantity);
CREATE INDEX idx_members_level ON members(membership_level);
CREATE INDEX idx_members_points ON members(points);
CREATE INDEX idx_members_join_date ON members(join_date);
CREATE INDEX idx_members_level_points ON members(membership_level, points);
CREATE INDEX idx_members_email ON members(email);
CREATE INDEX idx_members_phone ON members(contact_no);
CREATE INDEX idx_inventory_history_item ON inventory_history(item_id);
CREATE INDEX idx_inventory_history_date ON inventory_history(created_at);
CREATE INDEX idx_inventory_history_type ON inventory_history(change_type);
CREATE INDEX idx_member_points_member ON member_points_history(member_id);
CREATE INDEX idx_member_points_date ON member_points_history(created_at);
CREATE INDEX idx_member_points_reason ON member_points_history(reason);
CREATE INDEX idx_member_points_member_date ON member_points_history(member_id, created_at);
CREATE INDEX idx_member_points_transaction ON member_points_history(transaction_id);

-- DUMMY DATA

-- Users (needed for FKs)
INSERT INTO users (id, username, password_hash, full_name, email, role, active) VALUES
('U001', 'admin', 'admin', 'System Administrator', 'admin@pos.com', 'Admin', true),
('U002', 'cashier', 'cashier', 'Default Cashier', 'cashier@pos.com', 'Cashier', true);

-- Members
INSERT INTO members (id, member_code, full_name, contact_no, email, address, points, total_spent, membership_level, join_date, valid_until, active) VALUES
('M001', '1000001', 'John Doe', '09123456789', 'john@email.com', '123 Main St', 150.00, 5000.00, 'Gold', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR), true),
('M002', '1000002', 'Jane Smith', '09234567890', 'jane@email.com', '456 Oak Ave', 75.00, 2500.00, 'Silver', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR), true);

-- Categories
INSERT INTO categories (id, name, description) VALUES
('C001', 'Food', 'Food items and snacks'),
('C002', 'Beverages', 'Drinks and beverages'),
('C003', 'Household', 'Household items');

-- Inventory
INSERT INTO inventory (id, item_name, category, description, barcode, cost_price, price, quantity, reorder_level, image_path, is_active) VALUES
('I001', 'Bread', 'Food', 'Fresh bread', '1234567890', 35.00, 45.00, 100, 20, NULL, true),
('I002', 'Milk', 'Food', 'Fresh milk', '1234567891', 55.00, 65.00, 80, 15, NULL, true),
('I003', 'Coffee', 'Beverages', 'Ground coffee', '1234567892', 120.00, 150.00, 45, 15, NULL, true);

-- Transactions
INSERT INTO transactions (id, transaction_no, cashier_id, member_id, subtotal, discount_amount, tax_amount, total_amount, points_earned, points_redeemed, payment_method, amount_tendered, change_amount, status) VALUES
('T001', 'TXN001', 'U002', 'M001', 100.00, 0.00, 12.00, 112.00, 10.00, 0.00, 'Cash', 120.00, 8.00, 'Completed'),
('T002', 'TXN002', 'U002', 'M002', 200.00, 10.00, 24.00, 214.00, 20.00, 5.00, 'Card', 220.00, 6.00, 'Completed');

-- Transaction items
INSERT INTO transaction_items (id, transaction_id, item_id, quantity, unit_price, discount_amount, total_amount) VALUES
('TI001', 'T001', 'I001', 2, 45.00, 0.00, 90.00),
('TI002', 'T001', 'I003', 1, 150.00, 0.00, 150.00),
('TI003', 'T002', 'I002', 3, 65.00, 5.00, 190.00);

-- Returns
INSERT INTO returns (id, transaction_id, return_date, cashier_id, total_amount, reason, status) VALUES
('R001', 'T001', NOW(), 'U002', 45.00, 'Damaged item', 'Completed');

-- Return items
INSERT INTO return_items (id, return_id, product_id, quantity, unit_price, subtotal) VALUES
('RI001', 'R001', 'I001', 1, 45.00, 45.00);

-- Audit log
INSERT INTO audit_log (id, user_id, action_type, action_details, ip_address) VALUES
('A001', 'U001', 'LOGIN', 'Admin logged in', '127.0.0.1');

-- Settings
INSERT INTO settings (id, setting_key, setting_value, setting_type, description) VALUES
('S001', 'receipt_header', 'QuickVend POS System', 'text', 'Receipt header text'),
('S002', 'tax_rate', '12', 'number', 'VAT rate percentage');

-- Points history
INSERT INTO points_history (id, member_id, transaction_id, points_before, points_change, points_after, type, notes) VALUES
('PH001', 'M001', 'T001', 100, 10, 110, 'Earned', 'Purchase'),
('PH002', 'M002', 'T002', 50, 20, 70, 'Earned', 'Purchase');

-- Inventory history
INSERT INTO inventory_history (id, item_id, previous_quantity, new_quantity, change_type, reference_id, notes) VALUES
('IH001', 'I001', 120, 100, 'Sale', 'T001', 'Sold 2 units');

-- Member points history
INSERT INTO member_points_history (id, member_id, points_change, transaction_id, reason) VALUES
('MPH001', 'M001', 10, 'T001', 'Purchase points');

-- Reports
INSERT INTO reports (id, report_name, report_type, description, query_template, parameters, created_by, is_active, display_order, category) VALUES
('RPT001', 'Daily Sales', 'SALES', 'Shows daily sales', 'SELECT * FROM transactions', '{}', 'admin', true, 1, 'Sales');

-- Printer config (dummy data)
INSERT INTO printer_config (printer_name, paper_size, auto_print, copies, paper_type)
VALUES ('Default Printer', 'Thermal 80mm', false, 1, 'Thermal')
ON DUPLICATE KEY UPDATE printer_name=VALUES(printer_name);

-- Receipt design (dummy data)
INSERT INTO receipt_design (header_text, footer_text, show_logo, show_date_time, show_cashier_name, show_tax_details, font_family, font_size, show_border)
VALUES ('Welcome to Our Store', 'Thank you for shopping!', true, true, true, true, 'Arial', 12, false)
ON DUPLICATE KEY UPDATE header_text=VALUES(header_text); 