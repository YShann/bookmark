package com.personal.bookmark_backend.controller;

import com.personal.bookmark_backend.mapper.AuthMapper;
import com.personal.bookmark_backend.dto.request.auth.LoginRequest;
import com.personal.bookmark_backend.dto.request.auth.GoogleLoginRequest;
import com.personal.bookmark_backend.dto.request.auth.ForgotPasswordRequest;
import com.personal.bookmark_backend.dto.request.auth.VerifyResetCodeRequest;
import com.personal.bookmark_backend.dto.request.auth.ResetPasswordRequest;
import com.personal.bookmark_backend.dto.response.auth.VerifyResetCodeResponse;

import jakarta.servlet.http.HttpServletRequest;

import com.personal.bookmark_backend.dto.request.auth.LogoutRequest;
import com.personal.bookmark_backend.dto.request.auth.RefreshTokenRequest;
import com.personal.bookmark_backend.dto.request.auth.RegisterRequest;
import com.personal.bookmark_backend.dto.request.auth.SendRegistrationCodeRequest;
import com.personal.bookmark_backend.dto.response.auth.AuthResponse;
import com.personal.bookmark_backend.dto.response.auth.MessageResponse;
import com.personal.bookmark_backend.exception.UnauthorizedException;
import com.personal.bookmark_backend.service.AuthService;
import com.personal.bookmark_backend.service.GoogleAuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final GoogleAuthService googleAuthService;
    private final AuthMapper authMapper;

    /**
     * 1. 發送註冊 Email 驗證碼
     * <p>
     * POST /api/auth/registration-code
     */
    @PostMapping("/registration-code")
    public ResponseEntity<MessageResponse> sendRegistrationCode(
            @Valid @RequestBody SendRegistrationCodeRequest request
    ) {

        authService.sendRegistrationCode(request.email());

        return ResponseEntity.ok(
                new MessageResponse("驗證碼已寄送，請檢查 Email")
        );
    }

    /**
     * 2. 驗證 Email 並註冊帳號
     * <p>
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        authService.verifyEmailAndRegister(
                request.email(),
                request.verificationCode(),
                request.password()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new MessageResponse("註冊成功")
                );
    }

    /**
     * 3. 使用 Email 和密碼登入
     * <p>
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        AuthService.AuthResponse result =
                authService.login(
                        request.email(),
                        request.password()
                );

        return ResponseEntity.ok(
                authMapper.toResponse(result)
        );
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> loginWithGoogle(
            @Valid @RequestBody GoogleLoginRequest request
    ) {
        return ResponseEntity.ok(
                authMapper.toResponse(googleAuthService.login(request.idToken(), request.password()))
        );
    }

    /**
     * 4. 使用 Refresh Token 更新 Token
     * <p>
     * POST /api/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {

        AuthService.AuthResponse result =
                authService.refreshAccessToken(
                        request.refreshToken()
                );

        return ResponseEntity.ok(
                authMapper.toResponse(result)
        );
    }

    /**
     * 5. 使用者登出
     * <p>
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            @Valid @RequestBody LogoutRequest request
    ) {

        authService.logout(request.refreshToken());

        return ResponseEntity.ok(
                new MessageResponse("已成功登出")
        );
    }

    @PostMapping("/refresh-session")
    public ResponseEntity<AuthResponse> refreshSession(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        try {
            return ResponseEntity.ok(
                    authMapper.toResponse(authService.refreshSession(request.refreshToken()))
            );
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("X-Bookmark-Auth-Action", "sign-in")
                    .build();
        }
    }

    /**
     * 6. 登出所有裝置
     * <p>
     * POST /api/auth/logout-all
     * <p>
     * 需要有效的 Access Token
     */
    @PostMapping("/logout-all")
    public ResponseEntity<MessageResponse> logoutAll(
            Authentication authentication
    ) {

        // 從 Spring Security 取得目前登入者的 userId
        Long userId = (Long) authentication.getPrincipal();

        authService.logoutAll(userId);

        return ResponseEntity.ok(
                new MessageResponse("已登出所有裝置")
        );
    }

    /**
     * 7. 忘記密碼
     */

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            HttpServletRequest servletRequest
    ) {
        // 使用連線來源；不直接信任客戶端可偽造的 X-Forwarded-For。
        authService.forgotPassword(
                request.email(),
                servletRequest.getRemoteAddr()
        );
        return ResponseEntity.ok(
                new MessageResponse("如果此 Email 已註冊，將會收到密碼重設驗證碼")
        );
    }

    @PostMapping("/verify-reset-code")
    public ResponseEntity<VerifyResetCodeResponse> verifyResetCode(
            @Valid @RequestBody VerifyResetCodeRequest request,
            HttpServletRequest servletRequest
    ) {
        String resetToken = authService.verifyResetCode(
                request.email(),
                request.code(),
                servletRequest.getRemoteAddr()
        );
        return ResponseEntity.ok(new VerifyResetCodeResponse(resetToken));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        authService.resetPassword(
                request.resetToken(),
                request.newPassword(),
                request.confirmPassword()
        );
        return ResponseEntity.ok(new MessageResponse("密碼重設成功，請重新登入"));
    }
}
