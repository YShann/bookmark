package com.personal.bookmark_backend.controller;

import com.personal.bookmark_backend.dto.response.tag.TagResponse;
import com.personal.bookmark_backend.entity.Tag;
import com.personal.bookmark_backend.service.TagService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    // =====================================================
    // 共用方法：取得登入者 ID
    // =====================================================

    private Long getUserId(Authentication authentication) {

        return (Long) authentication.getPrincipal();
    }


    // =====================================================
    // 取得指定收藏夾內的所有標籤
    // =====================================================

    @GetMapping("/folders/{folderId}/tags")
    public ResponseEntity<List<TagResponse>> getFolderTags(
            @PathVariable Long folderId,
            Authentication authentication
    ) {

        // 從 JWT 驗證結果取得目前登入的使用者 ID
        Long userId = getUserId(authentication);

        // 查詢收藏夾內的標籤
        List<Tag> tags = tagService.getFolderTags(
                userId,
                folderId
        );

        // 將 Entity 轉換成 Response DTO
        List<TagResponse> response = tags.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }


    // =====================================================
    // Entity -> Response DTO
    // =====================================================

    private TagResponse toResponse(Tag tag) {

        return new TagResponse(
                tag.getId(),
                tag.getName(),
                tag.getCreatedAt()
        );
    }
}