package com.personal.bookmark_backend.service;

import com.personal.bookmark_backend.dto.response.bookmark.BookmarkResponse;
import com.personal.bookmark_backend.entity.Bookmark;
import com.personal.bookmark_backend.mapper.BookmarkMapperImpl;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class BookmarkResponseAssemblerTest {
    @Test
    void groupsTagQueriesByFolderAndPreservesBookmarkAndTagOrder() {
        TagService tags = mock(TagService.class);
        var assembler = new BookmarkResponseAssembler(tags, new BookmarkMapperImpl());
        when(tags.getBookmarkTagNames(1L, 10L, List.of(3L, 1L)))
                .thenReturn(Map.of(3L, List.of("旅行", "文章")));
        when(tags.getBookmarkTagNames(1L, 20L, List.of(2L)))
                .thenReturn(Map.of(2L, List.of("工作")));

        var result = assembler.toResponses(
                1L, List.of(
                        bookmark(3L, 10L), bookmark(2L, 20L), bookmark(1L, 10L)
                )
        );

        assertEquals(List.of(3L, 2L, 1L), result.stream().map(BookmarkResponse::id).toList());
        assertEquals(
                List.of(List.of("旅行", "文章"), List.of("工作"), List.of()),
                result.stream().map(BookmarkResponse::tags).toList()
        );
        verify(tags).getBookmarkTagNames(1L, 10L, List.of(3L, 1L));
        verify(tags).getBookmarkTagNames(1L, 20L, List.of(2L));
        verifyNoMoreInteractions(tags);
    }

    private Bookmark bookmark(long id, long folderId) {
        var bookmark = new Bookmark();
        bookmark.setId(id);
        bookmark.setFolderId(folderId);
        return bookmark;
    }
}
