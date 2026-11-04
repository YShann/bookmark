package com.personal.bookmark_backend.dto.response;

import java.time.LocalDateTime;

public record ErrorResponse(

        // HTTP 狀態碼
        int status,

        // 錯誤訊息
        String message,

        // 發生錯誤的時間
        LocalDateTime timestamp

) {
}
