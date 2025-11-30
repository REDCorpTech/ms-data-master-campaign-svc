package com.ms.data.master.campaign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MsDataMasterCampaignApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsDataMasterCampaignApplication.class, args);
	}

}