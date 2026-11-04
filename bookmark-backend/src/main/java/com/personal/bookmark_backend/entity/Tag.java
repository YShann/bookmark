package com.personal.bookmark_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "tags",
        uniqueConstraints = {

                // 同一使用者的同一收藏夾不能有重複標籤名稱
                @UniqueConstraint(
                        name = "uk_tags_user_folder_name",
                        columnNames = {
                                "user_id",
                                "folder_id",
                                "name"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Tag {

    // 標籤 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 標籤所屬使用者 ID
    @Column(
            name = "user_id",
            nullable = false
    )
    private Long userId;

    // 標籤所屬收藏夾 ID
    @Column(
            name = "folder_id",
            nullable = false
    )
    private Long folderId;

    // 標籤名稱
    @Column(
            name = "name",
            nullable = false,
            length = 30
    )
    private String name;

    // 標籤建立時間
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    // 新增標籤之前自動設定建立時間
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

