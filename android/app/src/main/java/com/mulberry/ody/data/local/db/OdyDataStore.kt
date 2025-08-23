package com.mulberry.ody.data.local.db

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mulberry.ody.domain.model.AuthToken
import com.mulberry.ody.domain.model.NotificationType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class OdyDataStore(private val context: Context) {
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

    suspend fun updateEtaDashboardGuideSeen() {
        context.dataStore.edit {
            it[IS_FIRST_SEEN_ETA_DASHBOARD] = false
        }
    }

    fun getIsFirstSeenEtaDashboard(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[IS_FIRST_SEEN_ETA_DASHBOARD] ?: true
        }
    }

    suspend fun setIsNotificationOn(
        notificationType: NotificationType,
        isNotificationOn: Boolean,
    ) {
        val preferencesKey = notificationType.toPreferencesKey() ?: return
        context.dataStore.edit {
            it[preferencesKey] = isNotificationOn
        }
    }

    fun getIsNotificationOn(notificationType: NotificationType): Flow<Boolean> {
        val preferencesKey = notificationType.toPreferencesKey() ?: return flow { emit(false) }
        return context.dataStore.data.map { preferences ->
            preferences[preferencesKey] ?: true
        }
    }

    private fun NotificationType.toPreferencesKey(): Preferences.Key<Boolean>? {
        return when (this) {
            NotificationType.ENTRY -> IS_NOTIFICATION_ENTRY_ON
            NotificationType.DEPARTURE_REMINDER -> IS_NOTIFICATION_DEPARTURE_ON
            NotificationType.NUDGE, NotificationType.ETA_NOTICE -> null
        }
    }

    companion object {
        private const val ODY_KEY = "ody_key"
        private val FCM_TOKEN = stringPreferencesKey("fcmToken")
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val IS_FIRST_SEEN_ETA_DASHBOARD = booleanPreferencesKey("is_first_seen_eta_dashboard")
        private val IS_NOTIFICATION_DEPARTURE_ON = booleanPreferencesKey("is_notification_departure_on")
        private val IS_NOTIFICATION_ENTRY_ON = booleanPreferencesKey("is_notification_entry_on")
    }
}
