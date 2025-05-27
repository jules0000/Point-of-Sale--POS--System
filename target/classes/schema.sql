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

-- Insert default settings
INSERT INTO settings (id, setting_key, setting_value, setting_type, description) VALUES
-- Receipt Settings
('S001', 'receipt_header', 'QuickVend POS System\n123 Main Street\nAnytown, Philippines', 'text', 'Receipt header text'),
('S002', 'receipt_footer', 'Thank you for shopping!\nPlease come again.', 'text', 'Receipt footer text'),
('S003', 'receipt_show_tax', 'true', 'boolean', 'Show tax breakdown on receipt'),
('S004', 'receipt_show_cashier', 'true', 'boolean', 'Show cashier name on receipt'),
('S005', 'receipt_paper_width', '80', 'number', 'Receipt paper width in mm'),

-- Printer Settings
('S006', 'printer_name', 'POS-80', 'text', 'Default receipt printer name'),
('S007', 'printer_port', 'COM1', 'text', 'Printer port (for serial printers)'),
('S008', 'printer_char_per_line', '42', 'number', 'Characters per line for receipt'),

-- Points Settings
('S009', 'points_peso_per_point', '25', 'number', 'Pesos spent to earn 1 point'),
('S010', 'points_peso_value', '1', 'number', 'Peso value of 1 point when redeeming'),
('S011', 'points_minimum_redeem', '100', 'number', 'Minimum points required for redemption'),
('S012', 'points_expiry_months', '12', 'number', 'Number of months before points expire'),

-- Tax Settings
('S013', 'tax_rate', '12', 'number', 'VAT rate percentage'),
('S014', 'tax_inclusive', 'true', 'boolean', 'Whether prices include tax');

-- Insert sample members
INSERT INTO members (id, member_code, full_name, contact_no, email, points, total_spent, membership_level, join_date, valid_until, active) VALUES
('M001', '1000001', 'John Doe', '09123456789', 'john@email.com', 150.00, 5000.00, 'Gold', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR), true),
('M002', '1000002', 'Jane Smith', '09234567890', 'jane@email.com', 75.00, 2500.00, 'Silver', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR), true);

-- Create indexes
CREATE INDEX idx_transactions_date ON transactions(transaction_date);
CREATE INDEX idx_transactions_cashier ON transactions(cashier_id);
CREATE INDEX idx_transactions_member ON transactions(member_id);
CREATE INDEX idx_transaction_items_transaction ON transaction_items(transaction_id);
CREATE INDEX idx_points_history_member ON points_history(member_id);
CREATE INDEX idx_points_history_transaction ON points_history(transaction_id);
CREATE INDEX idx_members_code ON members(member_code);
CREATE INDEX idx_members_name ON members(full_name);
CREATE INDEX idx_settings_key ON settings(setting_key);

