package com.personal.bookmark_backend.dto.response.bookmark;

import java.util.List;
import java.util.Map;

public record BookmarkSearchResponse(
        List<BookmarkResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        Map<String, Long> sourceCounts
) {
}
