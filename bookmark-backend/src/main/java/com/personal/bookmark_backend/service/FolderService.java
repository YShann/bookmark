package com.personal.bookmark_backend.service;

import com.personal.bookmark_backend.entity.*;
import com.personal.bookmark_backend.exception.ConflictException;
import com.personal.bookmark_backend.exception.ResourceNotFoundException;
import com.personal.bookmark_backend.repository.BookmarkRepository;
import com.personal.bookmark_backend.repository.BookmarkTagRepository;
import com.personal.bookmark_backend.repository.FolderRepository;
import com.personal.bookmark_backend.repository.TagRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FolderService {

    // 收藏夾 Repository
    private final FolderRepository folderRepository;

    // 收藏 Repository
    private final BookmarkRepository bookmarkRepository;

    // 標籤 Repository
    private final TagRepository tagRepository;

    // 收藏與標籤關聯 Repository
    private final BookmarkTagRepository bookmarkTagRepository;


    // =====================================================
    // 1. 查詢使用者的所有收藏夾
    // =====================================================

    @Transactional(readOnly = true)
    public List<Folder> getUserFolders(Long userId) {

        // 依照最近活動時間排序：
        // 最新新增或修改過收藏的收藏夾排在前面。
        // 空收藏夾則依照建立時間排序。
        return folderRepository.findAllByUserIdOrderByLatestActivity(userId);
    }


    // =====================================================
    // 2. 新增收藏夾
    // =====================================================

    @Transactional
    public Folder createFolder(Long userId, String name) {

        // 去除名稱前後的空白
        String folderName = validateFolderName(name);

        // 檢查同一位使用者是否已經有相同名稱的收藏夾
        if (folderRepository.existsByUserIdAndName(userId, folderName)) {
            throw new ConflictException("收藏夾名稱已經存在");
        }

        // 建立新的收藏夾 Entity
        Folder folder = new Folder();

        folder.setUserId(userId);
        folder.setName(folderName);

        // 一般新增的收藏夾不是預設收藏夾
        folder.setIsDefault(false);

        // 儲存至資料庫
        return folderRepository.save(folder);
    }


    // =====================================================
    // 3. 修改收藏夾名稱
    // =====================================================

    @Transactional
    public Folder renameFolder(Long userId, Long folderId, String newName) {

        // 確認新名稱是否合法
        String folderName = validateFolderName(newName);

        // 查詢收藏夾，同時確認它屬於目前使用者
        Folder folder = findUserFolder(userId, folderId);

        // 不允許修改預設收藏夾「未分類」的名稱
        if (Boolean.TRUE.equals(folder.getIsDefault())) {
            throw new IllegalArgumentException("不能修改預設收藏夾的名稱");
        }

        // 檢查是否有其他收藏夾使用相同名稱
        Optional<Folder> existingFolder =
                folderRepository.findByUserIdAndName(userId, folderName);

        if (existingFolder.isPresent()
                && !existingFolder.get().getId().equals(folderId)) {

            throw new ConflictException("收藏夾名稱已經存在");
        }

        // 更新收藏夾名稱
        folder.setName(folderName);

        return folderRepository.save(folder);
    }


    // =====================================================
    // 4. 刪除收藏夾
    // =====================================================

    @Transactional
    public void deleteFolder(Long userId, Long folderId) {

        // 取得準備刪除的收藏夾
        Folder sourceFolder = findUserFolder(userId, folderId);

        // 不允許刪除預設收藏夾「未分類」
        if (Boolean.TRUE.equals(sourceFolder.getIsDefault())) {
            throw new IllegalArgumentException("不能刪除預設收藏夾");
        }

        // 找出使用者的預設收藏夾
        Folder defaultFolder = folderRepository
                .findByUserIdAndIsDefaultTrue(userId)
                .orElseThrow(() ->
                        new IllegalStateException("找不到使用者的預設收藏夾")
                );

        // 取得來源收藏夾內的所有收藏
        List<Bookmark> bookmarks =
                bookmarkRepository.findAllByUserIdAndFolderIdOrderByCreatedAtDesc(
                        userId,
                        folderId
                );

        // 取得來源收藏夾內的所有標籤
        List<Tag> sourceTags =
                tagRepository.findAllByUserIdAndFolderId(userId, folderId);

        // 取得來源收藏夾內的所有收藏與標籤關聯
        List<BookmarkTag> sourceAssociations =
                bookmarkTagRepository.findAllByUserIdAndFolderId(
                        userId,
                        folderId
                );

        // -------------------------------------------------
        // 步驟 A：整理來源收藏夾的標籤資料
        // -------------------------------------------------

        // 建立 Tag ID -> Tag Name 的對照表
        // 例如：
        // 1 -> 美食
        // 2 -> 推薦
        Map<Long, String> sourceTagNameMap = sourceTags.stream()
                .collect(Collectors.toMap(
                        Tag::getId,
                        Tag::getName
                ));

        // 建立 Bookmark ID -> 原本標籤名稱清單
        // 每一筆收藏只保留自己原本使用的標籤
        Map<Long, List<String>> bookmarkTagNamesMap = new HashMap<>();

        for (BookmarkTag association : sourceAssociations) {

            Long bookmarkId = association.getBookmarkId();
            Long tagId = association.getTagId();

            String tagName = sourceTagNameMap.get(tagId);

            // 如果關聯找不到對應標籤，代表資料不一致
            if (tagName == null) {
                throw new IllegalStateException(
                        "找不到收藏對應的標籤，無法安全搬移資料"
                );
            }

            // 將標籤名稱放進該筆收藏自己的清單
            bookmarkTagNamesMap
                    .computeIfAbsent(bookmarkId, key -> new ArrayList<>())
                    .add(tagName);
        }


        // -------------------------------------------------
        // 步驟 B：先刪除來源收藏夾的舊標籤關聯
        // -------------------------------------------------

        // 為什麼要先刪除？
        // 因為 bookmark_tags 有複合外鍵，
        // bookmark 的 folder_id 改變前，
        // 必須先移除原本指向舊 folder_id 的關聯。
        bookmarkTagRepository.deleteAll(sourceAssociations);

        // 強制將刪除操作送到資料庫
        // 避免後續更新 Bookmark 時，舊關聯仍然存在而違反外鍵限制
        bookmarkTagRepository.flush();


        // -------------------------------------------------
        // 步驟 C：將收藏搬移到「未分類」
        // -------------------------------------------------

        // 取得預設收藏夾已經存在的標籤
        List<Tag> defaultTags =
                tagRepository.findAllByUserIdAndFolderId(
                        userId,
                        defaultFolder.getId()
                );

        // 建立 Tag Name -> Tag Entity 的對照表
        // 方便後續重複使用相同名稱的標籤
        Map<String, Tag> defaultTagMap = defaultTags.stream()
                .collect(Collectors.toMap(
                        Tag::getName,
                        tag -> tag
                ));

        // 暫存新建立的標籤關聯
        List<BookmarkTag> newAssociations = new ArrayList<>();

        for (Bookmark bookmark : bookmarks) {

            // 取得這筆收藏原本的標籤名稱
            List<String> originalTagNames =
                    bookmarkTagNamesMap.getOrDefault(
                            bookmark.getId(),
                            List.of()
                    );

            // 修改收藏所屬收藏夾
            bookmark.setFolderId(defaultFolder.getId());

            // 逐一保留這筆收藏原本的標籤
            for (String tagName : originalTagNames) {

                Tag targetTag = defaultTagMap.get(tagName);

                // 如果「未分類」沒有相同名稱的標籤，就建立新的
                if (targetTag == null) {

                    targetTag = new Tag();

                    targetTag.setUserId(userId);
                    targetTag.setFolderId(defaultFolder.getId());
                    targetTag.setName(tagName);

                    targetTag = tagRepository.save(targetTag);

                    // 放進 Map，避免後續收藏重複建立相同標籤
                    defaultTagMap.put(tagName, targetTag);
                }

                // 建立新的收藏與標籤關聯
                BookmarkTag newAssociation = new BookmarkTag();

                BookmarkTagId newId = new BookmarkTagId();

                newId.setBookmarkId(bookmark.getId());
                newId.setTagId(targetTag.getId());

                newAssociation.setId(newId);
                newAssociation.setUserId(userId);
                newAssociation.setFolderId(defaultFolder.getId());

                newAssociations.add(newAssociation);
            }
        }


        // -------------------------------------------------
        // 步驟 D：儲存搬移後的收藏
        // -------------------------------------------------

        // 更新每筆收藏的 folder_id
        // 同時會觸發 Bookmark 的 @PreUpdate，
        // 更新 updated_at，讓「未分類」的最近活動時間也能正確反映搬移。
        bookmarkRepository.saveAll(bookmarks);

        // 確保 Bookmark 的 folder_id 已經更新
        // 再新增指向新 folder_id 的 bookmark_tags 關聯
        bookmarkRepository.flush();


        // -------------------------------------------------
        // 步驟 E：建立新的標籤關聯
        // -------------------------------------------------

        bookmarkTagRepository.saveAll(newAssociations);


        // -------------------------------------------------
        // 步驟 F：刪除來源收藏夾原本的標籤
        // -------------------------------------------------

        // 因為來源收藏夾內的收藏已經全部搬走，
        // 舊的標籤也已經沒有用途，因此可以全部刪除。
        tagRepository.deleteAll(sourceTags);

        tagRepository.flush();


        // -------------------------------------------------
        // 步驟 G：更新「未分類」的封面圖片
        // -------------------------------------------------

        updateFolderCoverImage(defaultFolder);


        // -------------------------------------------------
        // 步驟 H：刪除原本的收藏夾
        // -------------------------------------------------

        // 所有收藏、標籤關聯及標籤都處理完成後，
        // 才能刪除來源收藏夾。
        folderRepository.delete(sourceFolder);

        folderRepository.flush();
    }


    // =====================================================
    // 5. 共用方法：查詢使用者自己的收藏夾
    // =====================================================

    private Folder findUserFolder(Long userId, Long folderId) {

        return folderRepository.findByIdAndUserId(folderId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("找不到此收藏夾")
                );
    }


    // =====================================================
    // 6. 共用方法：驗證收藏夾名稱
    // =====================================================

    private String validateFolderName(String name) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("收藏夾名稱不能為空");
        }

        String folderName = name.trim();

        // Folder Entity 的 name 長度限制為 50
        if (folderName.length() > 50) {
            throw new IllegalArgumentException("收藏夾名稱不能超過 50 個字");
        }

        return folderName;
    }


    // =====================================================
    // 7. 共用方法：更新收藏夾封面圖片
    // =====================================================
    private void updateFolderCoverImage(Folder folder) {

        Optional<Bookmark> latestBookmark =
                bookmarkRepository.findTopByUserIdAndFolderIdOrderByCreatedAtDesc(
                        folder.getUserId(),
                        folder.getId()
                );

        // 有收藏時使用最新收藏的圖片，沒有收藏時清空封面
        folder.setImageUrl(
                latestBookmark
                        .map(Bookmark::getImageUrl)
                        .orElse(null)
        );

        folderRepository.save(folder);
    }
}
