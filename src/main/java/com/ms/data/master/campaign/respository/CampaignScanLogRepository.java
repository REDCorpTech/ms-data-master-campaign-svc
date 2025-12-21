package com.ms.data.master.campaign.respository;

import com.ms.data.master.campaign.model.Campaign;
import com.ms.data.master.campaign.model.CampaignScanLog;
import com.ms.data.master.campaign.model.dto.campaign.CampaignScanSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CampaignScanLogRepository extends JpaRepository<CampaignScanLog, UUID>, JpaSpecificationExecutor<CampaignScanLog> {
    Long countByEmail(String email);


    @Query(
            value = """
        SELECT
        sl.email                                AS email,
        pd->>'id'                               AS productId,
        pd->>'productName'                      AS productName,
        COUNT(*)                                AS totalScan,
        c.scan_count                            AS scanPerClaim,
        FLOOR(COUNT(*)::numeric / c.scan_count) AS allowedClaim
    FROM "ms-data-master-campaign-svc".t_campaign_scan_log sl
    JOIN LATERAL jsonb_array_elements(sl.product_details) pd ON true
    JOIN "ms-data-master-campaign-svc".t_campaign c
        ON c.product_details @> jsonb_build_array(
            jsonb_build_object('id', pd->>'id')
        )
    WHERE sl.email = :email
    GROUP BY
        sl.email,
        pd->>'id',
        pd->>'productName',
        c.scan_count
    ORDER BY
        productName
    """,
            nativeQuery = true
    )
    List<CampaignScanSummaryProjection> findCampaignScanSummaryByEmail(
            @Param("email") String email
    );

    @Query(value = """
    SELECT sl.*
    FROM "ms-data-master-campaign-svc".t_campaign_scan_log sl
    JOIN LATERAL jsonb_array_elements(sl.product_details) pd ON true
    WHERE sl.email = :email
      AND pd->>'id' = :productId
      AND sl.is_claim = false
    ORDER BY sl.scan_at ASC
    """, nativeQuery = true)
    List<CampaignScanLog> findAllByEmailAndProductIdAndIsClaimFalseOrderByScanAtAsc(
            @Param("email") String email,
            @Param("productId") String productId
    );




}
