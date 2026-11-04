package com.personal.bookmark_backend.repository;

import com.personal.bookmark_backend.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    // 依照 Token 雜湊值查詢 Refresh Token
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    // 查詢指定使用者的所有 Refresh Token
    List<RefreshToken> findAllByUserId(Long userId);

    // 查詢指定使用者目前尚未撤銷的 Refresh Token
    List<RefreshToken> findAllByUserIdAndRevokedAtIsNull(Long userId);

    // 查詢指定雜湊值且尚未撤銷的 Refresh Token
    Optional<RefreshToken> findByTokenHashAndRevokedAtIsNull(
            String tokenHash
    );
}