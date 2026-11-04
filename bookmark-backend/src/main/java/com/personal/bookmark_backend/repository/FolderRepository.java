package com.personal.bookmark_backend.repository;

import com.personal.bookmark_backend.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    // 查詢指定使用者的所有收藏夾
    // 排序規則：
    // 1. 取得每個收藏夾最近一次收藏活動時間。
    // 2. 使用最新收藏的 created_at 作為活動時間。
    // 3. 如果收藏夾沒有收藏，使用收藏夾自己的 created_at。
    // 4. 如果收藏夾建立時間比最新收藏建立時間晚，則使用收藏夾建立時間。
    // 5. 依照最新一筆收藏時間由新到舊排列。
    // 6. 未分類固定在第一個
    @Query(value = """
        SELECT f.*
        FROM folders f
        LEFT JOIN (
            SELECT
                folder_id,
                MAX(created_at) AS latest_bookmark_at
            FROM bookmarks
            WHERE user_id = :userId
            GROUP BY folder_id
        ) b ON b.folder_id = f.id
        WHERE f.user_id = :userId
        ORDER BY
            f.is_default DESC,
            GREATEST(
                f.created_at,
                COALESCE(b.latest_bookmark_at, f.created_at)
            ) DESC,
            f.created_at DESC
        """, nativeQuery = true)
    List<Folder> findAllByUserIdOrderByLatestActivity(
            @Param("userId") Long userId
    );

    // 查詢指定使用者的某一個收藏夾
    Optional<Folder> findByIdAndUserId(Long id, Long userId);

    // 查詢指定使用者的預設收藏夾「未分類」
    Optional<Folder> findByUserIdAndIsDefaultTrue(Long userId);

    // 依照使用者 ID 和收藏夾名稱查詢
    Optional<Folder> findByUserIdAndName(Long userId, String name);

    // 檢查指定使用者是否已經有同名收藏夾
    boolean existsByUserIdAndName(Long userId, String name);
}
