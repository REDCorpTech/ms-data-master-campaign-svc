package com.ms.data.master.campaign.service;

import com.ms.data.master.campaign.model.Campaign;
import com.ms.data.master.campaign.model.dto.campaign.CampaignStatus;
import com.ms.data.master.campaign.respository.CampaignRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignStatusScheduler {

    private final CampaignRepository campaignRepository;
    private final CampaignService campaignService;

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void updateAllCampaignStatuses() {

        List<Campaign> campaigns = campaignRepository.findAll();

        campaigns.forEach(campaign -> {
            CampaignStatus status = campaignService.determineStatus(
                    campaign.getStartDate(),
                    campaign.getEndDate()
            );
            campaign.setCampaignStatus(status.name());
        });

        campaignRepository.saveAll(campaigns);
    }
}
