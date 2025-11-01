package com.ms.data.master.campaign.model.dto.campaign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailsDTO {
    private UUID id;
    private String productName;
}
