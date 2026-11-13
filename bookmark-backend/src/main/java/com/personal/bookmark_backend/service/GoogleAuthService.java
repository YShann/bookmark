package com.personal.bookmark_backend.service;

import com.personal.bookmark_backend.entity.User;
import com.personal.bookmark_backend.exception.ConflictException;
import com.personal.bookmark_backend.exception.ForbiddenException;
import com.personal.bookmark_backend.exception.GoogleLinkRequiredException;
import com.personal.bookmark_backend.exception.UnauthorizedException;
import com.personal.bookmark_backend.repository.UserRepository;
import com.personal.bookmark_backend.security.GoogleIdTokenVerifier;
import com.personal.bookmark_backend.security.GoogleIdTokenVerifier.GoogleAccount;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {
    private final GoogleIdTokenVerifier verifier;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final PlatformTransactionManager transactionManager;
    private final PasswordEncoder passwordEncoder;

    public User link(Long userId, String idToken, String password) {
        GoogleAccount account = verifier.verify(idToken);
        try {
            return new TransactionTemplate(transactionManager)
                    .execute(status -> linkGoogle(lockUser(userId), account, password));
        } catch (DataIntegrityViolationException failure) {
            // 唯一索引也能防止同一個 Google 帳號在兩個請求中同時綁定。
            if (userRepository.findByGoogleSubject(account.subject()).isPresent()) {
                throw new ConflictException("此 Google 帳號已綁定其他聚藏帳號");
            }
            throw failure;
        }
    }

    private User linkGoogle(User user, GoogleAccount account, String password) {
        requireMatchingEmail(user, account);
        if (user.getPasswordHash() == null || password.getBytes(StandardCharsets.UTF_8).length > 72
                || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new ForbiddenException("目前密碼不正確");
        }
        if (account.subject().equals(user.getGoogleSubject())) {
            return user;
        }
        if (user.getGoogleSubject() != null) {
            throw new ConflictException("此聚藏帳號已綁定其他 Google 帳號");
        }
        user.setGoogleSubject(account.subject());
        return userRepository.saveAndFlush(user);
    }

    private User lockUser(Long userId) {
        return userRepository.lockById(userId)
                .orElseThrow(() -> new UnauthorizedException("登入已失效，請重新登入"));
    }

    private void requireMatchingEmail(User user, GoogleAccount account) {
        if (user.getEmailVerifiedAt() == null || !user.getEmail().equalsIgnoreCase(account.email())) {
            throw new ForbiddenException("請選擇與聚藏帳號相同且已驗證的 Google Email");
        }
    }

    public AuthService.AuthResponse login(String idToken, String password) {
        // 先驗證 Google 憑證，再開啟資料庫交易。
        GoogleAccount account = verifier.verify(idToken);
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);
        try {
            return transaction.execute(status -> loginOrRegister(account, password));
        } catch (DataIntegrityViolationException failure) {
            // 與 Email 註冊、Google 註冊或綁定同時發生時，從新交易重查帳號。
            return transaction.execute(status -> {
                if (userRepository.findByGoogleSubject(account.subject()).isPresent()
                        || userRepository.existsByEmail(account.email())) {
                    return loginOrRegister(account, password);
                }
                throw failure;
            });
        }
    }

    private AuthService.AuthResponse loginOrRegister(GoogleAccount account, String password) {
        User existing = userRepository.findByGoogleSubject(account.subject()).orElse(null);
        if (existing != null) {
            return issueSession(existing);
        }

        Long userId = userRepository.findIdByEmail(account.email()).orElse(null);
        if (userId != null) {
            User user = lockUser(userId);
            // 其他請求可能在等待鎖的期間已完成綁定。
            if (account.subject().equals(user.getGoogleSubject())) {
                return authService.issueSession(user);
            }
            if (user.getGoogleSubject() != null || user.getPasswordHash() == null
                    || user.getEmailVerifiedAt() == null) {
                throw new ForbiddenException("請使用此聚藏帳號原本的登入方式");
            }
            if (password == null) {
                throw new GoogleLinkRequiredException();
            }
            return authService.issueSession(linkGoogle(user, account, password));
        }
        if (password != null) {
            throw new ForbiddenException("找不到可綁定的帳號，請重新開始 Google 登入");
        }
        if (!account.googleManagesEmail()) {
            throw new ForbiddenException("首次使用 Google 登入請選擇 Gmail 或 Google Workspace 帳號，或使用 Email 註冊");
        }

        User user = new User();
        user.setGoogleSubject(account.subject());
        user.setEmail(account.email());
        user.setEmailVerifiedAt(LocalDateTime.now());
        userRepository.saveAndFlush(user);
        authService.createDefaultFolder(user.getId());
        return authService.issueSession(user);
    }

    private AuthService.AuthResponse issueSession(User existing) {
        User user = userRepository.lockById(existing.getId())
                .orElseThrow(() -> new UnauthorizedException("找不到此 Google 帳號"));
        return authService.issueSession(user);
    }

}
