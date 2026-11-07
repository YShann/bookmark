package com.personal.bookmark_backend.dto.request.user;

import jakarta.validation.constraints.Size;

public record UpdateAvatarRequest(
        @Size(max = 2048, message = "頭像網址不能超過 2048 個字")
        String avatarImageUrl
) {
}
