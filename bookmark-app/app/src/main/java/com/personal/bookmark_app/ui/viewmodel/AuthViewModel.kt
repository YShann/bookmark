package com.personal.bookmark_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personal.bookmark_app.data.repository.AuthRepository
import com.personal.bookmark_app.ui.screen.forgotpassword.ForgotPasswordUiState
import com.personal.bookmark_app.ui.screen.login.LoginUiState
import com.personal.bookmark_app.ui.screen.register.RegisterUiState
import com.personal.bookmark_app.ui.screen.verifyemail.VerifyEmailUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

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

    private var countdownJob: Job? = null

    // ==================================================
    // Login
    // ==================================================

    fun onLoginEmailChange(email: String) {
        _loginUiState.update {
            it.copy(email = email, errorMessage = null)
        }
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
        if (!state.isFormValid) return

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
                        resendCountdown = 45,
                        errorMessage = null
                    )
                }

                startResendCountdown()

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
                verificationCode = code.filter(Char::isDigit).take(6),
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
                countdownJob?.cancel()
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

        if (!state.canResend) return

        viewModelScope.launch {
            val result = authRepository.resendVerificationCode(
                email = state.email
            )

            result.onSuccess {
                _verifyEmailUiState.update {
                    it.copy(
                        resendCountdown = 45,
                        errorMessage = null
                    )
                }

                startResendCountdown()
            }.onFailure { error ->
                _verifyEmailUiState.update {
                    it.copy(
                        errorMessage = error.message ?: "驗證碼寄送失敗"
                    )
                }
            }
        }
    }

    private fun startResendCountdown() {
        countdownJob?.cancel()

        countdownJob = viewModelScope.launch {
            while (_verifyEmailUiState.value.resendCountdown > 0) {
                delay(1000)

                _verifyEmailUiState.update {
                    it.copy(
                        resendCountdown = (it.resendCountdown - 1).coerceAtLeast(0)
                    )
                }
            }
        }
    }

    // ==================================================
    // Forgot Password
    // ==================================================

    fun onForgotPasswordEmailChange(email: String) {
        _forgotPasswordUiState.update {
            it.copy(
                email = email,
                errorMessage = null
            )
        }
    }

    fun sendPasswordResetLink() {
        // 點擊發送後，開始顯示欄位錯誤
        _forgotPasswordUiState.update {
            it.copy(
                hasSubmitted = true,
                errorMessage = null
            )
        }

        // 取得最新表單狀態
        val state = _forgotPasswordUiState.value

        // Email 格式不正確就停止發送
        if (!state.isFormValid) return

        viewModelScope.launch {
            _forgotPasswordUiState.update {
                it.copy(isLoading = true)
            }

            val result = authRepository.sendPasswordResetLink(
                email = state.email
            )

            _forgotPasswordUiState.update {
                it.copy(isLoading = false)
            }

            result.onSuccess {
                _authEvent.emit(AuthEvent.PasswordResetLinkSent)
            }.onFailure { error ->
                _forgotPasswordUiState.update {
                    it.copy(
                        errorMessage = error.message
                            ?: "寄送失敗，請稍後再試"
                    )
                }
            }
        }
    }

    // ==================================================
    // Reset State
    // ==================================================

    fun resetRegisterState() {
        _registerUiState.value = RegisterUiState()
    }

    fun resetForgotPasswordState() {
        _forgotPasswordUiState.value = ForgotPasswordUiState()
    }

    override fun onCleared() {
        countdownJob?.cancel()
        super.onCleared()
    }
}

