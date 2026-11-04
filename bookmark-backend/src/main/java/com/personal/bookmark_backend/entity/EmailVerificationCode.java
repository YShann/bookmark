package com.personal.bookmark_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification_codes")
@Getter
@Setter
@NoArgsConstructor
public class EmailVerificationCode {

    // 驗證碼紀錄 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 要驗證的 Email
    @Column(
            name = "email",
            nullable = false,
            length = 255
    )
    private String email;

    // 驗證碼的雜湊值，不直接儲存使用者收到的原始驗證碼
    @Column(
            name = "code_hash",
            nullable = false,
            length = 255
    )
    private String codeHash;

    // 驗證碼到期時間，超過這個時間就不能再使用
    @Column(
            name = "expires_at",
            nullable = false
    )
    private LocalDateTime expiresAt;

    // 驗證成功時間，尚未驗證成功時為 NULL
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    // 驗證碼建立時間
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    // 新增驗證碼紀錄時自動設定時間
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

