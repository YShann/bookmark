package com.personal.bookmark_backend.dto.response.auth;

public record AuthResponse(

        Long userId,

        String email,

        String accessToken,

        String refreshToken

) {
}

