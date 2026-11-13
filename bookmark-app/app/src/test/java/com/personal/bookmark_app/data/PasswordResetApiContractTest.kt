package com.personal.bookmark_app.data

import com.google.gson.Gson
import com.personal.bookmark_app.data.remote.api.AuthApi
import com.personal.bookmark_app.data.remote.dto.request.auth.*
import com.personal.bookmark_app.data.remote.dto.response.auth.VerifyResetCodeResponse
import org.junit.Assert.*
import org.junit.Test
import retrofit2.http.POST

class PasswordResetApiContractTest {
    @Test
    fun endpointsMatchBackend() {
        mapOf(
            "forgotPassword" to "forgot-password",
            "verifyResetCode" to "verify-reset-code",
            "resetPassword" to "reset-password"
        ).forEach { (method, path) ->
            assertEquals(
                "api/auth/$path",
                requireNotNull(AuthApi::class.java.methods.single { it.name == method }
                    .getAnnotation(POST::class.java)).value
            )
        }
    }

    @Test
    fun jsonPreservesLeadingZeroAndPasswordFieldNames() {
        val gson = Gson()
        assertEquals(
            "012345",
            gson.toJsonTree(
                VerifyResetCodeRequest(
                    "user@example.com",
                    "012345"
                )
            ).asJsonObject["code"].asString
        )
        val request = ResetPasswordRequest("opaque-token", "NewPass123", "NewPass123")
        val json = gson.toJsonTree(request).asJsonObject
        assertEquals(setOf("resetToken", "newPassword", "confirmPassword"), json.keySet())
        assertEquals(
            "opaque-token",
            gson.fromJson(
                """{"resetToken":"opaque-token"}""",
                VerifyResetCodeResponse::class.java
            ).resetToken
        )
        assertFalse(request.toString().contains("NewPass123"))
    }
}
