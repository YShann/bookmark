package com.personal.bookmark_backend.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(List<T> content, int page, int totalPages, long totalElements) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return from(page, page.getContent());
    }

    public static <T> PageResponse<T> from(Page<?> page, List<T> content) {
        return new PageResponse<>(content, page.getNumber(), page.getTotalPages(), page.getTotalElements());
    }
}
