package com.ms.data.master.campaign.model.dto.campaign;

public interface CampaignScanSummaryProjection {
    String getEmail();

    String getProductId();

    String getProductName();

    Integer getTotalScan();

    Integer getScanPerClaim();

    Integer getAllowedClaim();
}