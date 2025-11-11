package com.ms.data.master.campaign.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "endpoint.coupon")
@Getter
@Setter
public class CouponEndpointProperties {
    private String base;
    private String getAll;
    private String getById;
    private String getByCouponStatus;
    private String delete;
}
