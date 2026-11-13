package com.personal.bookmark_backend.service;

import com.personal.bookmark_backend.entity.RefreshToken;
import com.personal.bookmark_backend.entity.User;
import com.personal.bookmark_backend.repository.RefreshTokenRepository;
import com.personal.bookmark_backend.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefreshTokenLegacyTest {
    private static final String TOKEN = "a".repeat(43);
    private static final String REQUEST_ID = "b".repeat(43);
    private final RefreshTokenRepository tokens = mock(RefreshTokenRepository.class);
    private final UserRepository users = mock(UserRepository.class);
    private final RefreshTokenService service = new RefreshTokenService(
            tokens, users, mock(EntityManager.class), Base64.getEncoder().encodeToString(new byte[32]));

    @Test
    void rotatesLegacyTokenAndPreservesAbsoluteDeadline() {
        var old = legacyToken(LocalDateTime.now().minusDays(175));
        when(tokens.save(any(RefreshToken.class))).thenAnswer(invocation -> {
            RefreshToken successor = invocation.getArgument(0);
            assertEquals(old.getCreatedAt().plusDays(180), successor.getAbsoluteExpiresAt());
            assertEquals(successor.getAbsoluteExpiresAt(), successor.getExpiresAt());
            successor.setId(2L);
            return successor;
        });

        assertNotNull(service.rotateRefreshToken(TOKEN, REQUEST_ID));
        assertEquals(old.getCreatedAt().plusDays(180), old.getAbsoluteExpiresAt());
        assertEquals(2L, old.getSuccessorId());
    }

    @Test
    void doesNotRotateExpiredLegacyToken() {
        legacyToken(LocalDateTime.now().minusDays(181));
        assertNull(service.rotateRefreshToken(TOKEN, REQUEST_ID));
        verify(tokens, never()).save(any());
    }

    @Test
    void doesNotRestoreExpiredFiniteToken() {
        var old = legacyToken(LocalDateTime.now().minusDays(10));
        old.setExpiresAt(LocalDateTime.now().minusDays(1));
        assertNull(service.rotateRefreshToken(TOKEN, REQUEST_ID));
        verify(tokens, never()).save(any());
    }

    private RefreshToken legacyToken(LocalDateTime createdAt) {
        var old = new RefreshToken();
        old.setId(1L);
        old.setUserId(3L);
        old.setCreatedAt(createdAt);
        old.setExpiresAt(LocalDateTime.of(2000, 1, 1, 0, 0));
        when(tokens.findByTokenHash(AuthService.hashValue(TOKEN))).thenReturn(Optional.of(old));
        when(users.lockById(3L)).thenReturn(Optional.of(new User()));
        return old;
    }
}
