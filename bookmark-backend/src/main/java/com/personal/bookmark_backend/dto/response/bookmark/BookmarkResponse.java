package com.personal.bookmark_backend.dto.response.bookmark;

import java.time.LocalDateTime;
import java.util.List;

public record BookmarkResponse(

        Long id,

        Long folderId,

        String url,

        String title,

        String content,

        String note,

        String source,

        String imageUrl,

        List<String> tags,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}
