package com.personal.bookmark_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "folders",
        uniqueConstraints = {

                // 同一個使用者不能建立相同名稱的收藏夾
                @UniqueConstraint(
                        name = "uk_folders_user_name",
                        columnNames = {"user_id", "name"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Folder extends BaseTimeEntity {

    // 收藏夾 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 所屬使用者 ID，用來區分不同使用者的收藏夾
    @Column(
            name = "user_id",
            nullable = false
    )
    private Long userId;

    // 收藏夾名稱
    @Column(
            name = "name",
            nullable = false,
            length = 50
    )
    private String name;

    // 收藏夾封面圖片網址
    // 取得該收藏夾最新一筆收藏的 image_url，沒有收藏時允許 NULL
    @Column(
            name = "image_url",
            length = 2048
    )
    private String imageUrl;

    // 是否為預設收藏夾
    // true = 未分類
    // false = 一般收藏夾
    @Column(
            name = "is_default",
            nullable = false
    )
    private Boolean isDefault = false;
}
