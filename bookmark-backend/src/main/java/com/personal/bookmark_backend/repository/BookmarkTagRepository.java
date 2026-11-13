package com.personal.bookmark_backend.repository;

import com.personal.bookmark_backend.entity.BookmarkTag;
import com.personal.bookmark_backend.entity.BookmarkTagId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookmarkTagRepository
        extends JpaRepository<BookmarkTag, BookmarkTagId> {

    // 查詢指定使用者、指定收藏夾中的所有收藏標籤關聯
    List<BookmarkTag> findAllByUserIdAndFolderId(
            Long userId,
            Long folderId
    );

    // 查詢指定收藏的所有標籤關聯
    @Query("""
            SELECT bt
            FROM BookmarkTag bt
            WHERE bt.userId = :userId
              AND bt.folderId = :folderId
              AND bt.id.bookmarkId = :bookmarkId
            """)
    List<BookmarkTag> findAllByBookmarkId(
            @Param("userId") Long userId,
            @Param("folderId") Long folderId,
            @Param("bookmarkId") Long bookmarkId
    );

    // 一次查詢多筆收藏的標籤關聯
    // 用於批次搬移收藏時，保留每一筆收藏原本的標籤
    @Query("""
            SELECT bt
            FROM BookmarkTag bt
            WHERE bt.userId = :userId
              AND bt.folderId = :folderId
              AND bt.id.bookmarkId IN :bookmarkIds
            """)
    List<BookmarkTag> findAllByBookmarkIds(
            @Param("userId") Long userId,
            @Param("folderId") Long folderId,
            @Param("bookmarkIds") List<Long> bookmarkIds
    );

    // 計算指定標籤目前有多少筆收藏關聯
    // 如果回傳 0，代表這個標籤已經沒有任何收藏使用
    @Query("""
            SELECT COUNT(bt)
            FROM BookmarkTag bt
            WHERE bt.userId = :userId
              AND bt.folderId = :folderId
              AND bt.id.tagId = :tagId
            """)
    long countByTagId(
            @Param("userId") Long userId,
            @Param("folderId") Long folderId,
            @Param("tagId") Long tagId
    );

    // 刪除指定收藏的所有標籤關聯
    @Modifying(
            flushAutomatically = true,
            clearAutomatically = true
    )
    @Query("""
            DELETE FROM BookmarkTag bt
            WHERE bt.userId = :userId
              AND bt.folderId = :folderId
              AND bt.id.bookmarkId = :bookmarkId
            """)
    int deleteAllByBookmarkId(
            @Param("userId") Long userId,
            @Param("folderId") Long folderId,
            @Param("bookmarkId") Long bookmarkId
    );

}
