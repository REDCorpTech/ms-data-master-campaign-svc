CREATE INDEX idx_campaign_product_details_gin
    ON t_campaign
    USING GIN (product_details);

CREATE INDEX idx_scan_log_product_details_gin
    ON t_campaign_scan_log
    USING GIN (product_details);

CREATE INDEX idx_scan_log_email
    ON t_campaign_scan_log(email);

