package com.personal.bookmark_backend.repository;

import com.personal.bookmark_backend.entity.EmailVerificationCode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface EmailVerificationCodeRepository
        extends JpaRepository<EmailVerificationCode, Long> {

    // 查詢指定 Email 最新建立的驗證碼紀錄
    Optional<EmailVerificationCode> findTopByEmailOrderByCreatedAtDescIdDesc(
            String email
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<EmailVerificationCode> findFirstByEmailOrderByCreatedAtDescIdDesc(String email);

}
