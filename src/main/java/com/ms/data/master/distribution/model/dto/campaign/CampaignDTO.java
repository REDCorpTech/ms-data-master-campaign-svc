package com.ms.data.master.distribution.model.dto.campaign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignDTO {
    private UUID id;
    private List<ProductDetailsDTO> productDetails;
    private String titleIdLanguage;
    private String titleEnLanguage;
    private String descriptionIdLanguage;
    private String descriptionEnLanguage;
    private String subDescriptionIdLanguage;
    private String subDescriptionEnLanguage;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer scanCount;
    private String campaignStatus;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
