package com.personal.bookmark_backend.service;

import com.personal.bookmark_backend.entity.EmailVerificationCode;
import com.personal.bookmark_backend.entity.Folder;
import com.personal.bookmark_backend.entity.User;
import com.personal.bookmark_backend.exception.ConflictException;
import com.personal.bookmark_backend.exception.ForbiddenException;
import com.personal.bookmark_backend.exception.ResourceNotFoundException;
import com.personal.bookmark_backend.exception.UnauthorizedException;
import com.personal.bookmark_backend.repository.EmailVerificationCodeRepository;
import com.personal.bookmark_backend.repository.FolderRepository;
import com.personal.bookmark_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final FolderRepository folderRepository;
    private final EmailVerificationCodeRepository emailVerificationCodeRepository;

    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Value("${auth.email-verification-expiration}")
    private long verificationCodeExpiration;

    @Value("${spring.mail.username}")
    private String senderEmail;

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * 發送註冊 Email 驗證碼
     */
    @Transactional
    public void sendRegistrationCode(String email) {

        String normalizedEmail = normalizeEmail(email);

        // 檢查 Email 是否已經註冊
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ConflictException(
                    "此 Email 已經註冊"
            );
        }

        // 產生六位數驗證碼
        String verificationCode = String.format(
                "%06d",
                secureRandom.nextInt(1_000_000)
        );

        // 儲存驗證碼
        String codeHash = passwordEncoder.encode(verificationCode);

        EmailVerificationCode code = new EmailVerificationCode();

        code.setEmail(normalizedEmail);
        code.setCodeHash(codeHash);
        code.setExpiresAt(
                LocalDateTime.now().plusNanos(
                        verificationCodeExpiration * 1_000_000
                )
        );
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

        if (verificationCode == null
                || !verificationCode.matches("\\d{6}")) {
            throw new IllegalArgumentException(
                    "請輸入正確的六位數驗證碼"
            );
        }

        // 再次確認 Email 尚未註冊
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ConflictException(
                    "此 Email 已經註冊"
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

        User savedUser = userRepository.save(user);

        // 建立使用者預設收藏夾「未分類」
        Folder defaultFolder = new Folder();

        defaultFolder.setUserId(savedUser.getId());
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
                .orElseThrow(() ->
                        new UnauthorizedException(
                                "Email 或密碼錯誤"
                        )
                );

        // 確認 Email 已經驗證
        if (user.getEmailVerifiedAt() == null) {
            throw new ForbiddenException(
                    "請先完成 Email 驗證"
            );
        }

        // 驗證密碼
        if (password == null
                || !passwordEncoder.matches(
                password,
                user.getPasswordHash()
        )) {
            throw new UnauthorizedException(
                    "Email 或密碼錯誤"
            );
        }

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

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(email);
        message.setSubject("【聚藏 Bookmark App】Email 驗證碼");

        message.setText(
                "您好，\n\n"
                        + "感謝您註冊聚藏 Bookmark App！\n\n"
                        + "您的 Email 驗證碼為："
                        + verificationCode
                        + "\n\n"
                        + "此驗證碼將於 10 分鐘後失效，"
                        + "請勿將驗證碼提供給其他人。\n\n"
                        + "如果您沒有進行註冊，請忽略此信件。"
        );

        mailSender.send(message);
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

        String normalizedEmail = email.trim().toLowerCase();

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

