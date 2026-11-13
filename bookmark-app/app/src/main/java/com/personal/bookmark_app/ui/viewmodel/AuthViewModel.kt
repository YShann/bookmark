package com.personal.bookmark_app.ui.viewmodel

import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personal.bookmark_app.data.repository.auth.AuthRepository
import com.personal.bookmark_app.data.repository.auth.GoogleLinkRequiredException
import com.personal.bookmark_app.ui.screen.forgotpassword.ForgotPasswordUiState
import com.personal.bookmark_app.ui.screen.login.LoginUiState
import com.personal.bookmark_app.ui.screen.register.RegisterUiState
import com.personal.bookmark_app.ui.screen.verifyemail.VERIFICATION_RESEND_SECONDS
import com.personal.bookmark_app.ui.screen.verifyemail.VerifyEmailUiState
import com.personal.bookmark_app.ui.screen.forgotpassword.PasswordResetStep
import com.personal.bookmark_app.ui.screen.resetpassword.ResetPasswordUiState
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel(
    private val authRepository: AuthRepository,
    private val nowMillis: () -> Long,
    coroutineScope: CoroutineScope
) : ViewModel(coroutineScope) {
    // Hilt 使用這個建構子；測試仍可透過上面的建構子提供可控制的時間與 CoroutineScope。
    @Inject
    constructor(authRepository: AuthRepository) : this(
        authRepository,
        { SystemClock.elapsedRealtime() },
        CoroutineScope(
            SupervisorJob() + Dispatchers.Main.immediate
        )
    )

    // -------------------------
    // Login UiState
    // -------------------------

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState = _loginUiState.asStateFlow()

    // -------------------------
    // Register UiState
    // -------------------------

    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState = _registerUiState.asStateFlow()

    // -------------------------
    // Verify Email UiState
    // -------------------------

    private val _verifyEmailUiState =
        MutableStateFlow(VerifyEmailUiState())

    val verifyEmailUiState = _verifyEmailUiState.asStateFlow()

    // -------------------------
    // Forgot Password UiState
    // -------------------------

    private val _forgotPasswordUiState =
        MutableStateFlow(ForgotPasswordUiState())

    val forgotPasswordUiState = _forgotPasswordUiState.asStateFlow()

    // -------------------------
    // UI Events
    // -------------------------

    private val _authEvent = MutableSharedFlow<AuthEvent>()
    val authEvent = _authEvent.asSharedFlow()

    private var registrationCountdownJob: Job? = null
    private var registrationResendAvailableAt = 0L

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
                .onSuccess { _authEvent.emit(AuthEvent.LoggedOut) }
                .onFailure { error ->
                    _authEvent.emit(AuthEvent.ShowMessage(error.message ?: "已清除本機登入狀態"))
                    _authEvent.emit(AuthEvent.LoggedOut)
                }
        }
    }

    // ==================================================
    // Login
    // ==================================================

    fun onLoginEmailChange(email: String) {
        _loginUiState.update {
            it.copy(email = email, errorMessage = null)
        }
    }

    fun resetLoginState() {
        _loginUiState.value = LoginUiState()
    }

    fun onLoginPasswordChange(password: String) {
        _loginUiState.update {
            it.copy(password = password, errorMessage = null)
        }
    }

    fun onLoginPasswordVisibilityChange() {
        _loginUiState.update {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    fun login() {
        // 點擊登入後，開始顯示欄位錯誤
        _loginUiState.update {
            it.copy(
                hasSubmitted = true,
                errorMessage = null
            )
        }

        // 取得最新的表單狀態
        val state = _loginUiState.value

        // 前端欄位驗證失敗就停止登入
        if (!state.isFormValid || state.isLoading) return

        viewModelScope.launch {

            // 開始登入，顯示 Loading
            _loginUiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            val result = authRepository.login(
                email = state.email,
                password = state.password
            )

            // 登入完成，關閉 Loading
            _loginUiState.update {
                it.copy(isLoading = false)
            }

            result.onSuccess {
                _authEvent.emit(AuthEvent.Authenticated)

            }.onFailure { error ->
                _loginUiState.update {
                    it.copy(
                        errorMessage = error.message ?: "登入失敗，請稍後再試"
                    )
                }
            }
        }
    }

    suspend fun loginWithGoogle(
        requestIdToken: suspend () -> String?,
        onLinkRequired: (String) -> Unit
    ) {
        if (_loginUiState.value.isLoading || _registerUiState.value.isLoading) return
        _loginUiState.update { it.copy(isLoading = true, errorMessage = null) }
        _registerUiState.update { it.copy(isLoading = true) }
        try {
            val idToken = requestIdToken() ?: return
            val result = authRepository.loginWithGoogle(idToken)
            if (result.isSuccess) {
                _authEvent.emit(AuthEvent.Authenticated)
            } else {
                val error = result.exceptionOrNull()
                if (error is GoogleLinkRequiredException) {
                    onLinkRequired(idToken)
                } else {
                    _authEvent.emit(
                        AuthEvent.ShowMessage(
                            error?.message ?: "Google 登入失敗，請稍後再試"
                        )
                    )
                }
            }
        } finally {
            _loginUiState.update { it.copy(isLoading = false) }
            _registerUiState.update { it.copy(isLoading = false) }
        }
    }

    suspend fun confirmGoogleLink(idToken: String, password: String): Result<Unit> =
        authRepository.loginWithGoogle(idToken, password).onSuccess {
            _authEvent.emit(AuthEvent.Authenticated)
        }

    // ==================================================
    // Register
    // ==================================================

    fun onRegisterEmailChange(email: String) {
        _registerUiState.update {
            it.copy(email = email)
        }
    }

    fun onRegisterPasswordChange(password: String) {
        _registerUiState.update {
            it.copy(password = password)
        }
    }

    fun onConfirmPasswordChange(password: String) {
        _registerUiState.update {
            it.copy(confirmPassword = password)
        }
    }

    fun onRegisterPasswordVisibilityChange() {
        _registerUiState.update {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    fun onConfirmPasswordVisibilityChange() {
        _registerUiState.update {
            it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible)
        }
    }

    fun onTermsAcceptedChange(checked: Boolean) {
        _registerUiState.update {
            it.copy(isTermsAccepted = checked)
        }
    }

    fun register() {
        // 先標記使用者已經嘗試註冊
        _registerUiState.update {
            it.copy(hasSubmitted = true)
        }

        // 取得最新的表單狀態
        val state = _registerUiState.value

        // 驗證失敗就停止，讓畫面顯示錯誤提示
        if (!state.isFormValid || state.isLoading) return

        viewModelScope.launch {
            _registerUiState.update { it.copy(isLoading = true) }

            val result = authRepository.register(
                email = state.email,
                password = state.password
            )

            _registerUiState.update { it.copy(isLoading = false) }

            result.onSuccess {
                _verifyEmailUiState.update {
                    it.copy(
                        email = state.email,
                        verificationCode = "",
                        errorMessage = null
                    )
                }

                startRegistrationCountdown()

                _authEvent.emit(AuthEvent.NavigateToVerifyEmail)
            }.onFailure { error ->
                _authEvent.emit(
                    AuthEvent.ShowMessage(
                        error.message ?: "註冊失敗，請稍後再試"
                    )
                )
            }
        }
    }

    // ==================================================
    // Verify Email
    // ==================================================

    fun onVerificationCodeChange(code: String) {
        _verifyEmailUiState.update {
            it.copy(
                verificationCode = sixDigitInput(code),
                errorMessage = null
            )
        }
    }

    fun verifyEmail() {
        val state = _verifyEmailUiState.value

        if (!state.isVerifyEnabled) return

        viewModelScope.launch {
            _verifyEmailUiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            val result = authRepository.verifyEmail(
                email = state.email,
                code = state.verificationCode
            )

            _verifyEmailUiState.update {
                it.copy(isLoading = false)
            }

            result.onSuccess {
                registrationCountdownJob?.cancel()
                _authEvent.emit(AuthEvent.Authenticated)
            }.onFailure { error ->
                _verifyEmailUiState.update {
                    it.copy(
                        errorMessage = error.message ?: "驗證失敗，請重新嘗試"
                    )
                }
            }
        }
    }

    fun resendVerificationCode() {
        val state = _verifyEmailUiState.value
        if (state.email.isBlank() || state.isLoading || nowMillis() < registrationResendAvailableAt) return

        _verifyEmailUiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = authRepository.resendVerificationCode(state.email)
            if (result.isSuccess) {
                startRegistrationCountdown()
                _verifyEmailUiState.update { it.copy(verificationCode = "") }
            }
            _verifyEmailUiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = if (result.isFailure) {
                        result.exceptionOrNull()?.message ?: "驗證碼寄送失敗"
                    } else null
                )
            }
        }
    }

    private fun startRegistrationCountdown() {
        registrationResendAvailableAt = nowMillis() + VERIFICATION_RESEND_SECONDS * 1000L
        registrationCountdownJob?.cancel()
        registrationCountdownJob = startCountdown(registrationResendAvailableAt) { seconds ->
            _verifyEmailUiState.update { it.copy(resendCountdown = seconds) }
        }
    }

    // 每次用截止時間重算，App 從背景回來也會顯示正確秒數。
    private fun startCountdown(deadline: Long, updateSeconds: (Int) -> Unit): Job =
        viewModelScope.launch {
            while (true) {
                val seconds = remainingSeconds(deadline)
                updateSeconds(seconds)
                if (seconds == 0) break
                delay(500)
            }
        }

    private fun remainingSeconds(deadline: Long): Int {
        val remainingMillis = (deadline - nowMillis()).coerceAtLeast(0)
        return ((remainingMillis + 999) / 1000).toInt()
    }

    private fun sixDigitInput(code: String): String =
        code.filter { it in '0'..'9' }.take(6)

    // ==================================================
    // Forgot Password
    // ==================================================

    private val _resetPasswordUiState = MutableStateFlow(ResetPasswordUiState())
    val resetPasswordUiState = _resetPasswordUiState.asStateFlow()

    // 僅存在 ViewModel 記憶體；不放進 UiState、SavedStateHandle、導航或持久化儲存。
    private var resetToken: String? = null
    private var resetTokenExpiresAt = 0L
    private var resendAvailableAt = 0L
    private var passwordResetJob: Job? = null
    private var passwordResetCountdownJob: Job? = null

    fun onForgotPasswordEmailChange(email: String) {
        if (_forgotPasswordUiState.value.isLoading) return
        _forgotPasswordUiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun forgotPassword() {
        val state = _forgotPasswordUiState.value
        if (state.isLoading || state.step != PasswordResetStep.EMAIL) return
        _forgotPasswordUiState.update { it.copy(hasSubmitted = true) }
        if (!state.isFormValid) return
        sendResetCode(state.email.trim())
    }

    fun resendResetCode() {
        val state = _forgotPasswordUiState.value
        refreshPasswordResetCountdown()
        if (state.step != PasswordResetStep.CODE || state.isLoading || nowMillis() < resendAvailableAt) return
        sendResetCode(state.email)
    }

    private fun sendResetCode(email: String) {
        resetToken = null
        _forgotPasswordUiState.update {
            it.copy(
                email = email,
                isLoading = true,
                errorMessage = null
            )
        }
        passwordResetJob = viewModelScope.launch {
            val result = authRepository.forgotPassword(email)
            ensureActive()

            if (result.isFailure) {
                showResetCodeError(result.exceptionOrNull()?.message ?: "寄送失敗，請稍後再試")
                return@launch
            }

            resendAvailableAt = nowMillis() + VERIFICATION_RESEND_SECONDS * 1000L
            _forgotPasswordUiState.update {
                it.copy(
                    step = PasswordResetStep.CODE,
                    code = "",
                    isLoading = false,
                    resendCountdown = VERIFICATION_RESEND_SECONDS
                )
            }
            startPasswordResetCountdown()
        }
    }

    private fun startPasswordResetCountdown() {
        passwordResetCountdownJob?.cancel()
        passwordResetCountdownJob = startCountdown(resendAvailableAt) { seconds ->
            _forgotPasswordUiState.update { it.copy(resendCountdown = seconds) }
        }
    }

    fun refreshPasswordResetCountdown() {
        val seconds = remainingSeconds(resendAvailableAt)
        _forgotPasswordUiState.update { it.copy(resendCountdown = seconds) }
    }

    fun onResetCodeChange(code: String) {
        if (_forgotPasswordUiState.value.isLoading) return
        _forgotPasswordUiState.update { it.copy(code = sixDigitInput(code), errorMessage = null) }
    }

    fun verifyResetCode() {
        val state = _forgotPasswordUiState.value
        if (state.step != PasswordResetStep.CODE || state.isLoading || state.code.length != 6) return
        _forgotPasswordUiState.update { it.copy(isLoading = true, errorMessage = null) }
        passwordResetJob = viewModelScope.launch {
            val result = authRepository.verifyResetCode(state.email, state.code)
            ensureActive()
            val token = result.getOrNull()
            if (token.isNullOrBlank()) {
                val message = result.exceptionOrNull()?.message
                    ?: if (result.isSuccess) "驗證回應不完整，請重新驗證" else "驗證失敗，請重新嘗試"
                showResetCodeError(message)
                return@launch
            }

            resetToken = token
            resetTokenExpiresAt = nowMillis() + 10 * 60_000
            _resetPasswordUiState.value = ResetPasswordUiState()
            _forgotPasswordUiState.update {
                it.copy(step = PasswordResetStep.PASSWORD, code = "", isLoading = false)
            }
        }
    }

    private fun showResetCodeError(message: String) {
        _forgotPasswordUiState.update { it.copy(isLoading = false, errorMessage = message) }
    }

    fun onNewPasswordChange(value: String) {
        if (!_resetPasswordUiState.value.isLoading) _resetPasswordUiState.update {
            it.copy(
                password = value,
                errorMessage = null
            )
        }
    }

    fun onNewPasswordConfirmationChange(value: String) {
        if (!_resetPasswordUiState.value.isLoading) _resetPasswordUiState.update {
            it.copy(
                confirmPassword = value,
                errorMessage = null
            )
        }
    }

    fun toggleNewPasswordVisibility() {
        _resetPasswordUiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleNewPasswordConfirmationVisibility() {
        _resetPasswordUiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun resetPassword() {
        val state = _resetPasswordUiState.value
        if (state.isLoading || _forgotPasswordUiState.value.step != PasswordResetStep.PASSWORD) return
        val token = resetToken
        if (token == null || nowMillis() >= resetTokenExpiresAt) {
            returnToResetCode("驗證已逾時，請重新寄送驗證碼")
            return
        }
        _resetPasswordUiState.update { it.copy(hasSubmitted = true) }
        if (!state.isFormValid) return
        _resetPasswordUiState.update { it.copy(isLoading = true, errorMessage = null) }
        passwordResetJob = viewModelScope.launch {
            val result = authRepository.resetPassword(token, state.password, state.confirmPassword)
            ensureActive()
            if (result.isFailure) {
                val message = result.exceptionOrNull()?.message ?: "重設失敗，請稍後再試"
                // 只有重設憑證失效需要重新驗證，其他錯誤可以重試。
                if (message.contains("憑證")) {
                    returnToResetCode(message)
                } else {
                    _resetPasswordUiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = message
                        )
                    }
                }
                return@launch
            }

            clearResetCredentials()
            passwordResetCountdownJob?.cancel()
            _forgotPasswordUiState.value = ForgotPasswordUiState(step = PasswordResetStep.SUCCESS)
            resetLoginState()
        }
    }

    private fun clearResetCredentials() {
        resetToken = null
        resetTokenExpiresAt = 0
        _resetPasswordUiState.value = ResetPasswordUiState()
    }

    fun returnToResetCode(message: String? = null) {
        passwordResetJob?.cancel()
        clearResetCredentials()
        _forgotPasswordUiState.update {
            it.copy(
                step = PasswordResetStep.CODE,
                code = "",
                isLoading = false,
                errorMessage = message
            )
        }
        refreshPasswordResetCountdown()
    }

    // ==================================================
    // Reset State
    // ==================================================

    fun resetRegisterState() {
        _registerUiState.value = RegisterUiState()
    }

    fun resetForgotPasswordState() {
        passwordResetJob?.cancel()
        passwordResetCountdownJob?.cancel()
        clearResetCredentials()
        resendAvailableAt = 0
        _forgotPasswordUiState.value = ForgotPasswordUiState()
    }

    override fun onCleared() {
        registrationCountdownJob?.cancel()
        resetForgotPasswordState()
        super.onCleared()
    }
}

