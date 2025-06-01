package com.mulberry.ody.data.repository

import com.mulberry.ody.data.local.db.OdyDataStore
import com.mulberry.ody.domain.model.NotificationType
import com.mulberry.ody.domain.repository.ody.SettingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultSettingRepository
    @Inject
    constructor(
        private val odyDataStore: OdyDataStore,
    ) : SettingRepository {
        override fun isNotificationOn(notificationType: NotificationType): Flow<Boolean> {
            return odyDataStore.getIsNotificationOn(notificationType)
        }

        override suspend fun changeNotificationSetting(
            notificationType: NotificationType,
            isNotificationOn: Boolean,
        ) {
            odyDataStore.setIsNotificationOn(notificationType, isNotificationOn)
        }
    }
