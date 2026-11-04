package com.personal.bookmark_backend.repository;

import com.personal.bookmark_backend.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    // 查詢指定使用者的所有收藏
    List<Bookmark> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    // 查詢指定使用者在某個收藏夾中的所有收藏
    // 依照建立時間由新到舊排序
    List<Bookmark> findAllByUserIdAndFolderIdOrderByCreatedAtDesc(
            Long userId,
            Long folderId
    );

    // 查詢指定使用者的某一筆收藏
    Optional<Bookmark> findByIdAndUserId(Long id, Long userId);

    // 查詢指定收藏夾中最新建立的收藏
    // 用於取得收藏夾封面應該顯示的最新收藏圖片
    Optional<Bookmark> findTopByUserIdAndFolderIdOrderByCreatedAtDesc(
            Long userId,
            Long folderId
    );

    // 計算指定使用者在某個收藏夾中的收藏數量
    long countByUserIdAndFolderId(Long userId, Long folderId);

}
