-- Drop table if exists
DROP TABLE IF EXISTS t_coupon;

-- Drop indexes if exist
DROP INDEX IF EXISTS idx_coupon_json_created_at;
DROP INDEX IF EXISTS idx_coupon_json_updated_at;

-- Create table
CREATE TABLE t_coupon (
                                                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                                    coupon_code VARCHAR UNIQUE,
                                                                    customer_coupon_redeemer_details JSONB,
                                                                    coupon_status VARCHAR,
                                                                    created_by VARCHAR,
                                                                    created_at TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_coupon_json_created_at ON t_coupon(scan_at);
CREATE INDEX idx_coupon_json_updated_at ON t_coupon(scan_by);

-- CREATE EXTENSION
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
