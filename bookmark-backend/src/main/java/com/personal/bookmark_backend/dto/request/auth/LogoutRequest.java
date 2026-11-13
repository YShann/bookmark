package com.personal.bookmark_backend.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LogoutRequest(

        @NotBlank(message = "Refresh Token 不可為空")
        @Size(max = 43, message = "Refresh Token 格式不正確")
        String refreshToken

) {
}

