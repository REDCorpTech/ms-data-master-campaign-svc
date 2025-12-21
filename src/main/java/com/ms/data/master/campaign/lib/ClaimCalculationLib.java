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
            if (allowedClaim <= 0) continue;

            List<CampaignScanLog> logs =
                    campaignScanLogRepository.findAllByEmailAndProductIdAndIsClaimFalseOrderByScanAtAsc(
                            summary.getEmail(),
                            summary.getProductId()
                    );

            // ambil hanya N yang boleh di-claim
            List<CampaignScanLog> logsToClaim = logs.stream()
                    .limit(allowedClaim)
                    .toList();

            logsToClaim.forEach(log -> log.setIsClaim(true));

            // ⬅️ PENTING: save hanya yang di-claim
            campaignScanLogRepository.saveAll(logsToClaim);
        }
    }
}
