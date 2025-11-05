package com.ms.data.master.campaign.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "endpoint.campaign-scan-log")
@Getter
@Setter
public class CampaignScanLogEndpointProperties {
    private String base;
    private String getAll;
    private String getById;
    private String update;
    private String delete;
    private String getCountScanByEmail;
}
