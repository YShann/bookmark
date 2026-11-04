package com.personal.bookmark_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookmark_tags")
@Getter
@Setter
@NoArgsConstructor
public class BookmarkTag {

    // 使用 BookmarkTagId 作為複合主鍵
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(
                    name = "bookmarkId",
                    column = @Column(name = "bookmark_id")
            ),
            @AttributeOverride(
                    name = "tagId",
                    column = @Column(name = "tag_id")
            )
    })
    private BookmarkTagId id;

    // 使用者 ID
    // 用來確認收藏與標籤屬於同一位使用者
    @Column(
            name = "user_id",
            nullable = false
    )
    private Long userId;

    // 收藏夾 ID
    // 用來確認收藏與標籤位於同一個收藏夾
    @Column(
            name = "folder_id",
            nullable = false
    )
    private Long folderId;

    // 關聯建立時間
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    // 新增關聯之前自動設定時間
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // 取得收藏 ID
    public Long getBookmarkId() {
        return id != null ? id.getBookmarkId() : null;
    }

    // 取得標籤 ID
    public Long getTagId() {
        return id != null ? id.getTagId() : null;
    }
}
