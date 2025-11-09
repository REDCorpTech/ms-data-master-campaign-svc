package com.ms.data.master.campaign.model.dto.campaign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponDTO {
    private UUID id;
    private List<ProductDetailsDTO> productDetails;
    private CustomerCouponRedeemerDTO customerCouponRedeemerDetails;
    private String couponCode;
    private String couponStatus;
    private LocalDateTime createdAt;
    private String createdBy;
}
