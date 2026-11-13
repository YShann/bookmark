package com.personal.bookmark_backend.dto.request.bookmark;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateBookmarkRequest(

        @NotBlank(message = "收藏網址不能為空")
        @Size(max = 2048, message = "收藏網址長度不能超過 2048 個字")
        String url,

        @NotBlank(message = "收藏標題不能為空")
        @Size(max = 255, message = "收藏標題長度不能超過 255 個字")
        String title,

        String content,

        String note,

        @NotBlank(message = "收藏來源不能為空")
        @Size(max = 30, message = "收藏來源不能超過 30 個字")
        String source,

        @Size(max = 2048, message = "預覽圖片網址長度不能超過 2048 個字")
        String imageUrl,

        List<@Size(max = 30, message = "每個標籤名稱不能超過 30 個字") String> tags,

        boolean addToDailyPicks

) {
}
