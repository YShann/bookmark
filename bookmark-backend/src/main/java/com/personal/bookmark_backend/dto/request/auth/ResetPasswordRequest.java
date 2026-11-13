package com.personal.bookmark_backend.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(

        @NotBlank(message = "重設密碼憑證不可為空")
        @Size(max = 43, message = "重設密碼憑證格式不正確")
        String resetToken,

        @NotBlank(message = "新密碼不可為空")
        @Size(max = 72, message = "新密碼不可超過 72 個 UTF-8 位元組")
        String newPassword,

        @NotBlank(message = "確認密碼不可為空")
        @Size(max = 72, message = "確認密碼不可超過 72 個 UTF-8 位元組")
        String confirmPassword

) {
}
