package com.woowacourse.ody.data.local.db

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OdyDatastore(private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = ODY_KEY)

    suspend fun setInviteCode(inviteCode: String) {
        context.dataStore.edit {
            it[INVITE_CODE] = inviteCode
        }
    }

    fun getInviteCode(): Flow<Result<String>> {
        return context.dataStore.data.map { preferences ->
            preferences[INVITE_CODE].let {
                if (!it.isNullOrEmpty()) {
                    Result.success(it)
                } else {
                    Result.failure(Exception("Invite code is empty"))
                }
            }
        }
    }

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
                    Result.failure(Exception("FCM Token is empty"))
                }
            }
        }
    }

    companion object {
        private const val ODY_KEY = "ody_key"
        private val INVITE_CODE = stringPreferencesKey("inviteCode")
        private val FCM_TOKEN = stringPreferencesKey("fcmToken")
    }
}
