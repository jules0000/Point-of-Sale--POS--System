-- Insert default users with BCrypt hashed passwords
-- admin/admin123 and cashier/cashier123
INSERT INTO users (id, username, password_hash, full_name, role, email, active) VALUES 
('U001', 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'System Administrator', 'Admin', 'admin@pos.com', true),
('U002', 'cashier', '$2a$10$8K1p/a0dR1LXMIgoEDFrwOeAQG6ldA3Uq1I3FQP9MZgHyUtwKQmqi', 'Default Cashier', 'Cashier', 'cashier@pos.com', true)
ON DUPLICATE KEY UPDATE id=id; 