-- Categories table
CREATE TABLE IF NOT EXISTS `categories` (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Inventory history table for tracking stock changes
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

-- Create audit_logs table
CREATE TABLE IF NOT EXISTS `audit_logs` (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36),
    action VARCHAR(50) NOT NULL,
    table_name VARCHAR(50) NOT NULL,
    record_id VARCHAR(36),
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create indexes for better query performance
-- Transaction indexes for date-based reporting
CREATE INDEX `idx_transactions_date` ON `transactions`(transaction_date);
CREATE INDEX `idx_transactions_date_amount` ON `transactions`(transaction_date, total_amount);
CREATE INDEX `idx_transactions_date_payment` ON `transactions`(transaction_date, payment_method);
CREATE INDEX `idx_transactions_date_status` ON `transactions`(transaction_date, status);
CREATE INDEX `idx_transactions_date_cashier` ON `transactions`(transaction_date, cashier_id);
CREATE INDEX `idx_transactions_date_member` ON `transactions`(transaction_date, member_id);
CREATE INDEX `idx_transactions_date_payment_status` ON `transactions`(transaction_date, payment_method, status);
CREATE INDEX `idx_transactions_date_amount_status` ON `transactions`(transaction_date, total_amount, status);
CREATE INDEX `idx_transactions_date_member_amount` ON `transactions`(transaction_date, member_id, total_amount);
CREATE INDEX `idx_transactions_date_payment_status_amount` ON `transactions`(transaction_date, payment_method, status, total_amount);
CREATE INDEX `idx_transactions_date_member_status` ON `transactions`(transaction_date, member_id, status);

-- Transaction items indexes
CREATE INDEX `idx_transaction_items_transaction` ON `transaction_items`(transaction_id);
CREATE INDEX `idx_transaction_items_item` ON `transaction_items`(item_id);
CREATE INDEX `idx_transaction_items_price` ON `transaction_items`(unit_price);
CREATE INDEX `idx_transaction_items_quantity` ON `transaction_items`(quantity);
CREATE INDEX `idx_transaction_items_transaction_item` ON `transaction_items`(transaction_id, item_id);
CREATE INDEX `idx_transaction_items_price_quantity` ON `transaction_items`(unit_price, quantity);
CREATE INDEX `idx_transaction_items_discount` ON `transaction_items`(discount_amount);
CREATE INDEX `idx_transaction_items_transaction_item_price` ON `transaction_items`(transaction_id, item_id, unit_price);

-- Inventory indexes
CREATE INDEX `idx_inventory_category` ON `inventory`(category);
CREATE INDEX `idx_inventory_name` ON `inventory`(item_name);
CREATE INDEX `idx_inventory_barcode` ON `inventory`(barcode);
CREATE INDEX `idx_inventory_quantity` ON `inventory`(quantity);
CREATE INDEX `idx_inventory_price` ON `inventory`(price, cost_price);
CREATE INDEX `idx_inventory_category_quantity` ON `inventory`(category, quantity);
CREATE INDEX `idx_inventory_reorder` ON `inventory`(reorder_level, quantity);
CREATE INDEX `idx_inventory_category_quantity_price` ON `inventory`(category, quantity, price);

-- Member indexes
CREATE INDEX `idx_members_level` ON `members`(membership_level);
CREATE INDEX `idx_members_points` ON `members`(points);
CREATE INDEX `idx_members_join_date` ON `members`(join_date);
CREATE INDEX `idx_members_level_points` ON `members`(membership_level, points);
CREATE INDEX `idx_members_email` ON `members`(email);
CREATE INDEX `idx_members_phone` ON `members`(contact_no);

-- History indexes
CREATE INDEX `idx_inventory_history_item` ON `inventory_history`(item_id);
CREATE INDEX `idx_inventory_history_date` ON `inventory_history`(created_at);
CREATE INDEX `idx_inventory_history_type` ON `inventory_history`(change_type);
CREATE INDEX `idx_inventory_history_item_date` ON `inventory_history`(item_id, created_at);
CREATE INDEX `idx_inventory_history_type_date` ON `inventory_history`(change_type, created_at);

CREATE INDEX `idx_member_points_member` ON `member_points_history`(member_id);
CREATE INDEX `idx_member_points_date` ON `member_points_history`(created_at);
CREATE INDEX `idx_member_points_reason` ON `member_points_history`(reason);
CREATE INDEX `idx_member_points_member_date` ON `member_points_history`(member_id, created_at);
CREATE INDEX `idx_member_points_transaction` ON `member_points_history`(transaction_id);

-- Create indexes for audit logs
CREATE INDEX `idx_audit_logs_user` ON `audit_logs`(user_id);
CREATE INDEX `idx_audit_logs_action` ON `audit_logs`(action);
CREATE INDEX `idx_audit_logs_table` ON `audit_logs`(table_name);
CREATE INDEX `idx_audit_logs_record` ON `audit_logs`(record_id);
CREATE INDEX `idx_audit_logs_date` ON `audit_logs`(created_at);
CREATE INDEX `idx_audit_logs_user_action` ON `audit_logs`(user_id, action);
CREATE INDEX `idx_audit_logs_table_record` ON `audit_logs`(table_name, record_id);
CREATE INDEX `idx_audit_logs_user_date` ON `audit_logs`(user_id, created_at);

-- Insert default users with BCrypt hashed passwords
-- admin/admin and cashier/cashier
INSERT INTO users (id, username, password_hash, full_name, role, email, active)
VALUES 
    ('1', 'admin', '$2a$10$aAnNmAzditmUCjEMLqSB2u5OMJTzYWrIqopq/T5DzkG55PMy2wTBi', 'System Administrator', 'Admin', 'admin@pos.com', true),
    ('2', 'cashier', '$2a$10$HUqmTZbJcdwPkqJTsUFSoe5JJM5tMgN/SYZakzD9GQYQdUVb8oz0O', 'Default Cashier', 'Cashier', 'cashier@pos.com', true);

-- Insert sample categories
INSERT INTO `categories` (id, name, description) VALUES
('C001', 'Food', 'Food items and snacks'),
('C002', 'Beverages', 'Drinks and beverages'),
('C003', 'Household', 'Household items'),
('C004', 'Electronics', 'Electronic items and accessories')
ON DUPLICATE KEY UPDATE id=id;

-- Insert sample inventory items
INSERT INTO `inventory` (id, item_name, category, description, barcode, cost_price, price, quantity, reorder_level) VALUES
('I001', 'Bread', 'Food', 'Fresh bread', '1234567890', 1.50, 2.50, 100, 20),
('I002', 'Milk', 'Food', 'Fresh milk', '1234567891', 2.00, 3.00, 80, 15),
('I003', 'Coffee', 'Beverages', 'Ground coffee', '1234567892', 4.00, 5.75, 50, 10),
('I004', 'Soap', 'Household', 'Bath soap', '1234567893', 1.50, 2.25, 60, 10),
('I005', 'Paper Towels', 'Household', 'Kitchen paper towels', '1234567894', 3.00, 4.75, 40, 10),
('I006', 'Juice', 'Beverages', 'Orange juice', '1234567895', 2.50, 3.50, 30, 10),
('I007', 'Eggs', 'Food', 'Dozen eggs', '1234567896', 3.00, 4.25, 50, 10),
('I008', 'Water', 'Beverages', 'Bottled water', '1234567897', 0.50, 1.00, 200, 20)
ON DUPLICATE KEY UPDATE id=id;

-- Insert sample transactions (last 7 days)
INSERT INTO `transactions` (id, transaction_no, cashier_id, member_id, subtotal, discount_amount, tax_amount, total_amount, points_earned, points_redeemed, payment_method, amount_tendered, change_amount, status) VALUES
('T001', '1000001', '2', 'M001', 25.50, 0.00, 2.00, 27.50, 25.00, 0.00, 'Cash', 27.50, 0.00, 'Completed'),
('T002', '1000002', '2', 'M002', 15.75, 0.00, 1.00, 16.75, 15.00, 0.00, 'Credit Card', 16.75, 0.00, 'Completed'),
('T003', '1000003', '2', 'M003', 8.50, 0.00, 0.00, 8.50, 8.00, 0.00, 'Debit Card', 8.50, 0.00, 'Completed'),
('T004', '1000004', '2', 'M001', 32.25, 0.00, 2.00, 34.25, 32.00, 0.00, 'Credit Card', 34.25, 0.00, 'Completed'),
('T005', '1000005', '2', 'M004', 45.00, 0.00, 3.00, 48.00, 45.00, 0.00, 'Mobile Payment', 48.00, 0.00, 'Completed'),
('T006', '1000006', '2', 'M002', 18.75, 0.00, 1.00, 19.75, 18.00, 0.00, 'Cash', 19.75, 0.00, 'Completed'),
('T007', '1000007', '2', 'M003', 12.50, 0.00, 0.00, 12.50, 12.00, 0.00, 'Debit Card', 12.50, 0.00, 'Completed')
ON DUPLICATE KEY UPDATE id=id;

-- Insert sample transaction items
INSERT INTO `transaction_items` (id, transaction_id, item_id, quantity, unit_price, discount_amount, total_amount) VALUES
-- T001 items
('TI001', 'T001', 'I001', 2, 2.50, 0.00, 5.00),
('TI002', 'T001', 'I002', 1, 3.00, 0.00, 3.00),
('TI003', 'T001', 'I003', 3, 5.75, 0.00, 17.25),
-- T002 items
('TI004', 'T002', 'I004', 2, 2.25, 0.00, 4.50),
('TI005', 'T002', 'I005', 1, 4.75, 0.00, 4.75),
('TI006', 'T002', 'I006', 2, 3.50, 0.00, 7.00),
-- T003 items
('TI007', 'T003', 'I007', 1, 4.25, 0.00, 4.25),
('TI008', 'T003', 'I008', 4, 1.00, 0.00, 4.00),
-- T004 items
('TI009', 'T004', 'I001', 3, 2.50, 0.00, 7.50),
('TI010', 'T004', 'I002', 2, 3.00, 0.00, 6.00),
('TI011', 'T004', 'I003', 4, 5.75, 0.00, 23.00),
-- T005 items
('TI012', 'T005', 'I004', 5, 2.25, 0.00, 11.25),
('TI013', 'T005', 'I005', 3, 4.75, 0.00, 14.25),
('TI014', 'T005', 'I006', 4, 3.50, 0.00, 14.00),
-- T006 items
('TI015', 'T006', 'I007', 2, 4.25, 0.00, 8.50),
('TI016', 'T006', 'I008', 3, 1.00, 0.00, 3.00),
-- T007 items
('TI017', 'T007', 'I001', 1, 2.50, 0.00, 2.50),
('TI018', 'T007', 'I002', 2, 3.00, 0.00, 6.00),
('TI019', 'T007', 'I003', 1, 5.75, 0.00, 5.75)
ON DUPLICATE KEY UPDATE id=id;

-- Insert sample inventory history
INSERT INTO `inventory_history` (id, item_id, previous_quantity, new_quantity, change_type, reference_id, notes) VALUES
('IH001', 'I001', 100, 95, 'Sale', 'T001', 'Sale transaction'),
('IH002', 'I002', 80, 79, 'Sale', 'T001', 'Sale transaction'),
('IH003', 'I003', 50, 47, 'Sale', 'T001', 'Sale transaction'),
('IH004', 'I004', 60, 58, 'Sale', 'T002', 'Sale transaction'),
('IH005', 'I005', 40, 39, 'Sale', 'T002', 'Sale transaction')
ON DUPLICATE KEY UPDATE id=id;

-- Insert sample member points history
INSERT INTO `member_points_history` (id, member_id, points_change, transaction_id, reason) VALUES
('MP001', 'M001', 25, 'T001', 'Purchase points'),
('MP002', 'M002', 15, 'T002', 'Purchase points'),
('MP003', 'M003', 8, 'T003', 'Purchase points'),
('MP004', 'M001', 32, 'T004', 'Purchase points'),
('MP005', 'M004', 45, 'T005', 'Purchase points')
ON DUPLICATE KEY UPDATE id=id;

-- The transactions table should NOT have an item_id column. The item_id is in transaction_items.
-- If your application is trying to use item_id in transactions, refactor the code to use transaction_items instead.
-- No changes needed to the transactions table for item_id.
--
-- If you need to add item_id to transactions (not recommended), uncomment the following line:
-- ALTER TABLE `transactions` ADD COLUMN `item_id` VARCHAR(36) AFTER `tax_amount`; 