CREATE TABLE product_audit (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL,
    old_values JSONB,
    new_values JSONB,
    changed_by VARCHAR(255),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Create index for audit queries
CREATE INDEX idx_product_audit_product_id ON product_audit(product_id);
CREATE INDEX idx_product_audit_changed_at ON product_audit(changed_at);

-- docker/postgres/init.sql
-- This script runs when the PostgreSQL container starts for the first time
CREATE USER product_api_user WITH PASSWORD 'product_api_password';
GRANT ALL PRIVILEGES ON DATABASE products_db TO product_api_user;

