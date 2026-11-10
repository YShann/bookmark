package com.personal.bookmark_app.ui

import com.personal.bookmark_app.data.repository.auth.AuthRepository
import com.personal.bookmark_app.ui.viewmodel.AuthViewModel
import com.personal.bookmark_app.ui.screen.forgotpassword.PasswordResetStep
import com.personal.bookmark_app.ui.screen.resetpassword.ResetPasswordUiState
import kotlinx.coroutines.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Test

class PasswordResetViewModelTest {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
    private val repo = FakeAuthRepository()
    private var now = 1000L
    private val vm = AuthViewModel(repo, { now }, scope)
    @After fun cleanup() { scope.cancel() }

    private fun send() { vm.onForgotPasswordEmailChange(" user@example.com "); vm.forgotPassword() }
    private fun verify() { send(); vm.onResetCodeChange("012345"); vm.verifyResetCode() }
    private fun passwords() { vm.onNewPasswordChange("NewPass123"); vm.onNewPasswordConfirmationChange("NewPass123") }

    @Test fun successfulFlowClearsSecretsAndDoesNotLogin() {
        verify(); assertEquals(PasswordResetStep.PASSWORD, vm.forgotPasswordUiState.value.step)
        assertEquals("012345", repo.lastCode)
        passwords(); vm.resetPassword()
        assertEquals("opaque-token", repo.usedToken)
        assertEquals(PasswordResetStep.SUCCESS, vm.forgotPasswordUiState.value.step)
        assertEquals("", vm.resetPasswordUiState.value.password)
        assertEquals("", vm.resetPasswordUiState.value.confirmPassword)
        assertEquals("", vm.forgotPasswordUiState.value.code)
        assertEquals(0, repo.logins)
    }
    @Test fun duplicateRequestsAreBlockedBeforeCoroutineCompletes() {
        repo.sendGate = CompletableDeferred()
        send(); vm.forgotPassword(); assertEquals(1, repo.sends)
        assertTrue(vm.forgotPasswordUiState.value.isLoading)
        repo.sendGate!!.complete(Unit)
        repo.verifyGate = CompletableDeferred()
        vm.onResetCodeChange("012345"); vm.verifyResetCode(); vm.verifyResetCode()
        assertEquals(1, repo.verifications)
        repo.verifyGate!!.complete(Unit)
        repo.resetGate = CompletableDeferred()
        passwords(); vm.resetPassword(); vm.resetPassword(); assertEquals(1, repo.resets)
        repo.resetGate!!.complete(Unit)
    }
    @Test fun resendUsesSixtySecondDeadlineAndSeparateRegistrationState() {
        send(); val registration = vm.verifyEmailUiState.value
        vm.resendResetCode(); assertEquals(1, repo.sends)
        now += 59_000; vm.refreshPasswordResetCountdown(); assertEquals(1, vm.forgotPasswordUiState.value.resendCountdown)
        now += 1_000; vm.refreshPasswordResetCountdown(); vm.resendResetCode()
        assertEquals(2, repo.sends); assertEquals(60, vm.forgotPasswordUiState.value.resendCountdown)
        assertEquals(registration, vm.verifyEmailUiState.value)
    }
    @Test fun registrationResendWaitsForSixtySecondsAndRestartsCountdown() {
        vm.onRegisterEmailChange("user@example.com")
        vm.onRegisterPasswordChange("NewPass123")
        vm.onConfirmPasswordChange("NewPass123")
        vm.register()
        assertEquals(60, vm.verifyEmailUiState.value.resendCountdown)

        now += 59_999
        vm.resendVerificationCode()
        assertEquals(0, repo.registrationResends)

        now += 1
        vm.resendVerificationCode()
        assertEquals(1, repo.registrationResends)
        assertEquals(60, vm.verifyEmailUiState.value.resendCountdown)
        assertEquals(0, repo.sends)
    }
    @Test fun leavingCancelsLateResponseAndClearsState() {
        repo.sendGate = CompletableDeferred(); send()
        vm.resetForgotPasswordState(); repo.sendGate!!.complete(Unit)
        assertEquals(PasswordResetStep.EMAIL, vm.forgotPasswordUiState.value.step)
        assertEquals("", vm.forgotPasswordUiState.value.email)
        assertFalse(vm.forgotPasswordUiState.value.isLoading)
    }
    @Test fun expiredTokenRequiresNewVerificationWithoutSendingPassword() {
        verify(); passwords(); now += 600_000; vm.resetPassword()
        assertEquals(PasswordResetStep.CODE, vm.forgotPasswordUiState.value.step)
        assertEquals("", vm.resetPasswordUiState.value.password)
        assertEquals(0, repo.resets)
    }
    @Test fun recreatedViewModelCannotResetWithoutVerification() {
        verify(); passwords()
        val recreated = AuthViewModel(repo, { now }, scope)
        recreated.onNewPasswordChange("NewPass123"); recreated.onNewPasswordConfirmationChange("NewPass123"); recreated.resetPassword()
        assertEquals(PasswordResetStep.EMAIL, recreated.forgotPasswordUiState.value.step)
        assertEquals(0, repo.resets)
    }
    @Test fun invalidPasswordAndMismatchNeverCallApi() {
        verify(); vm.onNewPasswordChange("中文字密碼1234"); vm.onNewPasswordConfirmationChange("中文字密碼1234"); vm.resetPassword()
        assertNotNull(vm.resetPasswordUiState.value.passwordError)
        passwords(); vm.onNewPasswordConfirmationChange("different"); vm.resetPassword()
        assertNotNull(vm.resetPasswordUiState.value.confirmPasswordError); assertEquals(0, repo.resets)
        assertFalse(ResetPasswordUiState(password="secret").toString().contains("secret"))
    }
    @Test fun verificationErrorKeepsCodeScreenAndShowsBackendMessage() {
        send(); repo.verifyResult = Result.failure(IllegalStateException("驗證碼已過期"))
        vm.onResetCodeChange("012345"); vm.verifyResetCode()
        assertEquals(PasswordResetStep.CODE, vm.forgotPasswordUiState.value.step)
        assertEquals("驗證碼已過期", vm.forgotPasswordUiState.value.errorMessage)
        assertFalse(vm.forgotPasswordUiState.value.isLoading)
    }
    @Test fun returningToCodeDiscardsTokenAndPassword() {
        verify(); passwords(); vm.returnToResetCode(); vm.resetPassword()
        assertEquals(PasswordResetStep.CODE, vm.forgotPasswordUiState.value.step)
        assertEquals("", vm.resetPasswordUiState.value.password); assertEquals(0, repo.resets)
    }
    @Test fun resetServerFailureAllowsRetryButInvalidTokenRequiresVerification() {
        verify(); passwords(); repo.resetResult=Result.failure(IllegalStateException("連線失敗")); vm.resetPassword()
        assertEquals(PasswordResetStep.PASSWORD, vm.forgotPasswordUiState.value.step)
        assertFalse(vm.resetPasswordUiState.value.isLoading)
        repo.resetResult=Result.failure(IllegalStateException("重設密碼憑證無效或已過期，請重新驗證")); vm.resetPassword()
        assertEquals(PasswordResetStep.CODE, vm.forgotPasswordUiState.value.step)
        assertEquals("", vm.resetPasswordUiState.value.password)
    }

