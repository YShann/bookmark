package com.personal.bookmark_backend.controller;

import com.personal.bookmark_backend.dto.request.bookmark.CreateBookmarkRequest;
import com.personal.bookmark_backend.dto.request.bookmark.MoveBookmarkRequest;
import com.personal.bookmark_backend.dto.request.bookmark.UpdateBookmarkRequest;
import com.personal.bookmark_backend.dto.response.bookmark.BookmarkResponse;
import com.personal.bookmark_backend.dto.response.bookmark.BookmarkSearchResponse;
import com.personal.bookmark_backend.dto.response.bookmark.DailyPicksResponse;
import com.personal.bookmark_backend.entity.Bookmark;
import com.personal.bookmark_backend.service.BookmarkService;
import com.personal.bookmark_backend.service.TagService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final TagService tagService;

    // =====================================================
    // 共用方法：取得登入者 ID
    // =====================================================

    private Long getUserId(Authentication authentication) {

        return (Long) authentication.getPrincipal();
    }


    // =====================================================
    // 1. 查詢收藏夾內的所有收藏
    // =====================================================

    @GetMapping("/folders/{folderId}/bookmarks")
    public ResponseEntity<List<BookmarkResponse>> getFolderBookmarks(
            @PathVariable Long folderId,
            Authentication authentication
    ) {

        // 從登入狀態取得目前使用者 ID
        Long userId = getUserId(authentication);

        // 查詢收藏夾內的所有收藏
        List<Bookmark> bookmarks =
                bookmarkService.getFolderBookmarks(userId, folderId);

        // 取得所有收藏 ID
        List<Long> bookmarkIds = bookmarks.stream()
                .map(Bookmark::getId)
                .toList();

        // 一次查詢所有收藏的標籤名稱
        Map<Long, List<String>> tagNamesMap =
                tagService.getBookmarkTagNames(
                        userId,
                        folderId,
                        bookmarkIds
                );

        // 將 Entity 轉換成 Response DTO
        List<BookmarkResponse> response = bookmarks.stream()
                .map(bookmark -> toResponse(
                        bookmark,
                        tagNamesMap.getOrDefault(
                                bookmark.getId(),
                                List.of()
                        )
                ))
                .toList();

        return ResponseEntity.ok(response);
    }


    // =====================================================
    // 搜尋收藏（分頁）
    // =====================================================

    @GetMapping("/bookmarks/daily-picks")
    public ResponseEntity<DailyPicksResponse> getDailyPicks(
            @RequestParam(defaultValue = "0") int rotation,
            @RequestParam(required = false) List<Long> excludeIds,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        List<BookmarkResponse> picks = bookmarkService
                .getDailyPicks(userId, Math.max(0, rotation), excludeIds)
                .stream()
                .map(bookmark -> toBookmarkResponse(userId, bookmark))
                .toList();

        return ResponseEntity.ok(new DailyPicksResponse(
                bookmarkService.countUserBookmarks(userId),
                picks
        ));
    }

    @GetMapping("/bookmarks/search")
    public ResponseEntity<BookmarkSearchResponse> searchBookmarks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String source,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        String normalizedQuery = query == null || query.isBlank() ? null : query.trim();
        String normalizedSource = source == null || source.isBlank() ? null : source.trim();
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, Math.min(size, 50));

        Page<Bookmark> results = bookmarkService.searchBookmarks(
                userId,
                normalizedQuery,
                normalizedSource,
                PageRequest.of(safePage, safeSize)
        );

        Map<Long, List<Long>> idsByFolder = results.getContent().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Bookmark::getFolderId,
                        java.util.stream.Collectors.mapping(Bookmark::getId, java.util.stream.Collectors.toList())
                ));
        Map<Long, Map<Long, List<String>>> tagsByFolder = new java.util.HashMap<>();
        idsByFolder.forEach((folderId, ids) -> tagsByFolder.put(
                folderId,
                tagService.getBookmarkTagNames(userId, folderId, ids)
        ));

        List<BookmarkResponse> content = results.getContent().stream()
                .map(bookmark -> toResponse(
                        bookmark,
                        tagsByFolder.getOrDefault(bookmark.getFolderId(), Map.of())
                                .getOrDefault(bookmark.getId(), List.of())
                ))
                .toList();

        Map<String, Long> sourceCounts = bookmarkService.countSearchResultsBySource(userId, normalizedQuery);
        long webCount = sourceCounts.getOrDefault("WEB", 0L) + sourceCounts.getOrDefault("OTHER", 0L);
        sourceCounts.remove("OTHER");
        if (webCount > 0) sourceCounts.put("WEB", webCount);

        return ResponseEntity.ok(new BookmarkSearchResponse(
                content,
                results.getNumber(),
                results.getSize(),
                results.getTotalElements(),
                results.getTotalPages(),
                sourceCounts
        ));
    }

    // 查詢單筆收藏
    @GetMapping("/bookmarks/{bookmarkId}")
    public ResponseEntity<BookmarkResponse> getBookmark(
            @PathVariable Long bookmarkId,
            Authentication authentication
    ) {

        Long userId = getUserId(authentication);

        // 查詢指定收藏
        Bookmark bookmark =
                bookmarkService.getBookmark(userId, bookmarkId);

        // 查詢標籤並轉換成 Response DTO
        return ResponseEntity.ok(
                toBookmarkResponse(userId, bookmark)
        );
    }


    // =====================================================
    // 3. 新增收藏
    // =====================================================

    @PostMapping("/folders/{folderId}/bookmarks")
    public ResponseEntity<BookmarkResponse> createBookmark(
            @PathVariable Long folderId,
            @Valid @RequestBody CreateBookmarkRequest request,
            Authentication authentication
    ) {

        Long userId = getUserId(authentication);

        // 建立收藏
        Bookmark bookmark = bookmarkService.createBookmark(
                userId,
                folderId,
                request.url(),
                request.title(),
                request.content(),
                request.note(),
                request.source(),
                request.imageUrl(),
                request.tags()
        );

        BookmarkResponse response = toBookmarkResponse(
                userId,
                bookmark
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =====================================================
    // 4. 修改收藏
    // =====================================================

    @PatchMapping("/bookmarks/{bookmarkId}")
    public ResponseEntity<BookmarkResponse> updateBookmark(
            @PathVariable Long bookmarkId,
            @Valid @RequestBody UpdateBookmarkRequest request,
            Authentication authentication
    ) {

        Long userId = getUserId(authentication);

        // 修改收藏內容及標籤
        Bookmark bookmark = bookmarkService.updateBookmark(
                userId,
                bookmarkId,
                request.url(),
                request.title(),
                request.content(),
                request.note(),
                request.source(),
                request.imageUrl(),
                request.tags()
        );

        return ResponseEntity.ok(
                toBookmarkResponse(userId, bookmark)
        );
    }


    // =====================================================
    // 5. 刪除收藏
    // =====================================================

    @DeleteMapping("/bookmarks/{bookmarkId}")
    public ResponseEntity<Void> deleteBookmark(
            @PathVariable Long bookmarkId,
            Authentication authentication
    ) {

        Long userId = getUserId(authentication);

        bookmarkService.deleteBookmark(userId, bookmarkId);

        return ResponseEntity.noContent().build();
    }


    // =====================================================
    // 6. 搬移收藏到其他收藏夾
    // =====================================================

    @PatchMapping("/bookmarks/{bookmarkId}/move")
    public ResponseEntity<BookmarkResponse> moveBookmark(
            @PathVariable Long bookmarkId,
            @Valid @RequestBody MoveBookmarkRequest request,
            Authentication authentication
    ) {

        Long userId = getUserId(authentication);

        // 搬移收藏
        Bookmark bookmark = bookmarkService.moveBookmark(
                userId,
                bookmarkId,
                request.targetFolderId()
        );

        return ResponseEntity.ok(
                toBookmarkResponse(userId, bookmark)
        );
    }


    // =====================================================
    // 共用方法：查詢收藏標籤並轉換成 Response DTO
    // =====================================================

    private BookmarkResponse toBookmarkResponse(
            Long userId,
            Bookmark bookmark
    ) {

        // 查詢這筆收藏的標籤名稱
        Map<Long, List<String>> tagNamesMap =
                tagService.getBookmarkTagNames(
                        userId,
                        bookmark.getFolderId(),
                        List.of(bookmark.getId())
                );

        // 取得標籤名稱
        // 根據當前bookmark的 ID 找出對應的標籤清單；如果沒有任何標籤（在 Map 裡找不到對應的 ID），就回傳『空的清單（List.of()）』
        List<String> tagNames = tagNamesMap.getOrDefault(
                bookmark.getId(),
                List.of()
        );

        // Entity -> Response DTO
        return toResponse(bookmark, tagNames);
    }


    // =====================================================
    // 共用方法：Bookmark Entity 轉換成 Response DTO
    // =====================================================

    private BookmarkResponse toResponse(
            Bookmark bookmark,
            List<String> tagNames
    ) {

        return new BookmarkResponse(
                bookmark.getId(),
                bookmark.getFolderId(),
                bookmark.getUrl(),
                bookmark.getTitle(),
                bookmark.getContent(),
                bookmark.getNote(),
                bookmark.getSource(),
                bookmark.getImageUrl(),
                tagNames,
                bookmark.getCreatedAt(),
                bookmark.getUpdatedAt()
        );
    }
}
