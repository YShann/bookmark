package com.personal.bookmark_backend.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(

        @NotBlank(message = "重設密碼憑證不可為空")
        String resetToken,

        @NotBlank(message = "新密碼不可為空")
        String newPassword,

        @NotBlank(message = "確認密碼不可為空")
        String confirmPassword

) {
}
