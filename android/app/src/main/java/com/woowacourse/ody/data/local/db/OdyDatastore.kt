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

    fun getInviteCode(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[INVITE_CODE] ?: ""
        }
    }

    suspend fun setToken(token: String) {
        context.dataStore.edit {
            it[TOKEN] = token
        }
    }

    fun getToken(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[TOKEN] ?: ""
        }
    }

    companion object {
        private const val ODY_KEY = "ody_key"
        private val INVITE_CODE = stringPreferencesKey("inviteCode")
        private val TOKEN = stringPreferencesKey("token")

    }
}
