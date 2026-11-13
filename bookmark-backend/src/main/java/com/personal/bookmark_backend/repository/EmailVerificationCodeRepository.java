package com.personal.bookmark_backend.repository;

import com.personal.bookmark_backend.entity.EmailVerificationCode;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationCodeRepository
        extends JpaRepository<EmailVerificationCode, Long> {

    // 查詢指定 Email 最新建立的驗證碼紀錄
    Optional<EmailVerificationCode> findTopByEmailOrderByCreatedAtDesc(
            String email
    );

}
