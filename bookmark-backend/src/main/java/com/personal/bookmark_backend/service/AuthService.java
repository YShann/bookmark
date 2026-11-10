package com.personal.bookmark_backend.service;

import com.personal.bookmark_backend.entity.EmailVerificationCode;
import com.personal.bookmark_backend.entity.Folder;
import com.personal.bookmark_backend.entity.PasswordResetCode;
import com.personal.bookmark_backend.entity.User;
import com.personal.bookmark_backend.exception.ConflictException;
import com.personal.bookmark_backend.exception.ForbiddenException;
import com.personal.bookmark_backend.exception.ResourceNotFoundException;
import com.personal.bookmark_backend.exception.TooManyRequestsException;
import com.personal.bookmark_backend.exception.UnauthorizedException;
import com.personal.bookmark_backend.repository.EmailVerificationCodeRepository;
import com.personal.bookmark_backend.repository.FolderRepository;
import com.personal.bookmark_backend.repository.PasswordResetCodeRepository;
import com.personal.bookmark_backend.repository.UserRepository;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.personal.bookmark_backend.service.PasswordResetRateLimiter.RESEND_SECONDS;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String GOOGLE_LOGIN_HINT = "若你原本使用 Google 建立帳號，請選擇 Google 登入";
    private static final String REGISTERED_EMAIL = "此 Email 已經註冊，請用原本的方式登入。" + GOOGLE_LOGIN_HINT;
    private static final String UNREGISTERED_EMAIL = "此 Email 尚未註冊，請先建立帳號。";
    private static final String INVALID_LOGIN = "Email 或密碼錯誤。" + GOOGLE_LOGIN_HINT;

    private final UserRepository userRepository;
    private final FolderRepository folderRepository;
    private final EmailVerificationCodeRepository emailVerificationCodeRepository;

    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Value("${spring.mail.username}")
    private String senderEmail;

    private final SecureRandom secureRandom = new SecureRandom();

    private final PasswordResetCodeRepository passwordResetCodeRepository;
    private final PasswordResetRateLimiter passwordResetRateLimiter;
    private final PlatformTransactionManager transactionManager;
    private final EntityManager entityManager;
    private static final int RESET_CODE_MINUTES = 5;
    private static final int MAX_RESET_ATTEMPTS = 5;
    private static final int RESET_TOKEN_MINUTES = 10;
    private static final String INVALID_RESET_TOKEN = "重設密碼憑證無效或已過期，請重新驗證";

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    // 有界寄信佇列；合法 Email 使用相同排程路徑，回應不等待 SMTP。
    private final ExecutorService resetMailExecutor = new ThreadPoolExecutor(
            2, 2, 0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(100),
            new ThreadPoolExecutor.AbortPolicy());

    /**
     * 發送註冊 Email 驗證碼
     */
    @Transactional
    public void sendRegistrationCode(String email) {

        String normalizedEmail = normalizeEmail(email);

        // 檢查 Email 是否已經註冊
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ConflictException(
                    REGISTERED_EMAIL
            );
        }

        LocalDateTime resendCutoff = LocalDateTime.now().minusSeconds(RESEND_SECONDS);
        boolean recentlySent = emailVerificationCodeRepository
                .findTopByEmailOrderByCreatedAtDesc(normalizedEmail)
                .map(code -> code.getCreatedAt().isAfter(resendCutoff))
                .orElse(false);
        if (recentlySent) {
            throw new TooManyRequestsException();
        }
        // 若兩個請求同時進來，第二個不用等第一個寫入資料庫就會被擋下。
        passwordResetRateLimiter.checkRegistrationResend(hashValue(normalizedEmail));

        // 產生六位數驗證碼
        String verificationCode = generateVerificationCode();

        // 儲存驗證碼
        String codeHash = passwordEncoder.encode(verificationCode);

        EmailVerificationCode code = new EmailVerificationCode();

        code.setEmail(normalizedEmail);
        code.setCodeHash(codeHash);
        code.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        code.setVerifiedAt(null);

        emailVerificationCodeRepository.save(code);

        // 寄送驗證信
        sendVerificationEmail(normalizedEmail, verificationCode);
    }

    /**
     * 驗證 Email 並建立正式使用者帳號
     */
    @Transactional
    public void verifyEmailAndRegister(
            String email,
            String verificationCode,
            String password
    ) {

        String normalizedEmail = normalizeEmail(email);

        validatePassword(password);

        if (!isSixDigitCode(verificationCode)) {
            throw new IllegalArgumentException(
                    "請輸入正確的六位數驗證碼"
            );
        }

        // 再次確認 Email 尚未註冊
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ConflictException(
                    REGISTERED_EMAIL
            );
        }

        // 取得該 Email 最近一次發送的驗證碼
        EmailVerificationCode savedCode =
                emailVerificationCodeRepository
                        .findTopByEmailOrderByCreatedAtDesc(normalizedEmail)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "請先取得 Email 驗證碼"
                                )
                        );

        LocalDateTime now = LocalDateTime.now();

        // 確認驗證碼尚未使用
        if (savedCode.getVerifiedAt() != null) {
            throw new IllegalArgumentException(
                    "此驗證碼已經使用"
            );
        }

        // 確認驗證碼沒有過期
        if (!savedCode.getExpiresAt().isAfter(now)) {
            throw new IllegalArgumentException(
                    "驗證碼已過期，請重新取得"
            );
        }

        // 比對驗證碼雜湊值
        if (!passwordEncoder.matches(
                verificationCode,
                savedCode.getCodeHash()
        )) {
            throw new IllegalArgumentException("驗證碼錯誤");
        }

        // 標記驗證碼已使用
        savedCode.setVerifiedAt(now);
        emailVerificationCodeRepository.save(savedCode);

        // 建立正式使用者
        User user = new User();

        user.setEmail(normalizedEmail);
        user.setPasswordHash(
                passwordEncoder.encode(password)
        );
        user.setEmailVerifiedAt(now);

        createDefaultFolder(userRepository.save(user).getId());
    }

    void createDefaultFolder(Long userId) {
        // 建立使用者預設收藏夾「未分類」
        Folder defaultFolder = new Folder();

        defaultFolder.setUserId(userId);
        defaultFolder.setName("未分類");
        defaultFolder.setImageUrl(null);
        defaultFolder.setIsDefault(true);

        folderRepository.save(defaultFolder);
    }

    /**
     * 使用 Email 和密碼登入
     */
    @Transactional
    public AuthResponse login(String email, String password) {

        String normalizedEmail = normalizeEmail(email);

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UnauthorizedException(UNREGISTERED_EMAIL));

        userRepository.lockById(user.getId())
                .orElseThrow(() -> new UnauthorizedException(UNREGISTERED_EMAIL));
        entityManager.refresh(user, LockModeType.PESSIMISTIC_WRITE);

        // 確認 Email 已經驗證
        if (user.getEmailVerifiedAt() == null) {
            throw new ForbiddenException(
                    "請先完成 Email 驗證"
            );
        }

        // 驗證密碼
        if (password == null || user.getPasswordHash() == null
                || !passwordEncoder.matches(
                password,
                user.getPasswordHash()
        )) {
            throw new UnauthorizedException(
                    INVALID_LOGIN
            );
        }

        return issueSession(user);
    }

    AuthResponse issueSession(User user) {
        // 產生 Access Token
        String accessToken =
                jwtService.generateAccessToken(user.getId());

        // 產生 Refresh Token
        String refreshToken =
                refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                accessToken,
                refreshToken
        );
    }

    /**
     * 使用 Refresh Token 更新 Access Token
     */
    @Transactional
    public AuthResponse refreshAccessToken(String rawRefreshToken) {

        // 驗證並輪替 Refresh Token
        RefreshTokenService.RefreshTokenRotationResult result =
                refreshTokenService.rotateRefreshToken(
                        rawRefreshToken
                );

        // 取得使用者資料
        User user = userRepository.findById(result.userId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "使用者不存在"
                        )
                );

        // 產生新的 Access Token
        String newAccessToken =
                jwtService.generateAccessToken(user.getId());

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                newAccessToken,
                result.refreshToken()
        );
    }

    /**
     * 使用者登出
     */
    @Transactional
    public void logout(String refreshToken) {

        refreshTokenService.revokeRefreshToken(
                refreshToken
        );
    }

    /**
     * 撤銷使用者所有裝置的登入狀態
     */
    @Transactional
    public void logoutAll(Long userId) {

        refreshTokenService.revokeAllUserRefreshTokens(
                userId
        );
    }

    /**
     * 寄送 Email 驗證碼
     */
    private void sendVerificationEmail(
            String email,
            String verificationCode
    ) {
        sendEmail(
                email,
                "【聚藏 Bookmark App】Email 驗證碼",
                "您好，\n\n"
                        + "感謝您註冊聚藏 Bookmark App！\n\n"
                        + "您的 Email 驗證碼為："
                        + verificationCode
                        + "\n\n"
                        + "此驗證碼將於 10 分鐘後失效，"
                        + "請勿將驗證碼提供給其他人。\n\n"
                        + "如果您沒有進行註冊，請忽略此信件。"
        );
    }

    /**
     * 將 Email 統一轉換成小寫並去除前後空白
     */
    private String normalizeEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email 不可為空"
            );
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        if (!normalizedEmail.matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )) {
            throw new IllegalArgumentException(
                    "Email 格式不正確"
            );
        }

        return normalizedEmail;
    }

    /**
     * 驗證密碼
     * 至少 8 碼，且包含英文字母與數字
     */
    private void validatePassword(String password) {

        if (password == null
                || password.length() < 8
                || !password.matches(".*[A-Za-z].*")
                || !password.matches(".*\\d.*")) {

            throw new IllegalArgumentException(
                    "密碼至少需要 8 碼，並包含英文字母及數字"
            );
        }
    }

    /**
     * 使用 SHA-256 計算雜湊值
     */
    private String hashValue(String value) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = digest.digest(
                    value.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hashBytes);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "無法使用 SHA-256 雜湊演算法",
                    e
            );
        }
    }

    /**
     * 忘記密碼
     */

    @PreDestroy
    public void shutdownResetMailExecutor() {
        resetMailExecutor.shutdown();
    }

    public void forgotPassword(String email, String source) {
        String normalizedEmail = normalizeEmail(email);
        passwordResetRateLimiter.check("send", hashValue(normalizedEmail), source);

        // 信箱存在與否都走相同佇列，API 不必等待寄信。
        try {
            resetMailExecutor.execute(() -> deliverResetCode(normalizedEmail));
        } catch (RejectedExecutionException failure) {
            throw new TooManyRequestsException();
        }
    }

    public String verifyResetCode(String email, String code, String source) {
        String normalizedEmail = normalizeEmail(email);
        passwordResetRateLimiter.check("verify", hashValue(normalizedEmail), source);
        if (!isSixDigitCode(code)) {
            throw new IllegalArgumentException("請輸入六位數驗證碼");
        }

        // 先提交失敗次數，再回報錯誤，避免例外使 attempts 回滾。
        String token = new TransactionTemplate(transactionManager)
                .execute(status -> checkCodeAndIssueToken(normalizedEmail, code));
        if (token == null) {
            throw new IllegalArgumentException("驗證碼無效、已過期或已達嘗試上限，請重新取得");
        }
        return token;
    }

    @Transactional
    public void resetPassword(String resetToken, String newPassword, String confirmPassword) {
        if (resetToken == null || !resetToken.matches("[A-Za-z0-9_-]{43}")) {
            throw new IllegalArgumentException(INVALID_RESET_TOKEN);
        }
        validatePassword(newPassword);
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("新密碼與確認密碼不一致");
        }

        String tokenHash = hashValue(resetToken);
        PasswordResetCode saved = passwordResetCodeRepository.findByResetTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_RESET_TOKEN));
        User user = userRepository.lockById(saved.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(INVALID_RESET_TOKEN));
        if (user.getPasswordHash() == null) {
            throw new IllegalArgumentException(INVALID_RESET_TOKEN);
        }
        checkResetTokenAfterLock(saved, tokenHash);

        // 改密碼、清除重設憑證、撤銷舊登入憑證一起提交。
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        saved.setResetTokenHash(null);
        saved.setResetTokenExpiresAt(null);
        refreshTokenService.revokeAllUserRefreshTokens(user.getId());
        notifyPasswordChangedAfterCommit(user.getEmail());
    }

    private void deliverResetCode(String email) {
        try {
            // 寄信失敗時回滾，保留原有的驗證紀錄。
            new TransactionTemplate(transactionManager)
                    .executeWithoutResult(status -> saveAndEmailResetCode(email));
        } catch (RuntimeException failure) {
            // SMTP 例外可能包含郵件內容，只記錄通用警告。
            log.warn("密碼重設驗證信處理失敗，請檢查郵件服務與資料庫狀態");
        }
    }

    private void saveAndEmailResetCode(String email) {
        User user = findAndLockUser(email);
        if (user == null) {
            return;
        }
        PasswordResetCode saved = passwordResetCodeRepository.findByUserId(user.getId())
                .orElseGet(PasswordResetCode::new);
        LocalDateTime expiresAt = saved.getExpiresAt();
        // 從到期時間倒推上次發碼時間，確認已經過 60 秒。
        if (expiresAt != null && expiresAt.minusMinutes(RESET_CODE_MINUTES)
                .plusSeconds(RESEND_SECONDS).isAfter(LocalDateTime.now())) {
            return;
        }

        String code;
        do {
            code = generateVerificationCode();
        } while (saved.getCodeHash() != null && passwordEncoder.matches(code, saved.getCodeHash()));

        saved.setUserId(user.getId());
        saved.setCodeHash(passwordEncoder.encode(code));
        saved.setExpiresAt(LocalDateTime.now().plusMinutes(RESET_CODE_MINUTES));
        saved.setAttempts(0);
        saved.setVerifiedAt(null);
        saved.setResetTokenHash(null);
        saved.setResetTokenExpiresAt(null);
        passwordResetCodeRepository.saveAndFlush(saved);
        sendEmail(email, "【聚藏 Bookmark App】密碼重設驗證碼",
                "您的密碼重設驗證碼為：" + code
                        + "\n此驗證碼將於 5 分鐘後失效，請勿提供給其他人。"
                        + "\n如果您沒有要求重設密碼，請忽略此信件。");
    }

    private String checkCodeAndIssueToken(String email, String code) {
        User user = findAndLockUser(email);
        if (user == null) {
            return null;
        }
        PasswordResetCode saved = passwordResetCodeRepository.findByUserId(user.getId()).orElse(null);
        LocalDateTime now = LocalDateTime.now();
        if (saved == null || saved.getVerifiedAt() != null
                || saved.getAttempts() >= MAX_RESET_ATTEMPTS || !saved.getExpiresAt().isAfter(now)) {
            return null;
        }
        if (!passwordEncoder.matches(code, saved.getCodeHash())) {
            saved.setAttempts(saved.getAttempts() + 1);
            return null;
        }

        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        saved.setVerifiedAt(now);
        saved.setResetTokenHash(hashValue(token));
        saved.setResetTokenExpiresAt(now.plusMinutes(RESET_TOKEN_MINUTES));
        return token;
    }

    private User findAndLockUser(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        return user == null ? null : userRepository.lockById(user.getId())
                .filter(account -> account.getPasswordHash() != null)
                .orElse(null);
    }

    private void checkResetTokenAfterLock(PasswordResetCode saved, String tokenHash) {
        // 等鎖期間，另一個請求可能已重寄或用掉 Token，必須從資料庫重讀。
        entityManager.refresh(saved, LockModeType.PESSIMISTIC_WRITE);
        String currentHash = saved.getResetTokenHash();
        LocalDateTime expiresAt = saved.getResetTokenExpiresAt();
        if (saved.getVerifiedAt() == null || currentHash == null
                || expiresAt == null || !expiresAt.isAfter(LocalDateTime.now())
                || !MessageDigest.isEqual(tokenHash.getBytes(StandardCharsets.UTF_8),
                currentHash.getBytes(StandardCharsets.UTF_8))) {
            throw new IllegalArgumentException(INVALID_RESET_TOKEN);
        }
    }

    private void notifyPasswordChangedAfterCommit(String email) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    resetMailExecutor.execute(() -> sendPasswordChangedNotice(email));
                } catch (RejectedExecutionException failure) {
                    log.warn("密碼變更通知佇列已滿；密碼已成功更新");
                }
            }
        });
    }

    private void sendPasswordChangedNotice(String email) {
        try {
            sendEmail(email, "【聚藏 Bookmark App】密碼已變更",
                    "您的密碼已重設成功，請使用新密碼重新登入。"
                            + "\n若非您本人操作，請立即重新設定密碼並聯絡服務支援。");
        } catch (RuntimeException failure) {
            log.warn("密碼變更通知寄送失敗；密碼已成功更新");
        }
    }

    private void sendEmail(String email, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    private String generateVerificationCode() {
        return String.format(Locale.ROOT, "%06d", secureRandom.nextInt(1_000_000));
    }

    private boolean isSixDigitCode(String code) {
        return code != null && code.matches("[0-9]{6}");
    }

    /**
     * 登入及 Token 更新的回傳資料
     */
    public record AuthResponse(
            Long userId,
            String email,
            String accessToken,
            String refreshToken
    ) {
    }

}

