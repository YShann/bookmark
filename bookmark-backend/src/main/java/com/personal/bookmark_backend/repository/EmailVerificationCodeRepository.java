package com.personal.bookmark_backend.repository;

import com.personal.bookmark_backend.entity.EmailVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmailVerificationCodeRepository
        extends JpaRepository<EmailVerificationCode, Long> {

    // 查詢指定 Email 最新建立的驗證碼紀錄
    Optional<EmailVerificationCode> findTopByEmailOrderByCreatedAtDesc(
            String email
    );

    // 查詢指定 Email 尚未驗證的驗證碼紀錄
    List<EmailVerificationCode> findAllByEmailAndVerifiedAtIsNullOrderByCreatedAtDesc(
            String email
    );
}
