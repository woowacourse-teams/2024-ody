package com.mulberry.ody.data.local.service

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class LongTermJobUUIDStore
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) {
        suspend fun setJobUUID(
            workId: Long,
            uuid: String,
        ) {
            dataStore.edit {
                it[stringPreferencesKey(workId.toString())] = uuid
            }
        }

        fun getJobUUID(workId: Long): Flow<UUID?> =
            dataStore.data.map { preferences ->
                val key = stringPreferencesKey(workId.toString())
                val uuid =
                    if (preferences.contains(key)) {
                        UUID.fromString(preferences[key])
                    } else {
                        null
                    }
                uuid
            }

        suspend fun removeJobUUID(workId: Long) {
            dataStore.edit {
                it.remove(stringPreferencesKey(workId.toString()))
            }
        }
    }
