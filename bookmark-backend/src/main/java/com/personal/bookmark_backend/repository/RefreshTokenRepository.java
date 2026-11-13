package com.personal.bookmark_backend.repository;

import com.personal.bookmark_backend.entity.RefreshToken;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    // 查詢指定使用者的所有 Refresh Token
    List<RefreshToken> findAllByUserId(Long userId);

    // 查詢指定使用者目前尚未撤銷的 Refresh Token
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    List<RefreshToken> findAllByUserIdAndRevokedAtIsNull(Long userId);

    // 查詢指定雜湊值且尚未撤銷的 Refresh Token
    Optional<RefreshToken> findByTokenHashAndRevokedAtIsNull(
            String tokenHash
    );
}
