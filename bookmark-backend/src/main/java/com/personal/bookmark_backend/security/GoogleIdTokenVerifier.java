package com.personal.bookmark_backend.security;

import com.personal.bookmark_backend.exception.UnauthorizedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Component
public class GoogleIdTokenVerifier {
    private final String clientId;
    private final JwtDecoder decoder;

    @Autowired
    public GoogleIdTokenVerifier(@Value("${bookmark.google.client-id:}") String clientId) {
        this(clientId, googlePublicKeyDecoder());
    }

    GoogleIdTokenVerifier(String clientId, JwtDecoder decoder) {
        this.clientId = clientId.trim();
        this.decoder = decoder;
    }

    private static JwtDecoder googlePublicKeyDecoder() {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(5000);
        // Spring Security 使用 Google 公鑰驗簽，並處理公鑰的快取與輪替。
        return NimbusJwtDecoder.withJwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .restOperations(new RestTemplate(requestFactory))
                .build();
    }

    public GoogleAccount verify(String idToken) {
        if (clientId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "後端尚未設定 Google 登入");
        }

        Jwt token;
        try {
            token = decoder.decode(idToken);
        } catch (JwtException invalidToken) {
            throw invalidCredential();
        }

        String issuer = token.getClaimAsString("iss");
        String subject = token.getSubject();
        if ((!"https://accounts.google.com".equals(issuer) && !"accounts.google.com".equals(issuer))
                || !List.of(clientId).equals(token.getAudience())
                || token.getExpiresAt() == null || !token.getExpiresAt().isAfter(Instant.now())
                || subject == null || !subject.matches("[!-~]{1,255}")) {
            throw invalidCredential();
        }

        String email = token.getClaimAsString("email");
        if (email == null || email.isBlank() || email.length() > 255
                || !Boolean.TRUE.equals(token.getClaims().get("email_verified"))) {
            throw invalidCredential();
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        String hostedDomain = token.getClaimAsString("hd");
        boolean googleManagesEmail = normalizedEmail.endsWith("@gmail.com")
                || (hostedDomain != null && !hostedDomain.isBlank());
        return new GoogleAccount(subject, normalizedEmail, googleManagesEmail);
    }

    private UnauthorizedException invalidCredential() {
        return new UnauthorizedException("Google 登入憑證無效或已過期，請重新登入");
    }

    public record GoogleAccount(String subject, String email, boolean googleManagesEmail) {
    }
}
