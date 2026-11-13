package com.personal.bookmark_backend.dto.request.bookmark;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MoveBookmarkRequest(

        @NotNull(message = "目標收藏夾 ID 不能為空")
        @Positive(message = "目標收藏夾 ID 必須大於 0")
        Long targetFolderId

) {
}