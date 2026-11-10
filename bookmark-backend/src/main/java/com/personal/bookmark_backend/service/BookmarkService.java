package com.personal.bookmark_backend.service;

import com.personal.bookmark_backend.entity.Bookmark;
import com.personal.bookmark_backend.entity.BookmarkTag;
import com.personal.bookmark_backend.entity.Folder;
import com.personal.bookmark_backend.exception.ResourceNotFoundException;
import com.personal.bookmark_backend.repository.BookmarkRepository;
import com.personal.bookmark_backend.repository.BookmarkTagRepository;
import com.personal.bookmark_backend.repository.FolderRepository;
import com.personal.bookmark_backend.repository.TagRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final FolderRepository folderRepository;
    private final BookmarkTagRepository bookmarkTagRepository;
    private final TagRepository tagRepository;

    // 標籤操作交給 TagService 負責
    private final TagService tagService;
    private final DailyPickSelectionService dailyPickSelectionService;

    @Transactional(readOnly = true)
    public long countUserBookmarks(Long userId) {
        return bookmarkRepository.countByUserId(userId);
    }

    @Transactional
    public List<Bookmark> getDailyPicks(Long userId, int rotation, List<Long> excludeIds) {
        return dailyPickSelectionService.getPicks(userId, rotation, excludeIds);
    }

    @Transactional(readOnly = true)
    public Page<Bookmark> searchBookmarks(Long userId, String query, String source, Pageable pageable) {
        return bookmarkRepository.searchForUser(userId, query, source, pageable);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> countSearchResultsBySource(Long userId, String query) {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (Object[] row : bookmarkRepository.countSearchResultsBySource(userId, query)) {
            counts.put((String) row[0], (Long) row[1]);
        }
        return counts;
    }


    // =====================================================
    // 1. 查詢收藏夾內的所有收藏
    // =====================================================

    @Transactional(readOnly = true)
    public List<Bookmark> getFolderBookmarks(
            Long userId,
            Long folderId
    ) {

        // 先確認收藏夾屬於這位使用者
        findUserFolder(userId, folderId);

        return bookmarkRepository
                .findAllByUserIdAndFolderIdOrderByCreatedAtDesc(
                        userId,
                        folderId
                );
    }


    // =====================================================
    // 2. 查詢單筆收藏
    // =====================================================

    @Transactional(readOnly = true)
    public Bookmark getBookmark(Long userId, Long bookmarkId) {

        return findUserBookmark(userId, bookmarkId);
    }


    // =====================================================
    // 3. 新增收藏
    // =====================================================

    @Transactional
    public Bookmark createBookmark(
            Long userId,
            Long folderId,
            String url,
            String title,
            String content,
            String note,
            String source,
            String imageUrl,
            List<String> tagNames,
            boolean addToDailyPicks
    ) {

        dailyPickSelectionService.lockUser(userId);
        // 確認目標收藏夾屬於目前使用者
        Folder folder = findUserFolder(userId, folderId);

        // 驗證必要欄位
        validateBookmark(url, source);
        validateImageUrl(imageUrl);

        Bookmark bookmark = new Bookmark();

        bookmark.setUserId(userId);
        bookmark.setFolderId(folder.getId());
        bookmark.setUrl(url.trim());
        bookmark.setTitle(title);
        bookmark.setContent(content);
        bookmark.setNote(note);
        bookmark.setSource(source.trim());
        bookmark.setImageUrl(imageUrl);

        // 儲存收藏，取得資料庫產生的 bookmarkId
        Bookmark savedBookmark = bookmarkRepository.save(bookmark);

        if (addToDailyPicks) dailyPickSelectionService.includeBookmark(userId, savedBookmark);

        // 建立收藏與標籤的關聯
        // 如果標籤不存在，TagService 會自動建立
        tagService.replaceBookmarkTags(
                userId,
                folderId,
                savedBookmark.getId(),
                tagNames
        );

        // 更新收藏夾封面
        updateFolderCoverImage(folder);

        return bookmarkRepository.findByIdAndUserId(
                savedBookmark.getId(),
                userId
        ).orElseThrow(() ->
                new ResourceNotFoundException("找不到此收藏")
        );
    }


    // =====================================================
    // 4. 修改收藏內容及標籤
    // =====================================================

    @Transactional
    public Bookmark updateBookmark(
            Long userId,
            Long bookmarkId,
            String url,
            String title,
            String content,
            String note,
            String source,
            String imageUrl,
            List<String> tagNames
    ) {

        // 確認收藏屬於目前使用者
        Bookmark bookmark = findUserBookmark(userId, bookmarkId);

        validateBookmark(url, source);
        validateImageUrl(imageUrl);

        bookmark.setUrl(url.trim());
        bookmark.setTitle(title);
        bookmark.setContent(content);
        bookmark.setNote(note);
        bookmark.setSource(source.trim());
        bookmark.setImageUrl(imageUrl);

        // 主動更新活動時間
        bookmark.setUpdatedAt(LocalDateTime.now());

        Bookmark savedBookmark = bookmarkRepository.save(bookmark);

        // 替換標籤關聯
        // 修改標籤也會更新 Bookmark.updatedAt
        tagService.replaceBookmarkTags(
                userId,
                bookmark.getFolderId(),
                bookmarkId,
                tagNames
        );

        // 更新所屬收藏夾封面
        Folder folder = findUserFolder(userId, bookmark.getFolderId());

        updateFolderCoverImage(folder);

        return bookmarkRepository.findByIdAndUserId(
                bookmarkId,
                userId
        ).orElseThrow(() ->
                new ResourceNotFoundException("找不到此收藏")
        );
    }


    // =====================================================
    // 5. 刪除收藏
    // =====================================================

    @Transactional
    public void deleteBookmark(Long userId, Long bookmarkId) {
        dailyPickSelectionService.removeBookmarks(userId, List.of(bookmarkId));

        Bookmark bookmark = findUserBookmark(userId, bookmarkId);

        Long folderId = bookmark.getFolderId();

        // 先記錄這筆收藏原本使用的標籤 ID
        List<Long> oldTagIds = bookmarkTagRepository
                .findAllByBookmarkId(userId, folderId, bookmarkId)
                .stream()
                .map(BookmarkTag::getTagId)
                .distinct()
                .toList();

        // 先刪除收藏與標籤之間的關聯
        bookmarkTagRepository.deleteAllByBookmarkId(
                userId,
                folderId,
                bookmarkId
        );

        bookmarkTagRepository.flush();

        // 刪除收藏本身
        bookmarkRepository.delete(bookmark);
        bookmarkRepository.flush();

        // 清除已經沒有被其他收藏使用的標籤
        for (Long tagId : oldTagIds) {

            long usageCount = bookmarkTagRepository.countByTagId(
                    userId,
                    folderId,
                    tagId
            );

            if (usageCount == 0) {
                tagRepository.deleteById(tagId);
            }
        }

        tagRepository.flush();

        // 更新收藏夾封面
        Folder folder = findUserFolder(userId, folderId);

        updateFolderCoverImage(folder);
    }


    // =====================================================
    // 6. 搬移收藏到其他收藏夾
    // =====================================================

    @Transactional
    public Bookmark moveBookmark(
            Long userId,
            Long bookmarkId,
            Long targetFolderId
    ) {

        // 查詢原本的收藏
        Bookmark bookmark = findUserBookmark(userId, bookmarkId);

        // 確認目標收藏夾屬於目前使用者
        Folder targetFolder = findUserFolder(userId, targetFolderId);

        Long sourceFolderId = bookmark.getFolderId();

        // 如果來源與目標相同，就不需要搬移
        if (sourceFolderId.equals(targetFolderId)) {
            return bookmark;
        }

        // 取得來源收藏夾
        Folder sourceFolder = findUserFolder(userId, sourceFolderId);

        // 搬移收藏，並由 TagService 保留原本的標籤名稱
        tagService.moveBookmarkTags(
                userId,
                bookmark,
                targetFolderId
        );

        // 更新來源收藏夾與目標收藏夾的封面
        updateFolderCoverImage(sourceFolder);
        updateFolderCoverImage(targetFolder);

        return bookmark;
    }


    // =====================================================
    // 7. 共用方法：查詢使用者自己的收藏夾
    // =====================================================

    private Folder findUserFolder(Long userId, Long folderId) {

        return folderRepository.findByIdAndUserId(folderId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("找不到此收藏夾")
                );
    }


    // =====================================================
    // 8. 共用方法：查詢使用者自己的收藏
    // =====================================================

    private Bookmark findUserBookmark(Long userId, Long bookmarkId) {

        return bookmarkRepository.findByIdAndUserId(bookmarkId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("找不到此收藏")
                );
    }


    // =====================================================
    // 9. 共用方法：驗證收藏必要欄位
    // =====================================================

    private void validateBookmark(String url, String source) {

        // 檢查收藏網址是否為空
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("收藏網址不能為空");
        }

        String normalizedUrl = url.trim();

        // 檢查收藏網址長度
        if (normalizedUrl.length() > 2048) {
            throw new IllegalArgumentException("收藏網址長度不能超過 2048 個字");
        }

        // 檢查網址格式
        URI uri;

        try {
            uri = URI.create(normalizedUrl);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("收藏網址格式不正確");
        }

        // 只允許 HTTP 或 HTTPS
        String scheme = uri.getScheme();

        if (scheme == null ||
                (!scheme.equalsIgnoreCase("http") &&
                        !scheme.equalsIgnoreCase("https"))) {
            throw new IllegalArgumentException("收藏網址必須使用 HTTP 或 HTTPS");
        }

        // 檢查是否具有主機名稱
        if (uri.getHost() == null || uri.getHost().isBlank()) {
            throw new IllegalArgumentException("收藏網址格式不正確");
        }

        // 檢查收藏來源是否為空
        if (source == null || source.trim().isEmpty()) {
            throw new IllegalArgumentException("收藏來源不能為空");
        }

        // 檢查收藏來源長度
        if (source.trim().length() > 50) {
            throw new IllegalArgumentException("收藏來源不能超過 50 個字");
        }
    }


    // =====================================================
    // 10. 共用方法：更新收藏夾封面
    // =====================================================

    private void updateFolderCoverImage(Folder folder) {

        Optional<Bookmark> latestBookmark =
                bookmarkRepository.findTopByUserIdAndFolderIdOrderByCreatedAtDesc(
                        folder.getUserId(),
                        folder.getId()
                );

        folder.setImageUrl(
                latestBookmark
                        .map(Bookmark::getImageUrl)
                        .orElse(null)
        );

        folderRepository.save(folder);
    }

    // 驗證 ImageUrl
    private void validateImageUrl(String imageUrl) {

        // 預覽圖片網址為選填
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        String normalizedUrl = imageUrl.trim();

        // 檢查長度
        if (normalizedUrl.length() > 2048) {
            throw new IllegalArgumentException("預覽圖片網址長度不能超過 2048 個字");
        }

        URI uri;

        try {
            uri = URI.create(normalizedUrl);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("預覽圖片網址格式不正確");
        }

        String scheme = uri.getScheme();

        // 只允許 HTTP 或 HTTPS
        if (scheme == null ||
                (!scheme.equalsIgnoreCase("http") &&
                        !scheme.equalsIgnoreCase("https"))) {
            throw new IllegalArgumentException("預覽圖片網址必須使用 HTTP 或 HTTPS");
        }

        // 必須有主機名稱
        if (uri.getHost() == null || uri.getHost().isBlank()) {
            throw new IllegalArgumentException("預覽圖片網址格式不正確");
        }
    }
}
