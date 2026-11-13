package com.personal.bookmark_backend.service;

import com.personal.bookmark_backend.entity.RefreshToken;
import com.personal.bookmark_backend.exception.UnauthorizedException;
import com.personal.bookmark_backend.repository.RefreshTokenRepository;
import com.personal.bookmark_backend.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    // Reserved value for sessions without a deadline in the existing NOT NULL column.
    private static final LocalDateTime NO_EXPIRATION = LocalDateTime.of(2000, 1, 1, 0, 0);
    private static final String INVALID_REFRESH_TOKEN = "Refresh Token 無效或已失效";

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    // 使用安全亂數產生 Refresh Token
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * 建立新的 Refresh Token
     *
     * @param userId 使用者 ID
     * @return 原始 Refresh Token
     */
    @Transactional
    public String createRefreshToken(Long userId) {

        // 產生 32 bytes 的安全隨機資料
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        // 使用 Base64 URL 格式轉成字串
        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        // 資料庫只儲存 Token 的 SHA-256 雜湊值
        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUserId(userId);
        refreshToken.setTokenHash(tokenHash);
        refreshToken.setExpiresAt(expirationFrom(LocalDateTime.now()));
        refreshToken.setRevokedAt(null);

        refreshTokenRepository.save(refreshToken);

        // 只有這次回傳給使用者原始 Token
        return rawToken;
    }

    /**
     * 更新 Refresh Token
     * <p>
     * 驗證舊 Token 後撤銷舊 Token，
     * 並產生一組新的 Refresh Token。
     */
    @Transactional
    public RefreshTokenRotationResult rotateRefreshToken(String rawToken) {
        RefreshToken oldToken = lockActiveToken(rawToken);
        Long userId = oldToken.getUserId();
        oldToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(oldToken);
        return new RefreshTokenRotationResult(userId, createRefreshToken(userId));
    }

    @Transactional
    public Long renewSession(String rawToken) {
        RefreshToken token;
        try {
            token = lockActiveToken(rawToken);
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException(INVALID_REFRESH_TOKEN);
        }

        // Keep the same credential so a lost response can be retried.
        token.setExpiresAt(expirationFrom(LocalDateTime.now()));
        return token.getUserId();
    }

    private RefreshToken lockActiveToken(String rawToken) {
        RefreshToken token = refreshTokenRepository
                .findByTokenHashAndRevokedAtIsNull(hashToken(rawToken))
                .orElseThrow(() -> new IllegalArgumentException(INVALID_REFRESH_TOKEN));
        userRepository.lockById(token.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(INVALID_REFRESH_TOKEN));
        entityManager.refresh(token, LockModeType.PESSIMISTIC_WRITE);
        LocalDateTime expiry = token.getExpiresAt();
        if (token.getRevokedAt() != null
                || (!NO_EXPIRATION.equals(expiry) && !expiry.isAfter(LocalDateTime.now()))) {
            throw new IllegalArgumentException(INVALID_REFRESH_TOKEN);
        }
        return token;
    }

    private LocalDateTime expirationFrom(LocalDateTime now) {
        return refreshTokenExpiration == 0
                ? NO_EXPIRATION
                : now.plus(refreshTokenExpiration, ChronoUnit.MILLIS);
    }

    /**
     * 撤銷指定的 Refresh Token
     * <p>
     * 用於使用者登出。
     */
    @Transactional
    public void revokeRefreshToken(String rawToken) {

        if (rawToken == null || rawToken.isBlank()) {
            return;
        }

        String tokenHash = hashToken(rawToken);

        refreshTokenRepository
                .findByTokenHashAndRevokedAtIsNull(tokenHash)
                .ifPresent(token -> {
                    token.setRevokedAt(LocalDateTime.now());
                    refreshTokenRepository.save(token);
                });
    }

    /**
     * 撤銷使用者所有尚未撤銷的 Refresh Token
     * <p>
     * 可用於帳號安全管理，例如強制所有裝置登出。
     */
    @Transactional
    public void revokeAllUserRefreshTokens(Long userId) {

        LocalDateTime now = LocalDateTime.now();

        refreshTokenRepository
                .findAllByUserIdAndRevokedAtIsNull(userId)
                .forEach(token -> token.setRevokedAt(now));
    }

    /**
     * 使用 SHA-256 對 Token 進行雜湊
     */
    private String hashToken(String rawToken) {

        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException(
                    "Refresh Token 不可為空"
            );
        }

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = digest.digest(
                    rawToken.getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hashBytes);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "無法使用 SHA-256 雜湊演算法", e
            );
        }
    }

    /**
     * Refresh Token 更新結果
     */
    public record RefreshTokenRotationResult(
            Long userId,
            String refreshToken
    ) {
    }
}

