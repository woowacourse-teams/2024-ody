package com.woowacourse.ody.presentation.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.woowacourse.ody.domain.repository.ody.MatesEtaRepository
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import com.woowacourse.ody.domain.repository.ody.NotificationLogRepository
import com.woowacourse.ody.presentation.common.analytics.AnalyticsHelper

class MeetingRoomViewModelFactory(
    private val analyticsHelper: AnalyticsHelper,
    private val meetingId: Long,
    private val matesEtaRepository: MatesEtaRepository,
    private val notificationLogRepository: NotificationLogRepository,
    private val meetingRepository: MeetingRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras,
    ): T {
        return if (modelClass.isAssignableFrom(MeetingRoomViewModel::class.java)) {
            MeetingRoomViewModel(
                analyticsHelper,
                meetingId,
                matesEtaRepository,
                notificationLogRepository,
                meetingRepository,
            ) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
