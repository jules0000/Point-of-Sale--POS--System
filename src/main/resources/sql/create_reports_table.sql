-- Create reports table
CREATE TABLE IF NOT EXISTS reports (
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

-- Insert some default reports
INSERT INTO reports (id, report_name, report_type, description, query_template, parameters, category) VALUES
(
    UUID(),
    'Daily Sales Report',
    'SALES',
    'Shows daily sales summary with totals and payment methods',
    'SELECT 
        DATE(created_at) as sale_date,
        COUNT(*) as total_transactions,
        SUM(total_amount) as total_sales,
        SUM(CASE WHEN payment_method = ''Cash'' THEN total_amount ELSE 0 END) as cash_sales,
        SUM(CASE WHEN payment_method = ''Debit'' THEN total_amount ELSE 0 END) as debit_sales
    FROM transactions 
    WHERE DATE(created_at) = :date
    GROUP BY DATE(created_at)',
    '{"date": {"type": "date", "label": "Report Date", "required": true}}',
    'Sales'
),
(
    UUID(),
    'Inventory Status Report',
    'INVENTORY',
    'Shows current inventory levels and low stock items',
    'SELECT 
        i.item_name,
        i.category,
        i.quantity,
        i.reorder_level,
        CASE 
            WHEN i.quantity <= i.reorder_level THEN ''Low Stock''
            ELSE ''In Stock''
        END as status
    FROM inventory i
    WHERE i.quantity > 0
    ORDER BY i.category, i.item_name',
    '{}',
    'Inventory'
),
(
    UUID(),
    'Member Points Report',
    'MEMBERSHIP',
    'Shows member points history and current balances',
    'SELECT 
        m.member_code,
        m.full_name,
        m.membership_level,
        m.points,
        m.total_spent,
        COUNT(t.id) as total_transactions
    FROM members m
    LEFT JOIN transactions t ON m.id = t.member_id
    WHERE m.active = true
    GROUP BY m.id
    ORDER BY m.points DESC',
    '{}',
    'Membership'
),
(
    UUID(),
    'Product Performance Report',
    'SALES',
    'Shows top selling products and their performance metrics',
    'SELECT 
        i.item_name,
        i.category,
        COUNT(ti.id) as times_sold,
        SUM(ti.quantity) as total_quantity,
        SUM(ti.total_amount) as total_revenue
    FROM transaction_items ti
    JOIN inventory i ON ti.item_id = i.id
    WHERE ti.created_at >= :start_date AND ti.created_at <= :end_date
    GROUP BY i.id
    ORDER BY total_revenue DESC',
    '{
        "start_date": {"type": "date", "label": "Start Date", "required": true},
        "end_date": {"type": "date", "label": "End Date", "required": true}
    }',
    'Sales'
),
(
    UUID(),
    'Cashier Performance Report',
    'STAFF',
    'Shows cashier performance metrics and transaction counts',
    'SELECT 
        t.cashier_id,
        COUNT(*) as total_transactions,
        SUM(t.total_amount) as total_sales,
        AVG(t.total_amount) as average_sale,
        COUNT(DISTINCT DATE(t.created_at)) as days_worked
    FROM transactions t
    WHERE t.created_at >= :start_date AND t.created_at <= :end_date
    GROUP BY t.cashier_id
    ORDER BY total_sales DESC',
    '{
        "start_date": {"type": "date", "label": "Start Date", "required": true},
        "end_date": {"type": "date", "label": "End Date", "required": true}
    }',
    'Staff'
); 