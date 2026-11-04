package com.personal.bookmark_backend.service;

import com.personal.bookmark_backend.entity.Bookmark;
import com.personal.bookmark_backend.entity.BookmarkTag;
import com.personal.bookmark_backend.entity.BookmarkTagId;
import com.personal.bookmark_backend.entity.Tag;
import com.personal.bookmark_backend.exception.ResourceNotFoundException;
import com.personal.bookmark_backend.repository.BookmarkRepository;
import com.personal.bookmark_backend.repository.BookmarkTagRepository;
import com.personal.bookmark_backend.repository.FolderRepository;
import com.personal.bookmark_backend.repository.TagRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final BookmarkRepository bookmarkRepository;
    private final BookmarkTagRepository bookmarkTagRepository;
    private final FolderRepository folderRepository;


    // =====================================================
    // 1. 查詢收藏夾內的所有標籤
    // =====================================================

    @Transactional(readOnly = true)
    public List<Tag> getFolderTags(Long userId, Long folderId) {

        // 先確認收藏夾存在，而且屬於目前登入的使用者
        folderRepository.findByIdAndUserId(folderId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("找不到此收藏夾")
                );

        // 查詢收藏夾內的標籤
        return tagRepository.findAllByUserIdAndFolderIdOrderByCreatedAtAsc(
                userId,
                folderId
        );
    }

    // =====================================================
    // 查詢多筆收藏的標籤名稱
    // =====================================================

    @Transactional(readOnly = true)
    public Map<Long, List<String>> getBookmarkTagNames(
            Long userId,
            Long folderId,
            List<Long> bookmarkIds
    ) {

        // 沒有收藏 ID 時，直接回傳空 Map
        if (bookmarkIds == null || bookmarkIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 查詢這些收藏的標籤關聯
        List<BookmarkTag> associations =
                bookmarkTagRepository.findAllByBookmarkIds(
                        userId,
                        folderId,
                        bookmarkIds
                );

        // 查詢收藏夾內的所有標籤，依建立時間排序
        List<Tag> folderTags =
                tagRepository.findAllByUserIdAndFolderIdOrderByCreatedAtAsc(
                        userId,
                        folderId
                );

        // 建立 Tag ID -> Tag Name 對照表
        Map<Long, String> tagNameMap = new HashMap<>();

        for (Tag tag : folderTags) {
            tagNameMap.put(tag.getId(), tag.getName());
        }

        // 建立 Bookmark ID -> Tag ID Set 對照表
        Map<Long, Set<Long>> bookmarkTagIds = new HashMap<>();

        for (BookmarkTag association : associations) {

            bookmarkTagIds
                    .computeIfAbsent(
                            association.getBookmarkId(),
                            key -> new HashSet<>()
                    )
                    .add(association.getTagId());
        }

        // 建立最終結果
        Map<Long, List<String>> result = new LinkedHashMap<>();

        for (Long bookmarkId : bookmarkIds) {

            Set<Long> tagIds = bookmarkTagIds.getOrDefault(
                    bookmarkId,
                    Collections.emptySet()
            );

            List<String> names = new ArrayList<>();

            // 依照標籤建立時間排序，保留標籤顯示順序
            for (Tag tag : folderTags) {

                if (tagIds.contains(tag.getId())) {
                    names.add(tag.getName());
                }
            }

            result.put(bookmarkId, names);
        }

        return result;
    }


    // =====================================================
    // 2. 替換一筆收藏的標籤
    // =====================================================

    @Transactional
    public void replaceBookmarkTags(
            Long userId,
            Long folderId,
            Long bookmarkId,
            List<String> tagNames
    ) {

        // 確認收藏存在，而且屬於指定使用者及收藏夾
        Bookmark bookmark = bookmarkRepository
                .findByIdAndUserId(bookmarkId, userId)
                .filter(item -> item.getFolderId().equals(folderId))
                .orElseThrow(() ->
                        new ResourceNotFoundException("找不到這筆收藏")
                );

        // 整理使用者傳入的標籤名稱
        // 去除前後空白、排除空字串、移除重複名稱
        Set<String> normalizedNames = normalizeTagNames(tagNames);

        // 先查詢這筆收藏原本的標籤關聯
        List<BookmarkTag> oldAssociations =
                bookmarkTagRepository.findAllByBookmarkId(
                        userId,
                        folderId,
                        bookmarkId
                );

        // 記錄原本使用的標籤 ID
        Set<Long> oldTagIds = oldAssociations.stream()
                .map(BookmarkTag::getTagId)
                .collect(Collectors.toSet());

        // 刪除這筆收藏原本的標籤關聯
        bookmarkTagRepository.deleteAllByBookmarkId(
                userId,
                folderId,
                bookmarkId
        );

        // 先執行刪除，避免後續檢查仍讀到舊關聯
        bookmarkTagRepository.flush();


        // -------------------------------------------------
        // 建立或取得新的標籤
        // -------------------------------------------------

        // 一次查詢這個收藏夾已經存在的標籤
        List<Tag> folderTags =
                tagRepository.findAllByUserIdAndFolderId(userId, folderId);

        // 建立「標籤名稱 -> Tag Entity」對照表
        Map<String, Tag> tagMap = folderTags.stream()
                .collect(Collectors.toMap(
                        Tag::getName,
                        Function.identity()
                ));

        List<BookmarkTag> newAssociations = new ArrayList<>();

        for (String tagName : normalizedNames) {

            Tag tag = tagMap.get(tagName);

            // 如果標籤不存在，就建立新的標籤
            if (tag == null) {

                tag = new Tag();
                tag.setUserId(userId);
                tag.setFolderId(folderId);
                tag.setName(tagName);

                tag = tagRepository.save(tag);

                // 加入對照表，避免同一次操作重複建立
                tagMap.put(tagName, tag);
            }

            // 建立收藏與標籤的關聯
            BookmarkTag association = new BookmarkTag();

            BookmarkTagId associationId = new BookmarkTagId();
            associationId.setBookmarkId(bookmarkId);
            associationId.setTagId(tag.getId());

            association.setId(associationId);
            association.setUserId(userId);
            association.setFolderId(folderId);

            newAssociations.add(association);
        }

        // 儲存新的標籤關聯
        bookmarkTagRepository.saveAll(newAssociations);


        // -------------------------------------------------
        // 清除已經沒有被使用的舊標籤
        // -------------------------------------------------

        deleteOrphanTags(userId, folderId, oldTagIds);


        // -------------------------------------------------
        // 更新收藏活動時間
        // -------------------------------------------------

        // 修改 bookmark_tags 不會自動觸發 Bookmark 的 @PreUpdate，
        // 因此必須主動更新收藏的 updatedAt。
        bookmark.setUpdatedAt(LocalDateTime.now());

        bookmarkRepository.save(bookmark);
    }


    // =====================================================
    // 3. 搬移收藏時，保留原本的標籤名稱
    // =====================================================

    @Transactional
    public void moveBookmarkTags(
            Long userId,
            Bookmark bookmark,
            Long targetFolderId
    ) {

        Long sourceFolderId = bookmark.getFolderId();
        Long bookmarkId = bookmark.getId();

        // 如果目標收藏夾與來源相同，不需要搬移
        if (sourceFolderId.equals(targetFolderId)) {
            return;
        }

        // 查詢原本的標籤關聯
        List<BookmarkTag> oldAssociations =
                bookmarkTagRepository.findAllByBookmarkId(
                        userId,
                        sourceFolderId,
                        bookmarkId
                );

        // 取得來源收藏夾的標籤
        List<Tag> sourceTags =
                tagRepository.findAllByUserIdAndFolderId(
                        userId,
                        sourceFolderId
                );

        // 建立 Tag ID -> Tag Name 對照表
        Map<Long, String> sourceTagNames = sourceTags.stream()
                .collect(Collectors.toMap(
                        Tag::getId,
                        Tag::getName
                ));

        // 保留這筆收藏原本的標籤名稱
        List<String> originalTagNames = new ArrayList<>();

        for (BookmarkTag association : oldAssociations) {

            String tagName = sourceTagNames.get(association.getTagId());

            if (tagName == null) {
                throw new IllegalStateException(
                        "找不到收藏原本的標籤，無法完成搬移"
                );
            }

            originalTagNames.add(tagName);
        }

        Set<Long> oldTagIds = oldAssociations.stream()
                .map(BookmarkTag::getTagId)
                .collect(Collectors.toSet());


        // 刪除來源收藏夾的舊關聯
        bookmarkTagRepository.deleteAllByBookmarkId(
                userId,
                sourceFolderId,
                bookmarkId
        );

        bookmarkTagRepository.flush();


        // 修改收藏所屬收藏夾
        bookmark.setFolderId(targetFolderId);
        bookmark.setUpdatedAt(LocalDateTime.now());

        bookmarkRepository.save(bookmark);
        bookmarkRepository.flush();


        // 在目標收藏夾重新建立相同名稱的標籤關聯
        replaceBookmarkTags(
                userId,
                targetFolderId,
                bookmarkId,
                originalTagNames
        );

        // 清除來源收藏夾中已經沒有被使用的標籤
        deleteOrphanTags(userId, sourceFolderId, oldTagIds);
    }


    // =====================================================
    // 4. 共用方法：清除孤立標籤
    // =====================================================

    private void deleteOrphanTags(
            Long userId,
            Long folderId,
            Set<Long> tagIds
    ) {

        for (Long tagId : tagIds) {

            // 確認這個標籤是否還有其他收藏使用
            long usageCount = bookmarkTagRepository.countByTagId(
                    userId,
                    folderId,
                    tagId
            );

            // 如果沒有任何收藏使用，就刪除標籤
            if (usageCount == 0) {
                tagRepository.deleteById(tagId);
            }
        }

        // 將刪除操作送到資料庫
        tagRepository.flush();
    }


    // =====================================================
    // 5. 共用方法：整理標籤名稱
    // =====================================================

    private Set<String> normalizeTagNames(List<String> tagNames) {

        Set<String> normalizedNames = new LinkedHashSet<>();

        if (tagNames == null) {
            return normalizedNames;
        }

        for (String name : tagNames) {

            if (name == null || name.trim().isEmpty()) {
                continue;
            }

            String normalizedName = name.trim();

            // Tag Entity 的 name 長度限制為 30
            if (normalizedName.length() > 30) {
                throw new IllegalArgumentException(
                        "標籤名稱不能超過 30 個字"
                );
            }

            // LinkedHashSet 可以保留輸入順序並自動排除重複值
            normalizedNames.add(normalizedName);
        }

        return normalizedNames;
    }
}
