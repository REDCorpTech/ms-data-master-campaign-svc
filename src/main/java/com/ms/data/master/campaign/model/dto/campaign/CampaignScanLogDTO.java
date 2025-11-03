package com.ms.data.master.campaign.model.dto.campaign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignScanLogDTO {
    private UUID id;
    private String campaignName;
    private String email;
    private List<ProductDetailsDTO> productDetails;
    private String scanBy;
    private LocalDateTime scanAt;
}
