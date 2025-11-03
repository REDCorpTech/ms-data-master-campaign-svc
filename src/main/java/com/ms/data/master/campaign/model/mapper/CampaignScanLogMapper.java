package com.ms.data.master.campaign.model.mapper;

import com.ms.data.master.campaign.model.Campaign;
import com.ms.data.master.campaign.model.CampaignScanLog;
import com.ms.data.master.campaign.model.dto.campaign.CampaignDTO;
import com.ms.data.master.campaign.model.dto.campaign.CampaignScanLogDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CampaignScanLogMapper {
    CampaignScanLogMapper INSTANCE = Mappers.getMapper(CampaignScanLogMapper.class);

    CampaignScanLog toEntity(CampaignScanLogDTO campaignScanLogDTO);

    CampaignScanLogDTO toDTO(CampaignScanLog campaignScanLog);

    void updateFromDTOToEntity(CampaignScanLogDTO campaignScanLogDTO, @MappingTarget CampaignScanLog campaignScanLog);
}
