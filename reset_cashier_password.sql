-- Reset cashier password to 'cashier'
UPDATE users 
SET password_hash = '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBAQHxQxJ5JQHy' 
WHERE username = 'cashier';

-- Make sure cashier account is active
UPDATE users 
SET active = true 
WHERE username = 'cashier'; 