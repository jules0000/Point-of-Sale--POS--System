-- Insert sample users (passwords are hashed using BCrypt)
-- Default passwords: admin/admin and cashier/cashier
INSERT INTO users (id, username, password_hash, full_name, role, email, active) VALUES 
('U001', 'admin', '$2a$10$aAnNmAzditmUCjEMLqSB2u5OMJTzYWrIqopq/T5DzkG55PMy2wTBi', 'System Administrator', 'Admin', 'admin@pos.com', true),
('U002', 'cashier1', '$2a$10$HUqmTZbJcdwPkqJTsUFSoe5JJM5tMgN/SYZakzD9GQYQdUVb8oz0O', 'John Cashier', 'Cashier', 'john@pos.com', true),
('U003', 'cashier2', '$2a$10$HUqmTZbJcdwPkqJTsUFSoe5JJM5tMgN/SYZakzD9GQYQdUVb8oz0O', 'Jane Cashier', 'Cashier', 'jane@pos.com', true),
('U004', 'manager', '$2a$10$aAnNmAzditmUCjEMLqSB2u5OMJTzYWrIqopq/T5DzkG55PMy2wTBi', 'Store Manager', 'Admin', 'manager@pos.com', true)
ON DUPLICATE KEY UPDATE id=id;

-- Insert sample categories
INSERT INTO categories (id, name, description) VALUES
('C001', 'Food', 'Food items and snacks'),
('C002', 'Beverages', 'Drinks and beverages'),
('C003', 'Household', 'Household items'),
('C004', 'Electronics', 'Electronic items and accessories'),
('C005', 'Personal Care', 'Personal care and hygiene products'),
('C006', 'Stationery', 'Office and school supplies')
ON DUPLICATE KEY UPDATE id=id;

-- Insert sample inventory items
INSERT INTO inventory (id, item_name, category, description, barcode, cost_price, price, quantity, reorder_level) VALUES
-- Food items
('I001', 'Bread', 'Food', 'Fresh bread', '1234567890', 35.00, 45.00, 100, 20),
('I002', 'Milk 1L', 'Food', 'Fresh milk', '1234567891', 55.00, 65.00, 80, 15),
('I003', 'Rice 5kg', 'Food', 'Premium rice', '1234567892', 220.00, 250.00, 50, 10),
('I004', 'Eggs', 'Food', 'Fresh eggs per tray', '1234567893', 180.00, 200.00, 40, 10),
('I005', 'Instant Noodles', 'Food', 'Instant noodles pack', '1234567894', 12.00, 15.00, 200, 50),

-- Beverages
('I006', 'Coca Cola 1.5L', 'Beverages', 'Soft drink', '1234567895', 45.00, 55.00, 150, 30),
('I007', 'Water 500ml', 'Beverages', 'Mineral water', '1234567896', 10.00, 15.00, 300, 50),
('I008', 'Coffee 100g', 'Beverages', 'Ground coffee', '1234567897', 120.00, 150.00, 45, 15),
('I009', 'Tea Bags', 'Beverages', '100 tea bags', '1234567898', 80.00, 100.00, 60, 20),
('I010', 'Orange Juice 1L', 'Beverages', 'Fresh orange juice', '1234567899', 65.00, 80.00, 40, 15),

-- Household items
('I011', 'Soap', 'Household', 'Bath soap', '1234567900', 25.00, 35.00, 120, 30),
('I012', 'Paper Towels', 'Household', 'Kitchen paper towels', '1234567901', 45.00, 60.00, 80, 20),
('I013', 'Trash Bags', 'Household', '30pcs trash bags', '1234567902', 85.00, 100.00, 100, 25),
('I014', 'Dishwashing Liquid', 'Household', '500ml bottle', '1234567903', 35.00, 45.00, 70, 20),
('I015', 'Laundry Detergent', 'Household', '1kg powder', '1234567904', 90.00, 110.00, 60, 15)
ON DUPLICATE KEY UPDATE id=id;

