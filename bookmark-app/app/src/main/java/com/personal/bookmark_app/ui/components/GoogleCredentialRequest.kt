package com.personal.bookmark_app.ui.components

import android.content.Context
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.personal.bookmark_app.BuildConfig

internal suspend fun requestGoogleIdToken(context: Context): String? {
    val clientId = BuildConfig.GOOGLE_WEB_CLIENT_ID.trim()
    if (clientId.isEmpty()) {
        Toast.makeText(context, "App 尚未設定 Google 登入", Toast.LENGTH_SHORT).show()
        return null
    }
    if (!BuildConfig.API_BASE_URL.startsWith("https://", ignoreCase = true)) {
        Toast.makeText(context, "Google 登入需要 HTTPS 的後端網址", Toast.LENGTH_SHORT).show()
        return null
    }

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(GetSignInWithGoogleOption.Builder(clientId).build())
        .build()
    return try {
        val credential =
            CredentialManager.create(context).getCredential(context, request).credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            GoogleIdTokenCredential.createFrom(credential.data).idToken
        } else {
            Toast.makeText(context, "無法辨識 Google 登入憑證", Toast.LENGTH_SHORT).show()
            null
        }
    } catch (_: GetCredentialCancellationException) {
        null
    } catch (_: GetCredentialException) {
        Toast.makeText(context, "無法使用 Google 登入，請確認帳號和裝置設定", Toast.LENGTH_SHORT)
            .show()
        null
    } catch (_: GoogleIdTokenParsingException) {
        Toast.makeText(context, "無法讀取 Google 登入憑證", Toast.LENGTH_SHORT).show()
        null
    }
}
