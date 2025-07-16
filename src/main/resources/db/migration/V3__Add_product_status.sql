ALTER TABLE products ADD COLUMN status VARCHAR(20) DEFAULT 'ACTIVE';
ALTER TABLE products ADD COLUMN sku VARCHAR(50) UNIQUE;

-- Create index for SKU
CREATE INDEX idx_products_sku ON products(sku);