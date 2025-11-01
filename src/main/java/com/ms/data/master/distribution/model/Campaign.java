package com.ms.data.master.distribution.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ms.data.master.distribution.model.dto.campaign.ProductDetailsDTO;
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
@Table(name = "t_campaign", schema = "ms-data-master-campaign-svc")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Campaign {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "product_details")
    private List<ProductDetailsDTO> productDetails;

    @Column(name = "title_id_lang")
    private String titleIdLanguage;

    @Column(name = "title_en_lang")
    private String titleEnLanguage;

    @Column(name = "dscp_id_lang")
    private String descriptionIdLanguage;

    @Column(name = "dscp_en_lang")
    private String descriptionEnLanguage;

    @Column(name = "sub_dscp_id_lang")
    private String subDescriptionIdLanguage;

    @Column(name = "sub_dscp_en_lang")
    private String subDescriptionEnLanguage;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "scan_count")
    private Integer scanCount;

    @Column(name = "campaign_status")
    private String campaignStatus;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
