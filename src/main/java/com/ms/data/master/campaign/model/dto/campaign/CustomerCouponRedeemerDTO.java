package com.ms.data.master.campaign.model.dto.campaign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCouponRedeemerDTO {
    private String id;
    private String customerName;
    private String customerEmail;
    private LocalDate customerBirthOfDate;
    private String customerWhatsappNumber;
    private String customerGender;
}
