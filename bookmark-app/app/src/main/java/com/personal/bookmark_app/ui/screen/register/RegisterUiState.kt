package com.personal.bookmark_app.ui.screen.register

data class RegisterUiState(
    // val nickname: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isTermsAccepted: Boolean = false,

    // 密碼顯示狀態
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,

    // 是否已經點擊過註冊
    val hasSubmitted: Boolean = false,
    val isLoading: Boolean = false
) {
    // 密碼至少 8 個字元
    val hasValidLength: Boolean
        get() = password.length >= 8

    // 密碼必須包含英文字母及數字
    val hasLetterAndNumber: Boolean
        get() = password.any { it.isLetter() } &&
                password.any { it.isDigit() }

    // Email 基本格式檢查
    val isEmailValid: Boolean
        get() = email.matches(
            Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        )

    // 所有欄位是否符合註冊條件
    val isFormValid: Boolean
        get() = isEmailValid &&
                hasValidLength &&
                hasLetterAndNumber &&
                confirmPassword.isNotBlank() &&
                password == confirmPassword

    // Email 錯誤訊息
    val emailError: String?
        get() = when {
            !hasSubmitted -> null
            email.isBlank() -> "請輸入電子郵件"
            !isEmailValid -> "請輸入有效的電子郵件"
            else -> null
        }

    // 密碼錯誤訊息
    val passwordError: String?
        get() = when {
            !hasSubmitted -> null
            password.isBlank() -> "請設定密碼"
            !hasValidLength -> "密碼至少需要 8 個字元"
            !hasLetterAndNumber -> "密碼需包含英文字母及數字"
            else -> null
        }

    // 確認密碼錯誤訊息
    val confirmPasswordError: String?
        get() = when {
            !hasSubmitted -> null
            confirmPassword.isBlank() -> "請再次輸入密碼"
            password != confirmPassword -> "兩次輸入的密碼不一致"
            else -> null
        }
}
