package com.personal.bookmark_app.ui.screen.register

data class RegisterUiState(
    val nickname: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isTermsAccepted: Boolean = false,

    // 密碼顯示狀態
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false
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
        get() = nickname.isNotBlank() &&
                isEmailValid &&
                hasValidLength &&
                hasLetterAndNumber &&
                password == confirmPassword &&
                isTermsAccepted
}