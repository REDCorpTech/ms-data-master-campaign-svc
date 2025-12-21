package com.ms.data.master.campaign.lib;

import com.ms.data.master.campaign.model.CampaignScanLog;
import com.ms.data.master.campaign.model.dto.campaign.CampaignScanSummaryProjection;
import com.ms.data.master.campaign.respository.CampaignScanLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;


@Transactional
@RequiredArgsConstructor
@Slf4j
@Component
public class ClaimCalculationLib {

    private final CampaignScanLogRepository campaignScanLogRepository;

    public void processClaimForCustomer(String email) {
        List<CampaignScanSummaryProjection> scanSummary =
                campaignScanLogRepository.findCampaignScanSummaryByEmail(email);

        for (CampaignScanSummaryProjection summary : scanSummary) {
            int allowedClaim = summary.getAllowedClaim();
            if (allowedClaim > 0) {
                List<CampaignScanLog> logsToClaim =
                        campaignScanLogRepository.findAllByEmailAndProductIdAndIsClaimFalseOrderByScanAtAsc(
                                summary.getEmail(), summary.getProductId());

                logsToClaim.stream()
                        .limit(allowedClaim)
                        .forEach(log -> log.setIsClaim(true));

                // Batch update sekali saja
                campaignScanLogRepository.saveAll(logsToClaim);
            }
        }
    }
}