-- Insert sample transactions (last 7 days)
INSERT INTO transactions (id, transaction_date, cashier_id, total_amount, payment_method, status) VALUES
('T001', DATE_SUB(NOW(), INTERVAL 1 HOUR), 'U002', 350.00, 'Cash', 'Completed'),
('T002', DATE_SUB(NOW(), INTERVAL 2 HOUR), 'U002', 725.50, 'Credit Card', 'Completed'),
('T003', DATE_SUB(NOW(), INTERVAL 3 HOUR), 'U003', 150.25, 'Cash', 'Completed'),
('T004', DATE_SUB(NOW(), INTERVAL 4 HOUR), 'U003', 980.00, 'Mobile Payment', 'Completed'),
('T005', DATE_SUB(NOW(), INTERVAL 1 DAY), 'U002', 445.00, 'Cash', 'Completed'),
('T006', DATE_SUB(NOW(), INTERVAL 1 DAY), 'U003', 1250.75, 'Credit Card', 'Completed'),
('T007', DATE_SUB(NOW(), INTERVAL 2 DAY), 'U002', 890.50, 'Mobile Payment', 'Completed'),
('T008', DATE_SUB(NOW(), INTERVAL 2 DAY), 'U003', 675.25, 'Cash', 'Completed'),
('T009', DATE_SUB(NOW(), INTERVAL 3 DAY), 'U002', 1450.00, 'Credit Card', 'Completed'),
('T010', DATE_SUB(NOW(), INTERVAL 3 DAY), 'U003', 550.75, 'Cash', 'Completed')
ON DUPLICATE KEY UPDATE id=id;

-- Insert sample transaction items
INSERT INTO transaction_items (id, transaction_id, item_id, quantity, price) VALUES
-- Transaction T001
('TI001', 'T001', 'I001', 2, 45.00),
('TI002', 'T001', 'I002', 4, 65.00),
-- Transaction T002
('TI003', 'T002', 'I003', 2, 250.00),
('TI004', 'T002', 'I004', 1, 200.00),
-- Transaction T003
('TI005', 'T003', 'I005', 10, 15.00),
-- Transaction T004
('TI006', 'T004', 'I006', 8, 55.00),
('TI007', 'T004', 'I007', 20, 15.00),
-- Transaction T005
('TI008', 'T005', 'I008', 2, 150.00),
('TI009', 'T005', 'I009', 1, 100.00),
-- Transaction T006
('TI010', 'T006', 'I010', 5, 80.00),
('TI011', 'T006', 'I011', 10, 35.00),
-- Transaction T007
('TI012', 'T007', 'I012', 5, 60.00),
('TI013', 'T007', 'I013', 3, 100.00),
-- Transaction T008
('TI014', 'T008', 'I014', 5, 45.00),
('TI015', 'T008', 'I015', 3, 110.00)
ON DUPLICATE KEY UPDATE id=id;

-- Insert sample inventory history
INSERT INTO inventory_history (id, item_id, previous_quantity, new_quantity, change_type, notes) VALUES
('IH001', 'I001', 120, 100, 'Sale', 'Regular sale'),
('IH002', 'I002', 100, 80, 'Sale', 'Regular sale'),
('IH003', 'I003', 75, 50, 'Sale', 'Bulk purchase'),
('IH004', 'I004', 60, 40, 'Sale', 'Regular sale'),
('IH005', 'I005', 250, 200, 'Sale', 'Bulk purchase'),
('IH006', 'I006', 200, 150, 'Sale', 'Regular sale'),
('IH007', 'I007', 350, 300, 'Sale', 'Regular sale'),
('IH008', 'I008', 50, 45, 'Sale', 'Regular sale'),
('IH009', 'I009', 75, 60, 'Sale', 'Regular sale'),
('IH010', 'I010', 50, 40, 'Sale', 'Regular sale')
ON DUPLICATE KEY UPDATE id=id; 