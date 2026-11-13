package com.personal.bookmark_backend.service;

import com.personal.bookmark_backend.entity.RefreshToken;
import com.personal.bookmark_backend.repository.RefreshTokenRepository;
import com.personal.bookmark_backend.repository.UserRepository;
import io.jsonwebtoken.io.Decoders;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class RefreshTokenService {
    private static final int IDLE_DAYS = 30;
    private static final int ABSOLUTE_DAYS = 180;
    private static final int RETRY_SECONDS = 120;
    private static final LocalDateTime LEGACY_NO_EXPIRATION = LocalDateTime.of(2000, 1, 1, 0, 0);
    private static final Base64.Encoder TOKEN_ENCODER = Base64.getUrlEncoder().withoutPadding();

    private final RefreshTokenRepository refreshTokens;
    private final UserRepository users;
    private final EntityManager entityManager;
    private final SecureRandom random = new SecureRandom();
    private final SecretKey rotationKey;

    public RefreshTokenService(RefreshTokenRepository refreshTokens, UserRepository users,
                               EntityManager entityManager, @Value("${jwt.secret}") String secret) {
        this.refreshTokens = refreshTokens;
        this.users = users;
        this.entityManager = entityManager;
        var signingKey = new SecretKeySpec(Decoders.BASE64.decode(secret), "HmacSHA256");
        this.rotationKey = new SecretKeySpec(hmac(signingKey, "bookmark-refresh-key-v1"), "HmacSHA256");
    }

    @Transactional
    public SessionToken createRefreshToken(Long userId) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        LocalDateTime now = LocalDateTime.now();
        return store(userId, null, now.plusDays(ABSOLUTE_DAYS), now, TOKEN_ENCODER.encodeToString(bytes));
    }

    // Returning null lets replay revocation commit before the caller sends the error response.
    @Transactional
    public SessionToken rotateRefreshToken(String rawToken, String requestId) {
        RefreshToken token = lockToken(rawToken);
        if (token == null) return null;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime absoluteExpiry = token.getAbsoluteExpiresAt();
        boolean legacy = absoluteExpiry == null && token.getSuccessorId() == null;
        if (legacy && token.getCreatedAt() != null) {
            absoluteExpiry = token.getCreatedAt().plusDays(ABSOLUTE_DAYS);
        }
        if (absoluteExpiry == null || !absoluteExpiry.isAfter(now)) return null;
        String requestHash = requestId == null ? null : AuthService.hashValue(requestId);
        if (token.getRevokedAt() != null) {
            if (token.getSuccessorId() == null) return null;
            if (requestHash != null && requestHash.equals(token.getRotationRequestHash())
                    && token.getRevokedAt().plusSeconds(RETRY_SECONDS).isAfter(now)) {
                return retry(token, rawToken, requestId, now);
            }
            revokeFamily(token, now);
            return null;
        }

        LocalDateTime idleExpiry = token.getExpiresAt();
        if (idleExpiry == null || (!idleExpiry.isAfter(now)
                && !(legacy && LEGACY_NO_EXPIRATION.equals(idleExpiry)))) return null;
        // The client must retain both inputs to recover the same successor. Only hashes reach the DB.
        String replacement = deriveSuccessor(rawToken, requestId == null ? randomRequestId() : requestId);
        SessionToken next = store(token.getUserId(), familyId(token), absoluteExpiry, now, replacement);
        token.setAbsoluteExpiresAt(absoluteExpiry);
        token.setRevokedAt(now);
        token.setSuccessorId(next.id());
        token.setRotationRequestHash(requestHash);
        return next;
    }

    private SessionToken retry(RefreshToken token, String rawToken, String requestId, LocalDateTime now) {
        String replacement = deriveSuccessor(rawToken, requestId);
        return refreshTokens.lockById(token.getSuccessorId())
                .filter(next -> next.getRevokedAt() == null
                        && next.getExpiresAt() != null && next.getExpiresAt().isAfter(now)
                        && next.getAbsoluteExpiresAt() != null && next.getAbsoluteExpiresAt().isAfter(now))
                .filter(next -> next.getTokenHash().equals(AuthService.hashValue(replacement)))
                .map(next -> new SessionToken(next.getId(), next.getUserId(), replacement))
                .orElse(null);
    }

    private SessionToken store(Long userId, Long familyId, LocalDateTime absoluteExpiry,
                               LocalDateTime now, String rawToken) {
        var token = new RefreshToken();
        token.setUserId(userId);
        token.setFamilyId(familyId);
        token.setAbsoluteExpiresAt(absoluteExpiry);
        LocalDateTime idleExpiry = now.plusDays(IDLE_DAYS);
        token.setExpiresAt(idleExpiry.isBefore(absoluteExpiry) ? idleExpiry : absoluteExpiry);
        token.setTokenHash(AuthService.hashValue(rawToken));
        var saved = refreshTokens.save(token);
        return new SessionToken(saved.getId(), userId, rawToken);
    }

    private RefreshToken lockToken(String rawToken) {
        if (!CredentialInput.isSessionToken(rawToken)) return null;
        RefreshToken token = refreshTokens.findByTokenHash(AuthService.hashValue(rawToken)).orElse(null);
        if (token == null || users.lockById(token.getUserId()).isEmpty()) return null;
        entityManager.refresh(token, LockModeType.PESSIMISTIC_WRITE);
        return token;
    }

    @Transactional
    public void revokeRefreshToken(String rawToken) {
        RefreshToken token = lockToken(rawToken);
        if (token != null) revokeFamily(token, LocalDateTime.now());
    }

    private void revokeFamily(RefreshToken token, LocalDateTime now) {
        refreshTokens.lockActiveFamily(token.getUserId(), familyId(token))
                .forEach(active -> active.setRevokedAt(now));
    }

    private Long familyId(RefreshToken token) {
        return token.getFamilyId() == null ? token.getId() : token.getFamilyId();
    }

    @Transactional
    public void revokeAllUserRefreshTokens(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        refreshTokens.findAllByUserIdAndRevokedAtIsNull(userId)
                .forEach(token -> token.setRevokedAt(now));
    }

    private String randomRequestId() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return TOKEN_ENCODER.encodeToString(bytes);
    }

    private String deriveSuccessor(String rawToken, String requestId) {
        return TOKEN_ENCODER.encodeToString(hmac(rotationKey, rawToken + ":" + requestId));
    }

    private static byte[] hmac(SecretKey key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(key);
            return mac.doFinal(data.getBytes(StandardCharsets.US_ASCII));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("無法換發 Refresh Token", e);
        }
    }

    public record SessionToken(Long id, Long userId, String rawToken) {}
}

