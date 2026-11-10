package com.personal.bookmark_app.ui.mockdata

import com.personal.bookmark_app.ui.screen.verifyemail.VerifyEmailUiState

object VerifyEmailMockData {

    val default = VerifyEmailUiState(
        email = "shuhai.reader@cang.app",
        verificationCode = ""
    )

    val error = default.copy(
        verificationCode = "123456",
        errorMessage = "驗證碼錯誤，請重新確認"
    )

    val completed = default.copy(
        verificationCode = "123456",
        resendCountdown = 0
    )
}