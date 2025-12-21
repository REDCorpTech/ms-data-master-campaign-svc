package com.ms.data.master.campaign.service;

import com.ms.data.master.campaign.lib.ClaimCalculationLib;
import com.ms.data.master.campaign.model.Campaign;
import com.ms.data.master.campaign.model.Coupon;
import com.ms.data.master.campaign.model.dto.campaign.CampaignDTO;
import com.ms.data.master.campaign.model.dto.campaign.CouponDTO;
import com.ms.data.master.campaign.model.dto.response.PageResponse;
import com.ms.data.master.campaign.model.mapper.CampaignMapper;
import com.ms.data.master.campaign.model.mapper.CouponMapper;
import com.ms.data.master.campaign.respository.CampaignRepository;
import com.ms.data.master.campaign.respository.CouponRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponRepository couponRepository;
    private final ClaimCalculationLib claimCalculationLib;
    private final Random random = new Random();

    public PageResponse<CouponDTO> getAllService(Integer pageableSize, Integer pageablePage, Sort sorting,
                                                 CouponDTO couponDTO,
                                                 LocalDateTime startDate, LocalDateTime endDate, String search) {
        return new PageResponse<>(
                getAllFromRepository(pageableSize, pageablePage, sorting, couponDTO, startDate, endDate, search)
                        .getContent()
                        .stream()
                        .map(CouponMapper.INSTANCE::toDTO)
                        .collect(Collectors.toList()),
                getAllFromRepository(pageableSize, pageablePage, sorting, couponDTO, startDate, endDate, search).getTotalElements(),
                getAllFromRepository(pageableSize, pageablePage, sorting, couponDTO, startDate, endDate, search).getSize(),
                getAllFromRepository(pageableSize, pageablePage, sorting, couponDTO, startDate, endDate, search).getNumber() + 1
        );
    }

    public CouponDTO getIdService(UUID id) {
        return CouponMapper.INSTANCE.toDTO(getIdFromRepository(id));
    }

    public CouponDTO getCampaignStatusService(String orderRequestStatus) {
        return CouponMapper.INSTANCE.toDTO(getCouponStatusFromRepository(orderRequestStatus));
    }

    @Transactional
    public CouponDTO createService(CouponDTO couponDTO) {
        return CouponMapper.INSTANCE.toDTO(
                Optional.of(couponRepository.save(CouponMapper.INSTANCE.toEntity(
                                Optional.of(couponDTO)
                                        .filter(dto -> dto.getCouponCode() != null && !dto.getCouponCode().isBlank())
                                        .orElseGet(() -> {
                                            couponDTO.setCouponCode(generateUniqueCouponCode());
                                            return couponDTO;
                                        })
                        )))
                        .map(saved -> {
                            claimCalculationLib.processClaimForCustomer(couponDTO.getCustomerCouponRedeemerDetails().getCustomerEmail());
                            return saved;
                        })
                        .get()
        );
    }





    public void deleteService(UUID id) {
        deleteFromRepository(id);
    }

    private Page<Coupon> getAllFromRepository(Integer pageableSize, Integer pageablePage, Sort sorting,
                                              CouponDTO couponDTO,
                                              LocalDateTime startDate, LocalDateTime endDate, String search) {
        return couponRepository.findAll(buildSpecification(couponDTO, startDate, endDate, search),
                PageRequest.of(pageablePage, pageableSize, sorting));
    }


    private Coupon getIdFromRepository(UUID id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found for id: " + id));
    }

    private Coupon getCouponStatusFromRepository(String couponStatus) {
        return couponRepository.findAllByCouponStatus(couponStatus).stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found for campaign status: " + couponStatus));
    }

    private void deleteFromRepository(UUID id) {
        couponRepository.deleteById(id);
    }

    private String generateUniqueCouponCode() {
        String code;
        do {
            code = randomLetters(2) + String.format("%03d", random.nextInt(1000)) + randomLetters(2);
        } while (couponRepository.existsByCouponCode(code)); // ensure no duplicates
        return code;
    }

    private String randomLetters(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            char letter = (char) ('A' + random.nextInt(26)); // only uppercase
            sb.append(letter);
        }
        return sb.toString();
    }




    private Specification<Coupon> buildSpecification(
                    CouponDTO couponDTO,
                    LocalDateTime startDate,
                    LocalDateTime endDate,
                    String search
            ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // 1️⃣ Basic field filters
            addIfNotNull(predicates, cb, root.get("id"), couponDTO.getId());
            addIfNotEmpty(predicates, cb, root.get("createdBy"), couponDTO.getCreatedBy());
            addIfNotEmpty(predicates, cb, root.get("couponStatus"), couponDTO.getCouponStatus());
            // ✅ Handle nested customerEmail inside JSONB
            if (couponDTO.getCustomerCouponRedeemerDetails() != null &&
                    couponDTO.getCustomerCouponRedeemerDetails().getCustomerEmail() != null &&
                    !couponDTO.getCustomerCouponRedeemerDetails().getCustomerEmail().isBlank()) {

                String email = couponDTO.getCustomerCouponRedeemerDetails().getCustomerEmail().toLowerCase();

                // PostgreSQL JSONB path filter
                Expression<String> customerEmailExpr = cb.function(
                        "jsonb_extract_path_text",
                        String.class,
                        root.get("customerCouponRedeemerDetails"),
                        cb.literal("customerEmail")
                );

                predicates.add(cb.equal(cb.lower(customerEmailExpr), email));
            }

            // 4️⃣ Free-text search
            if (search != null && !search.isBlank()) {
                String likePattern = "%" + search.toLowerCase() + "%";
                List<Predicate> searchPredicates = new ArrayList<>();

                // Normal columns
                searchPredicates.add(cb.like(cb.lower(root.get("createdBy")), likePattern));
                searchPredicates.add(cb.like(cb.lower(root.get("couponStatus")), likePattern));

                // JSON columns
                searchPredicates.add(jsonbLike(cb, root, "productDetails", "productName", likePattern));
                searchPredicates.add(jsonbLike(cb, root, "customerCouponRedeemerDetails", "customerEmail", likePattern));

                predicates.add(cb.or(searchPredicates.toArray(new Predicate[0])));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    //
    // 🔧 Helper methods — add these inside your CampaignService
    //
    private <T> void addIfNotNull(List<Predicate> predicates, CriteriaBuilder cb, Path<T> path, T value) {
        if (value != null) {
            predicates.add(cb.equal(path, value));
        }
    }

    private void addIfNotEmpty(List<Predicate> predicates, CriteriaBuilder cb, Path<String> path, String value) {
        if (value != null && !value.isEmpty()) {
            predicates.add(cb.equal(cb.lower(path), value.toLowerCase()));
        }
    }

    /**
     * Extracts a JSONB field from PostgreSQL using jsonb_extract_path_text and compares with LIKE.
     * Requires PostgreSQL dialect support.
     */
    private Predicate jsonbLike(CriteriaBuilder cb, Root<?> root, String jsonField, String key, String pattern) {
        return cb.like(
                cb.lower(
                        cb.function(
                                "jsonb_extract_path_text",
                                String.class,
                                root.get(jsonField),
                                cb.literal(key)
                        )
                ),
                pattern
        );
    }


}
