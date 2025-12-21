package com.ms.data.master.campaign.controller;

import com.ms.data.master.campaign.exception.AccountExceptionHandler;
import com.ms.data.master.campaign.model.dto.campaign.CampaignDTO;
import com.ms.data.master.campaign.model.dto.campaign.CouponDTO;
import com.ms.data.master.campaign.model.dto.response.PageResponse;
import com.ms.data.master.campaign.service.CampaignService;
import com.ms.data.master.campaign.service.CouponService;
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
@RequestMapping("${endpoint.coupon.base}")
@RequiredArgsConstructor
@Slf4j
public class    CouponController {
    private final CouponService couponService;

    @Value("${common.pageable.size}")
    private Integer pageableSize;

    @Value("${common.pageable.page}")
    private Integer pageablePage;

    @Value("${common.sorting}")
    private String sortingPage;

    @GetMapping(value = "${endpoint.coupon.get-all}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PageResponse<CouponDTO>> getAllCampaign(
            @RequestParam(value = "pageableSize", required = false) Integer defaultPageableSize,
            @RequestParam(value = "pageablePage", required = false) Integer defaultPageablePage,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(value = "ende", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(value = "search", required = false) String search,
            @ModelAttribute CouponDTO couponDTO,
            @SortDefault(sort = "id", direction = Sort.Direction.ASC) Sort sorting) throws AccountExceptionHandler {

        return ResponseEntity.ok(couponService.getAllService(
                Optional.ofNullable(defaultPageableSize).filter(size -> size > 0).orElse(pageableSize),
                Optional.ofNullable(defaultPageablePage).filter(page -> page >= 0).orElse(pageablePage),
                Optional.ofNullable(sorting).orElse(Sort.by(Sort.Direction.fromString(sortingPage), "id")),
                couponDTO,
                startDate,
                endDate,
                search
        ));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CouponDTO> createCoupon(@RequestBody CouponDTO couponDTO) {
        return ResponseEntity.ok(couponService.createService(couponDTO));
    }

    @GetMapping(value = "${endpoint.coupon.get-by-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CouponDTO> getCouponById(@PathVariable UUID id) {
        return ResponseEntity.ok(couponService.getIdService(id));
    }

    @GetMapping(value = "${endpoint.coupon.get-by-coupon-status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CouponDTO> getCouponByOrderRequestStatus(@PathVariable String campaignStatus) {
        return ResponseEntity.ok(couponService.getCampaignStatusService(campaignStatus));
    }

    @DeleteMapping(value = "${endpoint.coupon.delete}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteCoupon(@PathVariable UUID id) {
        couponService.deleteService(id);
        return ResponseEntity.ok().build();
    }
}
