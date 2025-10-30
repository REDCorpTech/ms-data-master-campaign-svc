package com.ms.data.master.distribution.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> elements;
    private Long totalElements;
    private Integer totalPages;
    private Integer currentPage;
}