package com.personal.bookmark_backend.repository;

import com.personal.bookmark_backend.entity.PasswordResetCode;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetCodeRepository
        extends JpaRepository<PasswordResetCode, Long> {

    // 更新前由 Service 在同一交易內鎖定使用者，亦涵蓋尚無重設紀錄的情況。
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    Optional<PasswordResetCode> findByUserId(Long userId);

    // 只以雜湊查詢；Service 仍須檢查有效期限及是否已使用。
    Optional<PasswordResetCode> findByResetTokenHash(String resetTokenHash);
}
