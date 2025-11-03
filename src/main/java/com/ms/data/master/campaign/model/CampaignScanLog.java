package com.ms.data.master.campaign.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ms.data.master.campaign.model.dto.campaign.ProductDetailsDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "t_campaign_scan_log", schema = "ms-data-master-campaign-svc")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignScanLog {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(name = "email")
    private String email;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "product_details")
    private List<ProductDetailsDTO> productDetails;

    @Column(name = "campaign_name")
    private String campaignName;

    @Column(name = "scan_at")
    private LocalDateTime scanAt;

    @Column(name = "scan_by")
    private String scanBy;

}
