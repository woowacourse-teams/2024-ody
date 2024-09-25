package com.mulberry.ody.data.local.db

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mulberry.ody.domain.model.AuthToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class OdyDatastore(private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = ODY_KEY)

    suspend fun setFCMToken(token: String) {
        context.dataStore.edit {
            it[FCM_TOKEN] = token
        }
    }

    fun getFCMToken(): Flow<Result<String>> {
        return context.dataStore.data.map { preferences ->
            preferences[FCM_TOKEN].let {
                if (!it.isNullOrEmpty()) {
                    Result.success(it)
                } else {
                    Result.failure(Exception("FCM 토큰이 null입니다"))
                }
            }
        }
    }

    suspend fun setAuthToken(token: AuthToken) {
        context.dataStore.edit {
            it[ACCESS_TOKEN] = token.accessToken
            it[REFRESH_TOKEN] = token.refreshToken
        }
    }

    fun getAuthToken(): Flow<Result<AuthToken>> {
        return context.dataStore.data.map { preferences ->
            val accessToken = preferences[ACCESS_TOKEN]
            val refreshToken = preferences[REFRESH_TOKEN]
            if (!accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()) {
                Result.success(AuthToken(accessToken, refreshToken))
            } else {
                Result.failure(Exception("accessToken 또는 refreshToken이 null입니다"))
            }
        }
    }

    suspend fun removeAuthToken() {
        context.dataStore.edit {
            it.remove(ACCESS_TOKEN)
            it.remove(REFRESH_TOKEN)
        }
    }

    suspend fun setMeetingJobUUID(
        meetingId: Long,
        uuid: String,
    ) {
        context.dataStore.edit {
            it[stringPreferencesKey(meetingId.toString())] = uuid
        }
    }

    fun getMeetingJobUUID(meetingId: Long): Flow<UUID?> =
        context.dataStore.data.map { preferences ->
            val key = stringPreferencesKey(meetingId.toString())
            val uuid =
                if (preferences.contains(key)) {
                    UUID.fromString(preferences[key])
                } else {
                    null
                }
            uuid
        }

    suspend fun removeMeetingJobUUID(meetingId: Long) {
        context.dataStore.edit {
            it.remove(stringPreferencesKey(meetingId.toString()))
        }
    }

    companion object {
        private const val ODY_KEY = "ody_key"
        private val FCM_TOKEN = stringPreferencesKey("fcmToken")
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }
}
