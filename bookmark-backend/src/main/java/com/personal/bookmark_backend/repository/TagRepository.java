package com.personal.bookmark_backend.repository;

import com.personal.bookmark_backend.entity.Tag;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    // 查詢指定使用者在某個收藏夾中的所有標籤
    List<Tag> findAllByUserIdAndFolderIdOrderByCreatedAtAsc(
            Long userId,
            Long folderId
    );

    // 查詢指定使用者、指定收藏夾中的標籤
    // 後續可用於檢查標籤是否還有被收藏使用
    List<Tag> findAllByUserIdAndFolderId(
            Long userId,
            Long folderId
    );
}
