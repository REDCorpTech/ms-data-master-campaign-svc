package com.ms.data.master.campaign.service;

import com.ms.data.master.campaign.model.CampaignScanLog;
import com.ms.data.master.campaign.model.dto.campaign.CampaignScanLogDTO;
import com.ms.data.master.campaign.model.dto.response.PageResponse;
import com.ms.data.master.campaign.model.mapper.CampaignScanLogMapper;
import com.ms.data.master.campaign.respository.CampaignScanLogRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
public class CampaignScanLogService {
    private final CampaignScanLogRepository campaignScanLogRepository;

    public PageResponse<CampaignScanLogDTO> getAllService(Integer pageableSize, Integer pageablePage, Sort sorting,
                                                          CampaignScanLogDTO campaignScanLogDTO,
                                                          String search) {
        return new PageResponse<>(
                getAllFromRepository(pageableSize, pageablePage, sorting, campaignScanLogDTO, search)
                        .getContent()
                        .stream()
                        .map(CampaignScanLogMapper.INSTANCE::toDTO)
                        .collect(Collectors.toList()),
                getAllFromRepository(pageableSize, pageablePage, sorting, campaignScanLogDTO, search).getTotalElements(),
                getAllFromRepository(pageableSize, pageablePage, sorting, campaignScanLogDTO, search).getSize(),
                getAllFromRepository(pageableSize, pageablePage, sorting, campaignScanLogDTO, search).getNumber() + 1
        );
    }

    public CampaignScanLogDTO getIdService(UUID id) {
        return CampaignScanLogMapper.INSTANCE.toDTO(getIdFromRepository(id));
    }

    public void recordScan(CampaignScanLogDTO dto) {
        CampaignScanLog entity = CampaignScanLogMapper.INSTANCE.toEntity(dto);
        campaignScanLogRepository.save(entity);
    }

    @Transactional
    public CampaignScanLogDTO createService(CampaignScanLogDTO campaignScanLogDTO) {
        return CampaignScanLogMapper.INSTANCE.toDTO(
                campaignScanLogRepository.save(
                                CampaignScanLogMapper.INSTANCE.toEntity(campaignScanLogDTO)
                )
        );
    }

    @Transactional
    public CampaignScanLogDTO updateService(UUID id, CampaignScanLogDTO campaignScanLogDTO) {
        return CampaignScanLogMapper.INSTANCE.toDTO(
                campaignScanLogRepository.save(updateEntityFromDTO(getIdFromRepository(id), campaignScanLogDTO))
        );
    }

    public Long getScanCountByEmail(String email) {
        return countScanByEmailFromRepository(email);
    }

    public void deleteService(UUID id) {
        deleteFromRepository(id);
    }

    private Page<CampaignScanLog> getAllFromRepository(Integer pageableSize, Integer pageablePage, Sort sorting,
                                                       CampaignScanLogDTO campaignScanLogDTO,
                                                       String search) {
        return campaignScanLogRepository.findAll(buildSpecification(campaignScanLogDTO, search),
                PageRequest.of(pageablePage, pageableSize, sorting));
    }


    private CampaignScanLog getIdFromRepository(UUID id) {
        return campaignScanLogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Campaign Scan Log not found for id: " + id));
    }

    private void deleteFromRepository(UUID id) {
        campaignScanLogRepository.deleteById(id);
    }

    private CampaignScanLog updateEntityFromDTO(CampaignScanLog existing, CampaignScanLogDTO dto) {
        CampaignScanLogMapper.INSTANCE.updateFromDTOToEntity(dto, existing);
        existing.setScanAt(LocalDateTime.now());
        existing.setScanBy(dto.getScanBy());
        return existing;
    }

    private Long countScanByEmailFromRepository(String email) {
        return campaignScanLogRepository.countByEmail(email);
    }

    private Specification<CampaignScanLog> buildSpecification(
            CampaignScanLogDTO campaignScanLogDTO,
            String search
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // 1️⃣ Basic field filters
            addIfNotNull(predicates, cb, root.get("id"), campaignScanLogDTO.getId());
            addIfNotEmpty(predicates, cb, root.get("scanBy"), campaignScanLogDTO.getScanBy());

            // 4️⃣ Free-text search
            if (search != null && !search.isBlank()) {
                String likePattern = "%" + search.toLowerCase() + "%";
                List<Predicate> searchPredicates = new ArrayList<>();

                // Normal columns
                searchPredicates.add(cb.like(cb.lower(root.get("scanBy")), likePattern));
                searchPredicates.add(cb.like(cb.lower(root.get("campaignName")), likePattern));
                searchPredicates.add(cb.like(cb.lower(root.get("email")), likePattern));

                // JSON column: productDetails.productName (PostgreSQL only)
                searchPredicates.add(jsonbLike(cb, root, "productDetails", "productName", likePattern));

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
