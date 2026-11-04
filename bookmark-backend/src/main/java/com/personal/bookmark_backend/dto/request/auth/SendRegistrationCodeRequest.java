package com.personal.bookmark_backend.dto.request.auth;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendRegistrationCodeRequest(

        @NotBlank(message = "Email 不可為空")
        @Email(message = "Email 格式不正確")
        String email

) {
}

