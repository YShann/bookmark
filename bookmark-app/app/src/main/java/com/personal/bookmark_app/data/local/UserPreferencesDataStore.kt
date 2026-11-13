package com.personal.bookmark_app.data.local


import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.personal.bookmark_app.ui.model.FolderDisplayMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(
    name = "user_preferences"
)

private val ACCESS_TOKEN = stringPreferencesKey("access_token")
private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
private val SESSION_ID = stringPreferencesKey("local_session_id")
private val REFRESH_REQUEST_ID = stringPreferencesKey("refresh_request_id")
private var sessionStore: DataStore<Preferences>? = null

@Synchronized
private fun sessionDataStore(context: Context): DataStore<Preferences> {
    sessionStore?.let { return it }
    val appContext = context.applicationContext
    return PreferenceDataStoreFactory.create(
        migrations = listOf(object : DataMigration<Preferences> {
            override suspend fun shouldMigrate(currentData: Preferences): Boolean {
                val old = appContext.dataStore.data.first()
                return old.contains(ACCESS_TOKEN) || old.contains(REFRESH_TOKEN)
            }

            // Credentials in the old file may have come from a previous installation.
            override suspend fun migrate(currentData: Preferences): Preferences = currentData

            override suspend fun cleanUp() {
                appContext.dataStore.edit {
                    it.remove(ACCESS_TOKEN)
                    it.remove(REFRESH_TOKEN)
                }
            }
        }, object : DataMigration<Preferences> {
            override suspend fun shouldMigrate(currentData: Preferences): Boolean =
                REFRESH_TOKEN in currentData && SESSION_ID !in currentData

            override suspend fun migrate(currentData: Preferences): Preferences =
                currentData.toMutablePreferences().apply { this[SESSION_ID] = UUID.randomUUID().toString() }

            override suspend fun cleanUp() = Unit
        }),
        produceFile = { appContext.noBackupFilesDir.resolve("session.preferences_pb") }
    ).also { sessionStore = it }
}

internal class SessionTokens(val sessionId: String, val accessToken: String?, val refreshToken: String)

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private val FOLDER_DISPLAY_MODE =
            stringPreferencesKey("folder_display_mode")
    }

    private val session = sessionDataStore(context)

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
        session.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken
            preferences[SESSION_ID] = UUID.randomUUID().toString()
            preferences.remove(REFRESH_REQUEST_ID)
        }
    }

    val hasSession: Flow<Boolean> = session.data
        .map { !it[REFRESH_TOKEN].isNullOrBlank() }
        .distinctUntilChanged()

    internal suspend fun sessionTokens(): SessionTokens? {
        val preferences = session.data.first()
        val refreshToken = preferences[REFRESH_TOKEN]?.takeIf { it.isNotBlank() } ?: return null
        val sessionId = preferences[SESSION_ID] ?: return null
        return SessionTokens(sessionId, preferences[ACCESS_TOKEN], refreshToken)
    }

    internal suspend fun beginRefresh(expected: SessionTokens, requestId: String): String? {
        val updated = session.edit { preferences ->
            if (preferences[SESSION_ID] == expected.sessionId &&
                preferences[REFRESH_TOKEN] == expected.refreshToken &&
                REFRESH_REQUEST_ID !in preferences
            ) {
                preferences[REFRESH_REQUEST_ID] = requestId
            }
        }
        return updated[REFRESH_REQUEST_ID].takeIf {
            updated[SESSION_ID] == expected.sessionId && updated[REFRESH_TOKEN] == expected.refreshToken
        }
    }

    internal suspend fun finishRefresh(
        expected: SessionTokens, requestId: String, accessToken: String, refreshToken: String
    ): Boolean {
        var applied = false
        session.edit { preferences ->
            if (preferences[SESSION_ID] == expected.sessionId &&
                preferences[REFRESH_TOKEN] == expected.refreshToken &&
                preferences[REFRESH_REQUEST_ID] == requestId
            ) {
                preferences[ACCESS_TOKEN] = accessToken
                preferences[REFRESH_TOKEN] = refreshToken
                preferences.remove(REFRESH_REQUEST_ID)
                applied = true
            }
        }
        return applied
    }

    suspend fun clearTokens(expectedRefreshToken: String? = null): String? {
        var refreshToken: String? = null
        session.edit { preferences ->
            if (expectedRefreshToken != null && preferences[REFRESH_TOKEN] != expectedRefreshToken) {
                return@edit
            }
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(SESSION_ID)
            preferences.remove(REFRESH_REQUEST_ID)
            refreshToken = preferences.remove(REFRESH_TOKEN)
        }
        return refreshToken
    }
}
