package com.personal.bookmark_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bookmarks")
@Getter
@Setter
@NoArgsConstructor
public class Bookmark extends BaseTimeEntity {

    // 收藏 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 收藏所屬使用者 ID
    @Column(
            name = "user_id",
            nullable = false
    )
    private Long userId;

    // 收藏所屬收藏夾 ID
    @Column(
            name = "folder_id",
            nullable = false
    )
    private Long folderId;

    // 收藏網址
    @Column(
            name = "url",
            nullable = false,
            length = 2048
    )
    private String url;

    // 收藏標題
    @Column(
            name = "title",
            nullable = false,
            length = 255
    )
    private String title;

    // 收藏的文章原始內文
    @Column(
            name = "content",
            columnDefinition = "TEXT"
    )
    private String content;

    // 使用者自行撰寫的備註
    @Column(
            name = "note",
            columnDefinition = "TEXT"
    )
    private String note;

    // 收藏來源
    // 例如 THREADS、DCARD、INSTAGRAM、YOUTUBE、OTHER
    // 使用 String 儲存，未來增加來源不需要修改資料庫型態
    @Column(
            name = "source",
            nullable = false,
            length = 30
    )
    private String source;

    // 網頁預覽圖片網址
    @Column(
            name = "image_url",
            length = 2048
    )
    private String imageUrl;
}
