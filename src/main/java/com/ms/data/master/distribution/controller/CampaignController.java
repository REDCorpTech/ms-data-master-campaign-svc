package com.ms.data.master.distribution.controller;

import com.ms.data.master.distribution.exception.AccountExceptionHandler;
import com.ms.data.master.distribution.model.dto.campaign.CampaignDTO;
import com.ms.data.master.distribution.model.dto.response.PageResponse;
import com.ms.data.master.distribution.service.CampaignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("${endpoint.campaign.base}")
@RequiredArgsConstructor
@Slf4j
public class CampaignController {
    private final CampaignService campaignService;

    @Value("${common.pageable.size}")
    private Integer pageableSize;

    @Value("${common.pageable.page}")
    private Integer pageablePage;

    @Value("${common.sorting}")
    private String sortingPage;

    @GetMapping(value = "${endpoint.campaign.get-all}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PageResponse<CampaignDTO>> getAllCampaign(
            @RequestParam(value = "pageableSize", required = false) Integer defaultPageableSize,
            @RequestParam(value = "pageablePage", required = false) Integer defaultPageablePage,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(value = "ende", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(value = "search", required = false) String search,
            @ModelAttribute CampaignDTO campaignDTO,
            @SortDefault(sort = "id", direction = Sort.Direction.ASC) Sort sorting) throws AccountExceptionHandler {

        return ResponseEntity.ok(campaignService.getAllService(
                Optional.ofNullable(defaultPageableSize).filter(size -> size > 0).orElse(pageableSize),
                Optional.ofNullable(defaultPageablePage).filter(page -> page >= 0).orElse(pageablePage),
                Optional.ofNullable(sorting).orElse(Sort.by(Sort.Direction.fromString(sortingPage), "id")),
                campaignDTO,
                startDate,
                endDate,
                search
        ));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CampaignDTO> createCampaign(@RequestBody CampaignDTO campaignDTO) {
        return ResponseEntity.ok(campaignService.createService(campaignDTO));
    }

    @GetMapping(value = "${endpoint.campaign.get-by-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CampaignDTO> getCampaignById(@PathVariable UUID id) {
        return ResponseEntity.ok(campaignService.getIdService(id));
    }

    @GetMapping(value = "${endpoint.campaign.get-by-campaign-status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CampaignDTO> getCampaignByOrderRequestStatus(@PathVariable String campaignStatus) {
        return ResponseEntity.ok(campaignService.getCampaignStatusService(campaignStatus));
    }


    @PutMapping( value = "${endpoint.campaign.update}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CampaignDTO> updateCampaign(@PathVariable UUID id, @RequestBody CampaignDTO campaignDTO) {
        return ResponseEntity.ok(campaignService.updateService(id, campaignDTO));
    }

    @DeleteMapping(value = "${endpoint.campaign.delete}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteCampaign(@PathVariable UUID id) {
            campaignService.deleteService(id);
            return ResponseEntity.ok().build();
    }


}
