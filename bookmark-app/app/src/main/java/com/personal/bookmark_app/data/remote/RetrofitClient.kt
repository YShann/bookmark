package com.personal.bookmark_app.data.remote

import android.util.Base64

import com.personal.bookmark_app.BuildConfig
import com.personal.bookmark_app.data.local.SessionTokens
import com.personal.bookmark_app.data.local.UserPreferencesRepository
import com.personal.bookmark_app.data.remote.api.AuthApi
import com.personal.bookmark_app.data.remote.dto.request.auth.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitClient @Inject constructor(
    private val preferences: UserPreferencesRepository,
    private val authApi: AuthApi
) {
    private val refreshMutex = Mutex()
    private val random = SecureRandom()

    fun authenticated(): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val session = runBlocking { preferences.sessionTokens() }
                val request = chain.request().newBuilder().apply {
                    tag(SessionTokens::class.java, session)
                    session?.accessToken?.let { header("Authorization", "Bearer $it") }
                }.build()
                chain.proceed(request)
            }
            .authenticator { _, response ->
                val failedSession = response.request.tag(SessionTokens::class.java)
                    ?: return@authenticator null
                val token = runBlocking { refreshAfterUnauthorized(preferences, failedSession) }
                    ?: return@authenticator null
                response.request.newBuilder()
                    .tag(SessionTokens::class.java, null)
                    .header("Authorization", "Bearer $token")
                    .build()
            }
            .build()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private suspend fun refreshAfterUnauthorized(
        preferences: UserPreferencesRepository,
        failedSession: SessionTokens
    ): String? = refreshMutex.withLock {
        val current = preferences.sessionTokens() ?: return@withLock null
        if (current.sessionId != failedSession.sessionId) return@withLock null
        if (current.refreshToken != failedSession.refreshToken ||
            current.accessToken != failedSession.accessToken
        ) return@withLock current.accessToken

        val requestId = preferences.beginRefresh(current, Base64.encodeToString(
            ByteArray(32).also(random::nextBytes),
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )) ?: return@withLock null

        val renewed = try {
            try {
                authApi.refreshSession(requestId, RefreshTokenRequest(current.refreshToken))
            } catch (_: IOException) {
                authApi.refreshSession(requestId, RefreshTokenRequest(current.refreshToken))
            }
        } catch (error: HttpException) {
            val authAction = error.response()?.headers()?.get("X-Bookmark-Auth-Action")
            if (error.code() == 401 && authAction == "sign-in") {
                preferences.clearTokens(current.refreshToken)
            }
            return@withLock null
        }

        renewed.accessToken.takeIf {
            preferences.finishRefresh(current, requestId, it, renewed.refreshToken)
        }
    }
}
