package com.personal.bookmark_backend.mapper;

import com.personal.bookmark_backend.dto.response.bookmark.BookmarkResponse;
import com.personal.bookmark_backend.entity.Bookmark;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookmarkMapperTest {
    private final BookmarkMapper mapper = new BookmarkMapperImpl();

    @Test
    void mapsEveryResponseFieldIncludingTagsProvidedByTheController() {
        var createdAt = LocalDateTime.of(2026, 11, 13, 8, 0);
        var updatedAt = createdAt.plusHours(1);
        var bookmark = new Bookmark();
        bookmark.setId(7L);
        bookmark.setFolderId(2L);
        bookmark.setUrl("https://example.org");
        bookmark.setTitle("標題");
        bookmark.setContent("內文");
        bookmark.setNote(null);
        bookmark.setSource("OTHER");
        bookmark.setImageUrl("https://example.org/image.png");
        bookmark.setCreatedAt(createdAt);
        bookmark.setUpdatedAt(updatedAt);

        var tags = List.of("旅行", "文章");
        var expected = new BookmarkResponse(
                7L, 2L, "https://example.org", "標題", "內文", null, "OTHER",
                "https://example.org/image.png", tags, createdAt, updatedAt
        );

        assertEquals(expected, mapper.toResponse(bookmark, tags));
    }
}
