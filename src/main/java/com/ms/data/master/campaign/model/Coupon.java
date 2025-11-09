package com.ms.data.master.campaign.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ms.data.master.campaign.model.dto.campaign.CustomerCouponRedeemerDTO;
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
@Table(name = "t_coupon", schema = "ms-data-master-campaign-svc")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Coupon {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(name = "coupon_code")
    private String couponCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "product_details")
    private List<ProductDetailsDTO> productDetails;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "customer_coupon_redeemer_details")
    private CustomerCouponRedeemerDTO customerCouponRedeemerDetails;

    @Column(name = "coupon_status")
    private String couponStatus;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
