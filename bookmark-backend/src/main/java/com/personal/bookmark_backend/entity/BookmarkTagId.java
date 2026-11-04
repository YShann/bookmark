package com.personal.bookmark_backend.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

// @Embeddable：
// 表示這個類別可以嵌入另一個 Entity。
// 這裡用來建立複合主鍵。
@Embeddable

@Getter
@Setter
@NoArgsConstructor

// 自動產生 equals() 和 hashCode()
// 複合主鍵必須正確比較兩個欄位
// 同一篇收藏不能重複使用同一個標籤
@EqualsAndHashCode
public class BookmarkTagId implements Serializable {

    // 收藏 ID
    private Long bookmarkId;

    // 標籤 ID
    private Long tagId;
}

