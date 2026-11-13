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
     * @return JWT Token 字串
     */
    public String generateAccessToken(Long userId) {

        Date now = new Date();

        Date expiration = new Date(
                now.getTime() + accessTokenExpiration
        );

        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 從 Token 取得使用者 ID
     */
    public Long extractUserId(String token) {

        Claims claims = parseClaims(token);

        return Long.parseLong(claims.getSubject());
    }

    /**
     * 驗證 Token 是否有效，以及是否屬於指定使用者
     */
    public boolean isTokenValid(String token, Long userId) {

        try {
            Claims claims = parseClaims(token);

            return claims.getSubject().equals(userId.toString())
                    && claims.getExpiration().after(new Date());

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

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
