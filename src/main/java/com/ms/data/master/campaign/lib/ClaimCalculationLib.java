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

    @Transactional
    public void processClaimForCustomer(String email) {

        List<CampaignScanSummaryProjection> summaries =
                campaignScanLogRepository.findCampaignScanSummaryByEmail(email);

        for (CampaignScanSummaryProjection summary : summaries) {

            int scanPerClaim = summary.getScanPerClaim();

            while (true) {

                int claimedScanCount =
                        campaignScanLogRepository.countClaimedScan(
                                summary.getEmail(),
                                summary.getProductId()
                        );

                int totalScan = summary.getTotalScan();

                int allowedClaim = totalScan / scanPerClaim;
                int usedClaim = claimedScanCount / scanPerClaim;

                if (usedClaim >= allowedClaim) {
                    break;
                }

                List<CampaignScanLog> unclaimedLogs =
                        campaignScanLogRepository
                                .findUnclaimedByEmailAndProductOrderByScanAtAsc(
                                        summary.getEmail(),
                                        summary.getProductId()
                                );

                if (unclaimedLogs.size() < scanPerClaim) {
                    break;
                }

                unclaimedLogs.subList(0, scanPerClaim)
                        .forEach(log -> log.setIsClaim(true));

                campaignScanLogRepository.saveAll(
                        unclaimedLogs.subList(0, scanPerClaim)
                );
            }
        }
    }


}
