-- Drop and recreate users table
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('Admin', 'Cashier') NOT NULL,
    email VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert default users with BCrypt hashed passwords
-- admin/admin123 and cashier/cashier123
INSERT INTO users (id, username, password_hash, full_name, role, email, active) VALUES 
('U001', 'admin', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBAQHxQxJ5qK8y', 'System Administrator', 'Admin', 'admin@pos.com', true),
('U002', 'cashier', '$2a$12$8K1p/a0dR1LXMIgoEDFrwOeAQG6ldA3Uq1I3FQP9MZgHyUtwKQmqi', 'Default Cashier', 'Cashier', 'cashier@pos.com', true); 