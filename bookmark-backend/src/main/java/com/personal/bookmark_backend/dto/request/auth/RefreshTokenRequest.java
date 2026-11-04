package com.personal.bookmark_backend.dto.request.auth;


import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(

        @NotBlank(message = "Refresh Token 不可為空")
        String refreshToken

) {
}

