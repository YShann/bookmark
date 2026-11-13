package com.personal.bookmark_backend.service;

import com.personal.bookmark_backend.entity.User;
import com.personal.bookmark_backend.exception.ConflictException;
import com.personal.bookmark_backend.exception.ForbiddenException;
import com.personal.bookmark_backend.exception.UnauthorizedException;
import com.personal.bookmark_backend.repository.UserRepository;
import com.personal.bookmark_backend.security.GoogleIdTokenVerifier;
import com.personal.bookmark_backend.security.GoogleIdTokenVerifier.GoogleAccount;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(PasswordResetTransactionTest.Config.class)
@TestPropertySource(properties = {"spring.mail.username=test@example.com", "jwt.refresh-token-expiration=2592000000"})
class GoogleAccountLinkingTest {
    private static final String PASSWORD = "TestPassword123";

    @Autowired
    AuthService auth;
    @Autowired
    GoogleAuthService google;
    @Autowired
    GoogleIdTokenVerifier verifier;
    @Autowired
    UserRepository users;
    @Autowired
    PasswordEncoder encoder;

    @Test
    void passwordAccountCanLinkAndUseBothLoginMethods() {
        User user = passwordAccount();
        String token = googleToken(UUID.randomUUID().toString(), user.getEmail());
        assertThrows(ConflictException.class, () -> google.login(token, null));

        google.link(user.getId(), token, PASSWORD);
        google.link(user.getId(), token, PASSWORD);
        String otherGoogleAccount = googleToken(UUID.randomUUID().toString(), user.getEmail());
        assertThrows(ConflictException.class, () -> google.link(user.getId(), otherGoogleAccount, PASSWORD));

        assertEquals(user.getId(), google.login(token, null).userId());
        assertEquals(user.getId(), auth.login(user.getEmail(), PASSWORD).userId());
        assertEquals(
                user.getEmailVerifiedAt(),
                users.findById(user.getId()).orElseThrow().getEmailVerifiedAt()
        );
    }

    @Test
    void googleLoginCanLinkExistingAccountOnlyAfterConfirmingCurrentPassword() {
        User user = passwordAccount();
        String subject = UUID.randomUUID().toString();
        String token = googleToken(subject, user.getEmail());

        assertThrows(ConflictException.class, () -> google.login(token, null));
        assertThrows(ForbiddenException.class, () -> google.login(token, "WrongPassword123"));
        assertNull(users.findById(user.getId()).orElseThrow().getGoogleSubject());

        var linked = google.login(token, PASSWORD);
        assertEquals(user.getId(), linked.userId());
        assertNotNull(linked.accessToken());
        assertEquals(subject, users.findById(user.getId()).orElseThrow().getGoogleSubject());
        assertEquals(user.getId(), google.login(token, null).userId());
        assertEquals(user.getId(), auth.login(user.getEmail(), PASSWORD).userId());
        assertEquals(
                user.getEmailVerifiedAt(),
                users.findById(user.getId()).orElseThrow().getEmailVerifiedAt()
        );

        String otherGoogleAccount = googleToken(UUID.randomUUID().toString(), user.getEmail());
        assertThrows(ForbiddenException.class, () -> google.login(otherGoogleAccount, null));
        assertThrows(ForbiddenException.class, () -> google.login(otherGoogleAccount, PASSWORD));
        assertEquals(subject, users.findById(user.getId()).orElseThrow().getGoogleSubject());
    }

    @Test
    void linkRequiresCurrentPasswordAndMatchingEmail() {
        User user = passwordAccount();
        String sameEmail = googleToken(UUID.randomUUID().toString(), user.getEmail());
        String otherEmail = googleToken(UUID.randomUUID().toString(), newEmail());

        assertThrows(
                ForbiddenException.class,
                () -> google.link(user.getId(), sameEmail, "WrongPassword123")
        );
        assertThrows(ForbiddenException.class, () -> google.link(user.getId(), otherEmail, PASSWORD));
        assertNull(users.findById(user.getId()).orElseThrow().getGoogleSubject());
    }

    @Test
    void googleFirstAccountUsesGoogleAndCannotRegisterAgain() {
        String email = newEmail();
        String token = googleToken(UUID.randomUUID().toString(), email);
        Long id = google.login(token, null).userId();
        var registrationError = assertThrows(ConflictException.class, () -> auth.sendRegistrationCode(email));
        var loginError = assertThrows(UnauthorizedException.class, () -> auth.login(email, PASSWORD));
        var unknownEmailError = assertThrows(
                UnauthorizedException.class,
                () -> auth.login(newEmail(), PASSWORD)
        );
        User passwordUser = passwordAccount();
        var wrongPasswordError = assertThrows(
                UnauthorizedException.class,
                () -> auth.login(passwordUser.getEmail(), "WrongPassword123")
        );
        assertTrue(registrationError.getMessage().contains("請選擇 Google 登入"));
        assertTrue(loginError.getMessage().contains("請選擇 Google 登入"));
        assertEquals("此 Email 尚未註冊，請先建立帳號。", unknownEmailError.getMessage());
        assertEquals(wrongPasswordError.getMessage(), loginError.getMessage());
        assertThrows(ForbiddenException.class, () -> google.link(id, token, PASSWORD));
        assertEquals(id, google.login(token, null).userId());
        assertNull(users.findById(id).orElseThrow().getPasswordHash());
    }

    @Test
    void cannotReuseGoogleIdentityAcrossBookmarkAccounts() {
        User first = passwordAccount();
        User second = passwordAccount();
        String subject = UUID.randomUUID().toString();
        String firstToken = googleToken(subject, first.getEmail());
        String secondToken = googleToken(subject, second.getEmail());
        google.link(first.getId(), firstToken, PASSWORD);

        assertThrows(ConflictException.class, () -> google.link(second.getId(), secondToken, PASSWORD));
        assertNull(users.findById(second.getId()).orElseThrow().getGoogleSubject());
        assertEquals(first.getId(), google.login(secondToken, null).userId());
    }

    private User passwordAccount() {
        User user = new User();
        user.setEmail(newEmail());
        user.setEmailVerifiedAt(LocalDateTime.now().withNano(0));
        user.setPasswordHash(encoder.encode(PASSWORD));
        return users.save(user);
    }

    private String googleToken(String subject, String email) {
        String token = UUID.randomUUID().toString();
        when(verifier.verify(token)).thenReturn(new GoogleAccount(subject, email, true));
        return token;
    }

    private String newEmail() {
        return UUID.randomUUID() + "@gmail.com";
    }
}
