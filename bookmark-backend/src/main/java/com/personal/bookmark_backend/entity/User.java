package com.personal.bookmark_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                // 同一個 Email 只能註冊一個帳號
                @UniqueConstraint(
                        name = "uk_users_email",
                        columnNames = "email"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseTimeEntity {

    // 主鍵：使用者 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Email
    @Column(
            name = "email",
            nullable = false,
            length = 255
    )
    private String email;

    // 密碼雜湊值，儲存 BCrypt 雜湊
    @Column(
            name = "password_hash",
            nullable = false,
            length = 255
    )
    private String passwordHash;

    // 使用者頭像圖片網址，尚未設定頭像時允許 NULL
    @Column(
            name = "avatar_url",
            length = 2048
    )
    private String avatarUrl;

    // Email 驗證完成時間，尚未驗證時為 NULL
    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;
}

