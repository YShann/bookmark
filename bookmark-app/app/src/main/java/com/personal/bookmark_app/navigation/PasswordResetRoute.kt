package com.personal.bookmark_app.navigation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.personal.bookmark_app.ui.screen.forgotpassword.ForgotPasswordScreen
import com.personal.bookmark_app.ui.screen.forgotpassword.ForgotPasswordUiState
import com.personal.bookmark_app.ui.screen.forgotpassword.PasswordResetStep
import com.personal.bookmark_app.ui.screen.resetpassword.ResetPasswordScreen
import com.personal.bookmark_app.ui.screen.resetpassword.ResetPasswordUiState
import com.personal.bookmark_app.ui.screen.verifyemail.VerifyEmailScreen
import com.personal.bookmark_app.ui.screen.verifyemail.VerifyEmailUiState
import com.personal.bookmark_app.ui.viewmodel.AuthViewModel

// 三個畫面在同一導航流程中切換，密碼和 Token 由 ViewModel 管理。
@Composable
internal fun PasswordResetRoute(
    authViewModel: AuthViewModel,
    forgotPasswordUiState: ForgotPasswordUiState,
    resetPasswordUiState: ResetPasswordUiState,
    navController: NavHostController
) {
    val context = LocalContext.current
    val leaveFlow = {
        authViewModel.resetForgotPasswordState()
        navController.navigate(Screen.LOGIN) {
            popUpTo(Screen.LOGIN) { inclusive = true }
            launchSingleTop = true
        }
    }
    BackHandler {
        if (forgotPasswordUiState.step == PasswordResetStep.PASSWORD) {
            if (!resetPasswordUiState.isLoading) authViewModel.returnToResetCode("請重新寄送驗證碼")
        } else leaveFlow()
    }
    LaunchedEffect(forgotPasswordUiState.step) {
        if (forgotPasswordUiState.step == PasswordResetStep.SUCCESS) {
            Toast.makeText(context, "密碼重設成功，請重新登入", Toast.LENGTH_LONG).show()
            leaveFlow()
        }
    }
    when (forgotPasswordUiState.step) {
        PasswordResetStep.EMAIL -> ForgotPasswordScreen(
            uiState = forgotPasswordUiState,
            onEmailChange = authViewModel::onForgotPasswordEmailChange,
            onBackClick = leaveFlow,
            onSendCodeClick = authViewModel::forgotPassword
        )

        PasswordResetStep.CODE -> VerifyEmailScreen(
            // 僅轉換給共用 UI；註冊與重設的 ViewModel 狀態完全分開。
            uiState = VerifyEmailUiState(
                email = forgotPasswordUiState.email,
                verificationCode = forgotPasswordUiState.code,
                resendCountdown = forgotPasswordUiState.resendCountdown,
                errorMessage = forgotPasswordUiState.errorMessage,
                isLoading = forgotPasswordUiState.isLoading
            ),
            onCodeChange = authViewModel::onResetCodeChange,
            onVerifyClick = authViewModel::verifyResetCode,
            onResendClick = authViewModel::resendResetCode,
            title = "驗證密碼重設",
            description = "若信箱已註冊，將會收到六位數驗證碼",
            expiryText = "請於 5 分鐘內輸入驗證碼；未收到時請檢查垃圾郵件",
            buttonText = "驗證並設定新密碼",
        )

        PasswordResetStep.PASSWORD -> ResetPasswordScreen(
            uiState = resetPasswordUiState,
            onPasswordChange = authViewModel::onNewPasswordChange,
            onConfirmPasswordChange = authViewModel::onNewPasswordConfirmationChange,
            onPasswordVisibilityChange = authViewModel::toggleNewPasswordVisibility,
            onConfirmPasswordVisibilityChange = authViewModel::toggleNewPasswordConfirmationVisibility,
            onSaveClick = authViewModel::resetPassword,
            onBackClick = { authViewModel.returnToResetCode("請重新寄送驗證碼") }
        )

        PasswordResetStep.SUCCESS -> Unit
    }
}
