package com.mulberry.ody.domain.repository.ody

import com.mulberry.ody.domain.model.NotificationType
import kotlinx.coroutines.flow.Flow

interface SettingRepository {
    fun isNotificationOn(notificationType: NotificationType): Flow<Boolean>

    suspend fun changeNotificationSetting(
        notificationType: NotificationType,
        isNotificationOn: Boolean,
    )
}
