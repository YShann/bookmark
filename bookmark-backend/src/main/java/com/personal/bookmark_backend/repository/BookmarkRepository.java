package com.personal.bookmark_backend.repository;

import com.personal.bookmark_backend.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    @Query(value = "select * from bookmarks where user_id = :userId order by rand(:seed)", nativeQuery = true)
    List<Bookmark> findDailyPicks(
            @Param("userId") Long userId,
            @Param("seed") long seed,
            Pageable pageable
    );

    @Query(value = "select * from bookmarks where user_id = :userId and id not in (:excludeIds) order by rand(:seed)", nativeQuery = true)
    List<Bookmark> findDailyPicksExcluding(
            @Param("userId") Long userId,
            @Param("excludeIds") List<Long> excludeIds,
            @Param("seed") long seed,
            Pageable pageable
    );

    long countByUserId(Long userId);

    List<Bookmark> findAllByUserIdAndIdInOrderByCreatedAtDesc(Long userId, List<Long> ids);

    @Query("""
            select b from Bookmark b
            where b.userId = :userId
              and (:query is null or :query = ''
                   or lower(b.title) like lower(concat('%', :query, '%'))
                   or lower(b.url) like lower(concat('%', :query, '%'))
                   or lower(b.content) like lower(concat('%', :query, '%'))
                   or lower(b.note) like lower(concat('%', :query, '%'))
                   or lower(b.source) like lower(concat('%', :query, '%'))
                   or exists (
                       select 1 from BookmarkTag bt, Tag t
                       where bt.id.bookmarkId = b.id
                         and bt.id.tagId = t.id
                         and bt.userId = :userId
                         and lower(t.name) like lower(concat('%', :query, '%'))
                   ))
              and (:source is null or :source = ''
                   or lower(b.source) = lower(:source)
                   or (lower(:source) = 'web' and lower(b.source) = 'other'))
            order by b.createdAt desc, b.id desc
            """)
    Page<Bookmark> searchForUser(
            @Param("userId") Long userId,
            @Param("query") String query,
            @Param("source") String source,
            Pageable pageable
    );

    @Query("""
            select b.source, count(b) from Bookmark b
            where b.userId = :userId
              and (:query is null or :query = ''
                   or lower(b.title) like lower(concat('%', :query, '%'))
                   or lower(b.url) like lower(concat('%', :query, '%'))
                   or lower(b.content) like lower(concat('%', :query, '%'))
                   or lower(b.note) like lower(concat('%', :query, '%'))
                   or lower(b.source) like lower(concat('%', :query, '%'))
                   or exists (
                       select 1 from BookmarkTag bt, Tag t
                       where bt.id.bookmarkId = b.id
                         and bt.id.tagId = t.id
                         and bt.userId = :userId
                         and lower(t.name) like lower(concat('%', :query, '%'))
                   ))
            group by b.source
            """)
    List<Object[]> countSearchResultsBySource(
            @Param("userId") Long userId,
            @Param("query") String query
    );

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
