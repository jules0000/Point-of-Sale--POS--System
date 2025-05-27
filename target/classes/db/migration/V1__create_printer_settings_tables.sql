-- Create printer_config table
CREATE TABLE IF NOT EXISTS printer_config (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    printer_name VARCHAR(255) NOT NULL,
    paper_size VARCHAR(50) NOT NULL,
    auto_print BOOLEAN DEFAULT false,
    copies INTEGER DEFAULT 1,
    paper_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create receipt_design table
CREATE TABLE IF NOT EXISTS receipt_design (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
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

-- Insert default values
INSERT INTO printer_config (printer_name, paper_size, paper_type)
VALUES ('Default Printer', 'Thermal 80mm', 'Thermal');

INSERT INTO receipt_design (header_text, footer_text)
VALUES ('Welcome to Our Store', 'Thank you for shopping with us!'); 