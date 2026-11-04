package com.personal.bookmark_backend.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(

        @NotBlank(message = "Refresh Token 不可為空")
        String refreshToken

) {
}

