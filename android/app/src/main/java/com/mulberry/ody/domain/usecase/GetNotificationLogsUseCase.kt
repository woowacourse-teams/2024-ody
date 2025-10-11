package com.mulberry.ody.domain.usecase

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.NotificationLog
import com.mulberry.ody.domain.repository.ody.NotificationLogRepository
import javax.inject.Inject

class GetNotificationLogsUseCase @Inject constructor(
    private val notificationLogRepository: NotificationLogRepository,
) {
    suspend operator fun invoke(meetingId: Long): ApiResult<List<NotificationLog>> {
        return notificationLogRepository.fetchNotificationLogs(meetingId)
    }
}
