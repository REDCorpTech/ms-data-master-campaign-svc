package com.ms.data.master.campaign.controller;

import com.ms.data.master.campaign.exception.AccountExceptionHandler;
import com.ms.data.master.campaign.model.dto.campaign.CampaignDTO;
import com.ms.data.master.campaign.model.dto.campaign.CampaignScanLogDTO;
import com.ms.data.master.campaign.model.dto.response.PageResponse;
import com.ms.data.master.campaign.service.CampaignScanLogService;
import com.ms.data.master.campaign.service.CampaignService;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("${endpoint.campaign-scan-log.base}")
@RequiredArgsConstructor
@Slf4j
public class CampaignScanLogController {
    private final CampaignScanLogService campaignScanLogService;

    @Value("${common.pageable.size}")
    private Integer pageableSize;

    @Value("${common.pageable.page}")
    private Integer pageablePage;

    @Value("${common.sorting}")
    private String sortingPage;

    @GetMapping(value = "${endpoint.campaign-scan-log.get-all}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PageResponse<CampaignScanLogDTO>> getAllCampaign(
            @RequestParam(value = "pageableSize", required = false) Integer defaultPageableSize,
            @RequestParam(value = "pageablePage", required = false) Integer defaultPageablePage,
            @RequestParam(value = "search", required = false) String search,
            @ModelAttribute CampaignScanLogDTO campaignScanLogDTO,
            @SortDefault(sort = "id", direction = Sort.Direction.ASC) Sort sorting) throws AccountExceptionHandler {

        return ResponseEntity.ok(campaignScanLogService.getAllService(
                Optional.ofNullable(defaultPageableSize).filter(size -> size > 0).orElse(pageableSize),
                Optional.ofNullable(defaultPageablePage).filter(page -> page >= 0).orElse(pageablePage),
                Optional.ofNullable(sorting).orElse(Sort.by(Sort.Direction.fromString(sortingPage), "id")),
                campaignScanLogDTO,
                search
        ));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CampaignScanLogDTO> createCampaign(@RequestBody CampaignScanLogDTO campaignDTO) throws AccountExceptionHandler {
        return ResponseEntity.ok(campaignScanLogService.createService(campaignDTO));
    }

    @GetMapping(value = "${endpoint.campaign-scan-log.get-by-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CampaignScanLogDTO> getCampaignById(@PathVariable UUID id) {
        return ResponseEntity.ok(campaignScanLogService.getIdService(id));
    }

    @GetMapping(value = "${endpoint.campaign-scan-log.get-count-scan-by-email", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getCampaignByOrderRequestStatus(@PathVariable String email) {
        Long count = campaignScanLogService.getScanCountByEmail(email);
        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("totalScans", count);
        return ResponseEntity.ok(response);
    }

    @PutMapping( value = "${endpoint.campaign-scan-log.update}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CampaignScanLogDTO> updateCampaign(@PathVariable UUID id, @RequestBody CampaignScanLogDTO campaignScanLogDTO) {
        return ResponseEntity.ok(campaignScanLogService.updateService(id, campaignScanLogDTO));
    }

    @DeleteMapping(value = "${endpoint.campaign-scan-log.delete}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteCampaign(@PathVariable UUID id) {
            campaignScanLogService.deleteService(id);
            return ResponseEntity.ok().build();
    }


}
