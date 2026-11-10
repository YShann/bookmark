package com.personal.bookmark_backend.security;

import com.personal.bookmark_backend.exception.UnauthorizedException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GoogleIdTokenVerifierTest {
    private static final String CLIENT_ID = "bookmark.apps.googleusercontent.com";
    private static final KeyPair GOOGLE_KEY = Jwts.SIG.RS256.keyPair().build();
    private final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier(
            CLIENT_ID, NimbusJwtDecoder.withPublicKey((RSAPublicKey) GOOGLE_KEY.getPublic()).build());

    @Test
    void acceptsSignedCredentialsFromBothGoogleIssuerFormats() {
        for (String issuer : new String[]{"https://accounts.google.com", "accounts.google.com"}) {
            String token = token(issuer, CLIENT_ID, true, Instant.now().plusSeconds(300), GOOGLE_KEY);
            assertEquals(new GoogleIdTokenVerifier.GoogleAccount("google-user-id", "test@gmail.com", true),
                    verifier.verify(token));
        }
    }

    @Test
    void rejectsInvalidSignatureIssuerAudienceExpirationAndUnverifiedEmail() {
        String issuer = "https://accounts.google.com";
        Instant expires = Instant.now().plusSeconds(300);
        KeyPair otherKey = Jwts.SIG.RS256.keyPair().build();
        for (String token : new String[]{
                token(issuer, CLIENT_ID, true, expires, otherKey),
                token("https://example.com", CLIENT_ID, true, expires, GOOGLE_KEY),
                token(issuer, "another-client", true, expires, GOOGLE_KEY),
                token(issuer, CLIENT_ID, true, Instant.now().minusSeconds(5), GOOGLE_KEY),
                token(issuer, CLIENT_ID, false, expires, GOOGLE_KEY)
        }) {
            assertThrows(UnauthorizedException.class, () -> verifier.verify(token));
        }
    }

    private String token(String issuer, String audience, boolean verified, Instant expires, KeyPair key) {
        return Jwts.builder()
                .issuer(issuer)
                .audience().add(audience).and()
                .subject("google-user-id")
                .issuedAt(Date.from(Instant.now().minusSeconds(60)))
                .expiration(Date.from(expires))
                .claim("email", "Test@gmail.com")
                .claim("email_verified", verified)
                .signWith(key.getPrivate(), Jwts.SIG.RS256)
                .compact();
    }
}
