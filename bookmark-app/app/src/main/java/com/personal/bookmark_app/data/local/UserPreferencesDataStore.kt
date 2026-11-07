package com.personal.bookmark_app.data.local


import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.personal.bookmark_app.ui.model.FolderDisplayMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "user_preferences"
)

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

    suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
        }
    }
}
