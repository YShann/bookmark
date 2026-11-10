package com.personal.bookmark_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "password_reset_codes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_password_reset_codes_user",
                        columnNames = "user_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 每位使用者只保留一筆紀錄；外鍵由既有 MySQL 資料表管理。
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "code_hash", nullable = false, length = 255)
    private String codeHash;

    @Column(name = "expires_at", nullable = false, columnDefinition = "DATETIME(6)")
    private LocalDateTime expiresAt;

    @Column(name = "attempts", nullable = false)
    private int attempts = 0;

    @Column(name = "verified_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime verifiedAt;

    @Column(name = "reset_token_hash", length = 255)
    private String resetTokenHash;

    @Column(name = "reset_token_expires_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime resetTokenExpiresAt;

    // 保留首次建立時間，重新寄碼時不更新。
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false,
            columnDefinition = "DATETIME(6)"
    )
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
