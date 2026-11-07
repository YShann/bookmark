package com.personal.bookmark_backend.controller;

import com.personal.bookmark_backend.dto.request.auth.LoginRequest;
import com.personal.bookmark_backend.dto.request.auth.LogoutRequest;
import com.personal.bookmark_backend.dto.request.auth.RefreshTokenRequest;
import com.personal.bookmark_backend.dto.request.auth.RegisterRequest;
import com.personal.bookmark_backend.dto.request.auth.SendRegistrationCodeRequest;
import com.personal.bookmark_backend.dto.response.auth.AuthResponse;
import com.personal.bookmark_backend.dto.response.auth.MessageResponse;
import com.personal.bookmark_backend.service.AuthService;
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

    /**
     * 1. 發送註冊 Email 驗證碼
     *
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
     *
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
     *
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
                toAuthResponse(result)
        );
    }

    /**
     * 4. 使用 Refresh Token 更新 Token
     *
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
                toAuthResponse(result)
        );
    }

    /**
     * 5. 使用者登出
     *
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

    /**
     * 6. 登出所有裝置
     *
     * POST /api/auth/logout-all
     *
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
     * 將 Service 回傳資料轉換成 Response DTO
     */
    private AuthResponse toAuthResponse(
            AuthService.AuthResponse result
    ) {

        return new AuthResponse(
                result.userId(),
                result.email(),
                result.accessToken(),
                result.refreshToken()
        );
    }
}


