package com.personal.bookmark_backend.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LinkGoogleRequest(
        @NotBlank(message = "請提供 Google ID Token")
        @Size(max = 8192, message = "Google ID Token 過長")
        String idToken,
        @NotBlank(message = "請輸入目前密碼")
        @Size(max = 72, message = "密碼不可超過 72 個 UTF-8 位元組")
        String password
) {
}