    private class FakeAuthRepository : AuthRepository {
        var sends=0; var verifications=0; var resets=0; var logins=0
        var registrationResends = 0
        var lastCode=""; var usedToken=""
        var sendGate: CompletableDeferred<Unit>?=null
        var verifyGate: CompletableDeferred<Unit>?=null
        var resetGate: CompletableDeferred<Unit>?=null
        var verifyResult=Result.success("opaque-token")
        var resetResult=Result.success(Unit)
        override suspend fun forgotPassword(email:String):Result<Unit> { sends++; sendGate?.await(); return Result.success(Unit) }
        override suspend fun verifyResetCode(email:String,code:String):Result<String> { verifications++; lastCode=code; verifyGate?.await(); return verifyResult }
        override suspend fun resetPassword(resetToken:String,newPassword:String,confirmPassword:String):Result<Unit> { resets++; usedToken=resetToken; resetGate?.await(); return resetResult }
        override suspend fun login(email:String,password:String):Result<Unit> { logins++; return Result.success(Unit) }
        override suspend fun register(email:String,password:String)=Result.success(Unit)
        override suspend fun verifyEmail(email:String,code:String)=Result.success(Unit)
        override suspend fun resendVerificationCode(email: String): Result<Unit> {
            registrationResends++
            return Result.success(Unit)
        }
        override suspend fun logout()=Result.success(Unit)
    }
}
