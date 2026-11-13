package com.personal.bookmark_backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration
    ) {
        // 將 Base64 格式的 Secret 解碼成簽章金鑰
        byte[] keyBytes = Decoders.BASE64.decode(secret);

        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpiration = accessTokenExpiration;
    }

    /**
     * 產生 Access Token
     *
     * @param userId 使用者 ID
     * @param sessionId Refresh Token 資料列的 ID
     * @return JWT Token 字串
     */
    public String generateAccessToken(Long userId, Long sessionId) {

        Date now = new Date();

        Date expiration = new Date(
                now.getTime() + accessTokenExpiration
        );

        return Jwts.builder()
                .subject(userId.toString())
                .claim("sid", sessionId)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 從 Token 取得使用者 ID
     */
    public AccessIdentity authenticate(String token) {
        Claims claims = parseClaims(token);
        Long sessionId = claims.get("sid", Long.class);
        Date expiration = claims.getExpiration();
        if (sessionId == null || sessionId <= 0 || expiration == null || !expiration.after(new Date())) {
            throw new JwtException("Invalid access token");
        }
        return new AccessIdentity(Long.parseLong(claims.getSubject()), sessionId);
    }

    public record AccessIdentity(Long userId, Long sessionId) {}

    /**
     * 解析並驗證 JWT
     */
    private Claims parseClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
