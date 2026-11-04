package com.personal.bookmark_backend.dto.response.tag;

import java.time.LocalDateTime;

public record TagResponse(
        Long id,
        String name,
        LocalDateTime createdAt
) {
}