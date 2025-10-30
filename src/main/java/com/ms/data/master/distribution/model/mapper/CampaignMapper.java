package com.ms.data.master.distribution.model.mapper;

import com.ms.data.master.distribution.model.Campaign;
import com.ms.data.master.distribution.model.dto.campaign.CampaignDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CampaignMapper {
    CampaignMapper INSTANCE = Mappers.getMapper(CampaignMapper.class);

    Campaign toEntity(CampaignDTO campaignDTO);

    CampaignDTO toDTO(Campaign campaign);

    void updateFromDTOToEntity(CampaignDTO campaignDTO, @MappingTarget Campaign campaign);
}
