-- SQL for categories table
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Insert some default categories (optional)
INSERT IGNORE INTO categories (name) VALUES ('Food'), ('Beverages'), ('Household'); 