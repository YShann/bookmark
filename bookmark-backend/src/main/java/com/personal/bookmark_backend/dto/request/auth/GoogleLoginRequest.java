package com.personal.bookmark_backend.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GoogleLoginRequest(
        @NotBlank(message = "請提供 Google ID Token")
        @Size(max = 8192, message = "Google ID Token 過長")
        String idToken,
        @Size(max = 72, message = "密碼長度過長")
        String password
) {
}
