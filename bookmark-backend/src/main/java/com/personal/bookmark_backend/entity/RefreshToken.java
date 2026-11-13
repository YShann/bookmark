package com.personal.bookmark_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "refresh_tokens",
        uniqueConstraints = {

                // 每個 Token 雜湊值必須唯一
                @UniqueConstraint(
                        name = "uk_refresh_tokens_hash",
                        columnNames = "token_hash"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {

    // Token 紀錄 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Token 所屬使用者 ID
    @Column(
            name = "user_id",
            nullable = false
    )
    private Long userId;

    // Refresh Token 的雜湊值，不直接儲存原始 Token
    @Column(
            name = "token_hash",
            nullable = false,
            length = 255
    )
    private String tokenHash;

    // Token 到期時間
    @Column(
            name = "expires_at",
            nullable = false
    )
    private LocalDateTime expiresAt;

    // Token 撤銷時間
    // NULL = 尚未撤銷
    // 有時間 = 已經撤銷
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "family_id")
    private Long familyId;

    @Column(name = "absolute_expires_at", nullable = false)
    private LocalDateTime absoluteExpiresAt;

    @Column(name = "successor_id")
    private Long successorId;

    @Column(name = "rotation_request_hash", length = 64)
    private String rotationRequestHash;

    // Token 建立時間
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    // 新增 Token 時自動設定時間
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
