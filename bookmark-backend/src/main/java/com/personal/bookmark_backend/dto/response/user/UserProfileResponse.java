package com.personal.bookmark_backend.dto.response.user;

public record UserProfileResponse(
        String email,
        String avatarImageUrl,
        boolean googleLinked
) {
}
