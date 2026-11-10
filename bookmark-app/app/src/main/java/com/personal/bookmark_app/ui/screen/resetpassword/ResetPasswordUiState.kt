package com.personal.bookmark_app.ui.screen.resetpassword

data class ResetPasswordUiState(
    val password: String = "",
    val confirmPassword: String = "",

    // 密碼顯示狀態
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,

    // 是否已經點擊過儲存
    val hasSubmitted: Boolean = false,
    val isLoading: Boolean = false,

    // API 錯誤訊息
    val errorMessage: String? = null
) {

    // 密碼至少 8 個字元
    val hasValidLength: Boolean
        get() = password.length >= 8

    // 密碼必須包含英文字母及數字
    val hasLetterAndNumber: Boolean
        get() = password.any {
            it in 'A'..'Z' || it in 'a'..'z'
        } && password.any {
            it in '0'..'9'
        }

    // 確認密碼是否符合
    val isConfirmPasswordValid: Boolean
        get() = confirmPassword.isNotBlank() &&
                password == confirmPassword

    // 所有欄位是否符合重設密碼條件
    val isFormValid: Boolean
        get() = hasValidLength &&
                hasLetterAndNumber &&
                isConfirmPasswordValid

    // 新密碼錯誤訊息
    val passwordError: String?
        get() = when {
            !hasSubmitted -> null
            password.isBlank() -> "請設定新密碼"
            !hasValidLength -> "密碼至少需要 8 個字元"
            !hasLetterAndNumber -> "密碼需包含英文字母及數字"
            else -> null
        }

    // 確認密碼錯誤訊息
    val confirmPasswordError: String?
        get() = when {
            !hasSubmitted -> null
            confirmPassword.isBlank() -> "請再次輸入新密碼"
            password != confirmPassword -> "兩次輸入的密碼不一致"
            else -> null
        }

    // 避免 Log 洩漏密碼
    override fun toString(): String =
        "ResetPasswordUiState(isLoading=$isLoading)"
}