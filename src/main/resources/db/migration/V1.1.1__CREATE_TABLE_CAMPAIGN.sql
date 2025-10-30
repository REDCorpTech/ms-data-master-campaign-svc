-- Drop table if exists
DROP TABLE IF EXISTS t_campaign;

-- Drop indexes if exist
DROP INDEX IF EXISTS idx_campaign_json_created_at;
DROP INDEX IF EXISTS idx_campaign_json_updated_at;

-- Create table
CREATE TABLE t_campaign (
                                                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                                    campaign_name VARCHAR,
                                                                    product_details JSONB,
                                                                    title_id_lang VARCHAR,
                                                                    title_en_lang VARCHAR,
                                                                    dscp_id_lang VARCHAR,
                                                                    dscp_en_lang VARCHAR,
                                                                    sub_dscp_id_lang VARCHAR,
                                                                    sub_dscp_en_lang VARCHAR,
                                                                    start_date TIMESTAMP,
                                                                    end_date TIMESTAMP,
                                                                    scan_count INTEGER,
                                                                    campaign_status VARCHAR,
                                                                    created_by VARCHAR,
                                                                    created_at TIMESTAMP,
                                                                    updated_by VARCHAR,
                                                                    updated_at TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_campaign_json_created_at ON t_campaign(created_at);
CREATE INDEX idx_campaign_json_updated_at ON t_campaign(updated_at);

-- CREATE EXTENSION
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
