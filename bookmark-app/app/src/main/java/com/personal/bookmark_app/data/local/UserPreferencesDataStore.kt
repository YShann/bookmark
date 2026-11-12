package com.personal.bookmark_app.data.local


import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.personal.bookmark_app.ui.model.FolderDisplayMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(
    name = "user_preferences"
)

internal class SessionTokens(val accessToken: String?, val refreshToken: String)

class UserPreferencesRepository(
    private val context: Context
) {

    companion object {
        private val FOLDER_DISPLAY_MODE =
            stringPreferencesKey("folder_display_mode")
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    // 讀取使用者上次選擇的顯示方式
    val folderDisplayMode: Flow<FolderDisplayMode> =
        context.dataStore.data.map { preferences ->

            val savedMode = preferences[FOLDER_DISPLAY_MODE]

            savedMode?.let {
                runCatching {
                    FolderDisplayMode.valueOf(it)
                }.getOrDefault(FolderDisplayMode.GRID)
            } ?: FolderDisplayMode.GRID
        }

    // 儲存使用者選擇的顯示方式
    suspend fun saveFolderDisplayMode(
        mode: FolderDisplayMode
    ) {
        context.dataStore.edit { preferences ->
            preferences[FOLDER_DISPLAY_MODE] = mode.name
        }
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    val hasSession: Flow<Boolean> = context.dataStore.data
        .map { !it[REFRESH_TOKEN].isNullOrBlank() }
        .distinctUntilChanged()

    internal suspend fun sessionTokens(): SessionTokens? {
        val preferences = context.dataStore.data.first()
        val refreshToken = preferences[REFRESH_TOKEN]?.takeIf { it.isNotBlank() } ?: return null
        return SessionTokens(preferences[ACCESS_TOKEN], refreshToken)
    }

    internal suspend fun updateAccessToken(refreshToken: String, accessToken: String): Boolean {
        val updated = context.dataStore.edit { preferences ->
            if (preferences[REFRESH_TOKEN] == refreshToken) preferences[ACCESS_TOKEN] = accessToken
        }
        return updated[REFRESH_TOKEN] == refreshToken
    }

    suspend fun clearTokens(expectedRefreshToken: String? = null): String? {
        var refreshToken: String? = null
        context.dataStore.edit { preferences ->
            if (expectedRefreshToken != null && preferences[REFRESH_TOKEN] != expectedRefreshToken) {
                return@edit
            }
            preferences.remove(ACCESS_TOKEN)
            refreshToken = preferences.remove(REFRESH_TOKEN)
        }
        return refreshToken
    }
}
