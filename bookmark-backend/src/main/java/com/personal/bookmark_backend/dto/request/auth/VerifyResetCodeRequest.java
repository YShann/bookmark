package com.personal.bookmark_backend.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyResetCodeRequest(

        @NotBlank(message = "Email 不可為空")
        @Email(message = "Email 格式不正確")
        String email,

        @NotBlank(message = "驗證碼不可為空")
        @Pattern(
                regexp = "[0-9]{6}",
                message = "請輸入六位數驗證碼"
        )
        String code

) {
}
