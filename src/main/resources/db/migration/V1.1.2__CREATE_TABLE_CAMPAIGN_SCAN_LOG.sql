-- Drop table if exists
DROP TABLE IF EXISTS t_campaign_scan_log;

-- Drop indexes if exist
DROP INDEX IF EXISTS idx_campaign_scan_log_json_created_at;
DROP INDEX IF EXISTS idx_campaign_scan_log_json_updated_at;

-- Create table
CREATE TABLE t_campaign_scan_log (
                                                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                                    email VARCHAR,
                                                                    product_details JSONB,
                                                                    is_claim BOOLEAN DEFAULT FALSE,
                                                                    campaign_name VARCHAR,
                                                                    scan_by VARCHAR,
                                                                    scan_at TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_campaign_scan_log_json_created_at ON t_campaign_scan_log(scan_at);
CREATE INDEX idx_campaign_scan_log_json_updated_at ON t_campaign_scan_log(scan_by);

-- CREATE EXTENSION
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
