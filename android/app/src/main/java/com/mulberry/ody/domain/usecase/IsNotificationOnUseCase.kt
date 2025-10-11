package com.mulberry.ody.domain.usecase

import com.mulberry.ody.domain.model.NotificationType
import com.mulberry.ody.domain.repository.ody.SettingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsNotificationOnUseCase @Inject constructor(
    private val settingRepository: SettingRepository,
) {
    operator fun invoke(notificationType: NotificationType): Flow<Boolean> {
        return settingRepository.isNotificationOn(notificationType)
    }
}
