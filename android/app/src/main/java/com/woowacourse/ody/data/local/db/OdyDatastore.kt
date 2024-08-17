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

    suspend fun setToken(token: String) {
        context.dataStore.edit {
            it[TOKEN] = token
        }
    }

    fun getToken(): Flow<Result<String>> {
        return context.dataStore.data.map { preferences ->
            preferences[TOKEN].let {
                if (!it.isNullOrEmpty()) {
                    Result.success(it)
                } else {
                    Result.failure(Exception())
                }
            }
        }
    }

    companion object {
        private const val ODY_KEY = "ody_key"
        private val TOKEN = stringPreferencesKey("token")
    }
}
