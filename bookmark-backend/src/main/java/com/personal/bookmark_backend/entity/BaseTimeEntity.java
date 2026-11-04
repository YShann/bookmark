package com.personal.bookmark_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// @MappedSuperclass：
// 代表這個類別本身不會建立獨立資料表。
// 繼承它的 Entity 會擁有這裡定義的欄位。
@MappedSuperclass

// 自動產生 Getter
@Getter

// 自動產生 Setter
@Setter
public abstract class BaseTimeEntity {

    // 建立時間
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    // 最後修改時間
    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    // @PrePersist：
    // 新增資料到資料庫之前，自動執行這個方法。
    @PrePersist
    protected void onCreate() {

        // 取得目前時間
        LocalDateTime now = LocalDateTime.now();

        // 設定建立時間
        createdAt = now;

        // 設定最後修改時間
        updatedAt = now;
    }

    // @PreUpdate：
    // 修改資料庫資料之前，自動執行這個方法。
    @PreUpdate
    protected void onUpdate() {

        // 更新最後修改時間
        updatedAt = LocalDateTime.now();
    }
}
