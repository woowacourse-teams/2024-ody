package com.mulberry.ody.domain.usecase

import com.mulberry.ody.domain.model.NotificationType
import com.mulberry.ody.domain.repository.ody.SettingRepository
import javax.inject.Inject

class UpdateNotificationSetting @Inject constructor(
    private val settingRepository: SettingRepository,
) {
    suspend operator fun invoke(notificationType: NotificationType, isNotificationOn: Boolean) {
        settingRepository.changeNotificationSetting(notificationType, isNotificationOn)
    }
}
