package com.ms.data.master.campaign.service;

import com.ms.data.master.campaign.config.JwtUtil;
import com.ms.data.master.campaign.model.*;
import com.ms.data.master.campaign.model.dto.campaign.CampaignDTO;
import com.ms.data.master.campaign.model.dto.campaign.CampaignStatus;
import com.ms.data.master.campaign.model.dto.response.PageResponse;
import com.ms.data.master.campaign.model.mapper.*;
import com.ms.data.master.campaign.respository.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CampaignService {
    private final CampaignRepository campaignRepository;
    private final JwtUtil jwtUtil;
    private CampaignStatus campaignStatus;

    public PageResponse<CampaignDTO> getAllService(
            String token,
            Integer pageableSize,
            Integer pageablePage,
            Sort sorting,
            CampaignDTO campaignDTO,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String search) {

        // 1️⃣ Ambil brandId dari token
        String brandId = jwtUtil.getSub(token); // sub = brandId

        // 2️⃣ Panggil satu kali saja
        Page<Campaign> page = getAllFromRepository(
                pageableSize,
                pageablePage,
                sorting,
                campaignDTO,
                startDate,
                endDate,
                search,
                brandId
        );

        // 3️⃣ Return response
        return new PageResponse<>(
                page.getContent()
                        .stream()
                        .map(CampaignMapper.INSTANCE::toDTO)
                        .collect(Collectors.toList()),
                page.getTotalElements(),
                page.getSize(),
                page.getNumber() + 1
        );
    }


    public CampaignDTO getIdService(UUID id) {
        return CampaignMapper.INSTANCE.toDTO(getIdFromRepository(id));
    }

    public CampaignDTO getCampaignStatusService(String orderRequestStatus) {
        return CampaignMapper.INSTANCE.toDTO(getCampaignStatusFromRepository(orderRequestStatus));
    }

    @Transactional
    public CampaignDTO createService(CampaignDTO campaignDTO) {

        Campaign entity = CampaignMapper.INSTANCE.toEntity(campaignDTO);

        entity.setCampaignStatus(
                determineStatus(campaignDTO.getStartDate(), campaignDTO.getEndDate()).name()
        );

        return CampaignMapper.INSTANCE.toDTO(campaignRepository.save(entity));
    }


    @Transactional
    public CampaignDTO updateService(UUID id, CampaignDTO campaignDTO) {
        return CampaignMapper.INSTANCE.toDTO(
                campaignRepository.save(updateEntityFromDTO(getIdFromRepository(id), campaignDTO))
        );
    }

    public void deleteService(UUID id) {
        deleteFromRepository(id);
    }

    private Page<Campaign> getAllFromRepository(Integer pageableSize, Integer pageablePage, Sort sorting,
                                                CampaignDTO campaignDTO, LocalDateTime startDate, LocalDateTime endDate, String search, String brandId) {

        return campaignRepository.findAll(
                buildSpecification(campaignDTO, startDate, endDate, search, brandId),
                PageRequest.of(pageablePage, pageableSize, sorting)
        );
    }



    private Campaign getIdFromRepository(UUID id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order Request not found for id: " + id));
    }

    private Campaign getCampaignStatusFromRepository(String campaignStatus) {
        return campaignRepository.findAllByCampaignStatus(campaignStatus).stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Campaign not found for campaign status: " + campaignStatus));
    }

    private void deleteFromRepository(UUID id) {
        campaignRepository.deleteById(id);
    }

    private Campaign updateEntityFromDTO(Campaign existing, CampaignDTO dto) {
        CampaignMapper.INSTANCE.updateFromDTOToEntity(dto, existing);
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setUpdatedBy(dto.getUpdatedBy());
        return existing;
    }

    public CampaignStatus determineStatus(LocalDateTime start, LocalDateTime end) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(start)) {
            return CampaignStatus.NOT_STARTED;
        } else if (now.isAfter(end)) {
            return CampaignStatus.ENDED;
        } else {
            return CampaignStatus.ONGOING;
        }
    }

    private Specification<Campaign> buildSpecification(
            CampaignDTO campaignDTO,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String search,
            String brandId
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // 1️⃣ Basic field filters
            addIfNotNull(predicates, cb, root.get("id"), campaignDTO.getId());
            addIfNotEmpty(predicates, cb, root.get("createdBy"), campaignDTO.getCreatedBy());
            addIfNotEmpty(predicates, cb, root.get("campaignStatus"), campaignDTO.getCampaignStatus());

            // 2️⃣ Campaign start/end date (from DTO)
            if (campaignDTO.getStartDate() != null && campaignDTO.getEndDate() != null) {
                predicates.add(cb.between(root.get("startDate"), campaignDTO.getStartDate(), campaignDTO.getEndDate()));
            } else if (campaignDTO.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), campaignDTO.getStartDate()));
            } else if (campaignDTO.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("endDate"), campaignDTO.getEndDate()));
            }

            // 3️⃣ CreatedAt range (external params)
            if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get("createdAt"), startDate, endDate));
            } else if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            } else if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }

            // 4️⃣ Free-text search
            if (search != null && !search.isBlank()) {
                String likePattern = "%" + search.toLowerCase() + "%";
                List<Predicate> searchPredicates = new ArrayList<>();

                // Normal columns
                searchPredicates.add(cb.like(cb.lower(root.get("createdBy")), likePattern));
                searchPredicates.add(cb.like(cb.lower(root.get("campaignStatus")), likePattern));
                searchPredicates.add(cb.like(cb.lower(root.get("titleIdLanguage")), likePattern));
                searchPredicates.add(cb.like(cb.lower(root.get("titleEnLanguage")), likePattern));
                searchPredicates.add(cb.like(cb.lower(root.get("descriptionIdLanguage")), likePattern));
                searchPredicates.add(cb.like(cb.lower(root.get("descriptionEnLanguage")), likePattern));

                // JSON column: productDetails.productName (PostgreSQL only)
                searchPredicates.add(jsonbLike(cb, root, "productDetails", "productName", likePattern));

                predicates.add(cb.or(searchPredicates.toArray(new Predicate[0])));
            }

            if (brandId != null) {
                Subquery<Integer> sub = query.subquery(Integer.class);
                Root<Products> productRoot = sub.from(Products.class);

                Expression<String> productIdJson = cb.function(
                        "jsonb_array_elements_text",
                        String.class,
                        root.get("productDetails")
                );

                sub.select(cb.literal(1))
                        .where(
                                cb.equal(productRoot.get("id"), productIdJson),
                                cb.equal(productRoot.get("brandId"), brandId)
                        );

                predicates.add(cb.exists(sub));
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
