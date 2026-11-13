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

    @Query("""
            select b.folderId, count(b) from Bookmark b
            where b.userId = :userId and b.folderId in :folderIds
            group by b.folderId
            """)
    List<Object[]> countByFolderIds(Long userId, List<Long> folderIds);

    // 分頁與全選共用篩選條件，所選標籤必須全部符合。
    String FOLDER_BOOKMARK_FILTER = """
            from Bookmark b
            left join BookmarkSource s on lower(s.code) = lower(coalesce(b.source, 'OTHER'))
            where b.userId = :userId and b.folderId = :folderId
              and (:query = '' or locate(lower(:query), lower(b.title)) > 0
                   or locate(lower(:query), lower(case
                       when b.content is null or trim(b.content) = '' then b.url else b.content end)) > 0
                   or locate(lower(:query), lower(b.note)) > 0
                   or locate(lower(:query), lower(coalesce(s.displayName, b.source, 'OTHER'))) > 0)
              and (:tagCount = 0 or :tagCount = (
                   select count(distinct t.id) from BookmarkTag bt, Tag t
                   where bt.id.bookmarkId = b.id and bt.id.tagId = t.id
                     and bt.userId = :userId and bt.folderId = :folderId
                     and t.name in :tags))
            """;

    @Query(value = "select b " + FOLDER_BOOKMARK_FILTER, countQuery = "select count(b) "
            + FOLDER_BOOKMARK_FILTER)
    Page<Bookmark> findFolderBookmarkPage(
            Long userId, Long folderId, String query, List<String> tags, long tagCount, Pageable pageable);

    @Query("select b.id " + FOLDER_BOOKMARK_FILTER)
    List<Long> findMatchingFolderBookmarkIds(
            Long userId,
            Long folderId,
            String query,
            List<String> tags,
            long tagCount
    );

    List<Bookmark> findAllByUserIdAndIdInOrderByCreatedAtDesc(Long userId, List<Long> ids);

    // 搜尋列表與來源統計共用關鍵字條件；平台條件只用在列表。
    String SEARCH_FILTER = """
            from Bookmark b
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
            """;

    @Query("select b " + SEARCH_FILTER + """
              and (:source is null or :source = ''
                   or lower(b.source) = lower(:source))
            order by b.createdAt desc, b.id desc
            """)
    Page<Bookmark> searchForUser(
            @Param("userId") Long userId,
            @Param("query") String query,
            @Param("source") String source,
            Pageable pageable
    );

    @Query("select b.source, count(b) " + SEARCH_FILTER + """
            group by b.source
            """)
    List<Object[]> countSearchResultsBySource(
            @Param("userId") Long userId,
            @Param("query") String query
    );

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

}
