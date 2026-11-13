package com.personal.bookmark_backend.controller;

import com.personal.bookmark_backend.mapper.FolderMapper;
import com.personal.bookmark_backend.dto.request.folder.CreateFolderRequest;
import com.personal.bookmark_backend.dto.request.folder.UpdateFolderRequest;
import com.personal.bookmark_backend.dto.response.folder.FolderResponse;
import com.personal.bookmark_backend.dto.response.PageResponse;
import com.personal.bookmark_backend.entity.Folder;
import com.personal.bookmark_backend.service.FolderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;
    private final FolderMapper folderMapper;


    // =====================================================
    // 1. 查詢使用者的所有收藏夾
    // GET /api/folders
    // =====================================================

    @GetMapping
    public ResponseEntity<List<FolderResponse>> getUserFolders(
            Authentication authentication
    ) {

        // 從 JWT 驗證後的 SecurityContext 取得使用者 ID
        Long userId = getUserId(authentication);

        // 呼叫 Service 查詢收藏夾
        List<Folder> folders = folderService.getUserFolders(userId);
        Map<Long, Long> counts = folderService.countBookmarks(userId, folders);

        // 將 Entity 轉換成 Response DTO
        List<FolderResponse> response = folders.stream()
                .map(folder -> folderMapper.toResponse(folder, counts.getOrDefault(folder.getId(), 0L)))
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/page")
    public PageResponse<FolderResponse> getFolderPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        var folders = folderService.getFolderPage(
                userId,
                PageRequest.of(Math.max(0, page), Math.max(1, Math.min(size, 50)))
        );
        Map<Long, Long> counts = folderService.countBookmarks(userId, folders.getContent());
        return PageResponse.from(folders.map(folder -> folderMapper.toResponse(
                folder,
                counts.getOrDefault(folder.getId(), 0L)
        )));
    }

    @GetMapping("/{folderId}")
    public FolderResponse getFolder(
            @PathVariable Long folderId,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        Folder folder = folderService.getFolder(userId, folderId);
        return folderMapper.toResponse(
                folder,
                folderService.countBookmarks(userId, List.of(folder)).getOrDefault(folderId, 0L)
        );
    }


    // =====================================================
    // 2. 建立收藏夾
    // POST /api/folders
    // =====================================================

    @PostMapping
    public ResponseEntity<FolderResponse> createFolder(
            @Valid @RequestBody CreateFolderRequest request,
            Authentication authentication
    ) {

        Long userId = getUserId(authentication);

        // 建立收藏夾
        Folder folder = folderService.createFolder(
                userId,
                request.name()
        );

        // 轉換成 Response DTO
        FolderResponse response = folderMapper.toResponse(folder, 0);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =====================================================
    // 3. 修改收藏夾名稱
    // PATCH /api/folders/{folderId}
    // =====================================================

    @PatchMapping("/{folderId}")
    public ResponseEntity<FolderResponse> renameFolder(
            @PathVariable Long folderId,
            @Valid @RequestBody UpdateFolderRequest request,
            Authentication authentication
    ) {

        Long userId = getUserId(authentication);

        // 修改收藏夾名稱
        Folder folder = folderService.renameFolder(
                userId,
                folderId,
                request.name()
        );

        // 轉換成 Response DTO
        long count = folderService.countBookmarks(userId, List.of(folder)).getOrDefault(folderId, 0L);
        FolderResponse response = folderMapper.toResponse(folder, count);

        return ResponseEntity.ok(response);
    }


    // =====================================================
    // 4. 刪除收藏夾
    // DELETE /api/folders/{folderId}
    // =====================================================

    @DeleteMapping("/{folderId}")
    public ResponseEntity<Void> deleteFolder(
            @PathVariable Long folderId,
            @RequestParam(defaultValue = "false") boolean deleteBookmarks,
            Authentication authentication
    ) {

        Long userId = getUserId(authentication);

        // 依照選項刪除收藏夾，並決定保留或一併刪除其中的收藏
        folderService.deleteFolder(userId, folderId, deleteBookmarks);

        // 刪除成功回傳 HTTP 204
        return ResponseEntity.noContent().build();
    }


    // =====================================================
    // 共用方法：取得登入者 ID
    // =====================================================

    private Long getUserId(Authentication authentication) {

        return (Long) authentication.getPrincipal();
    }
}
