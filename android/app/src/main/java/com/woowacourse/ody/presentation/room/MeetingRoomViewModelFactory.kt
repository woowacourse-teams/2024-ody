package com.woowacourse.ody.presentation.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.woowacourse.ody.domain.repository.ody.MatesEtaRepository
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import com.woowacourse.ody.domain.repository.ody.NotificationLogRepository
import com.woowacourse.ody.presentation.common.analytics.AnalyticsHelper
import com.woowacourse.ody.presentation.common.image.ImageShareHelper
import com.woowacourse.ody.presentation.common.image.ImageStorage

class MeetingRoomViewModelFactory(
    private val analyticsHelper: AnalyticsHelper,
    private val meetingId: Long,
    private val matesEtaRepository: MatesEtaRepository,
    private val notificationLogRepository: NotificationLogRepository,
    private val meetingRepository: MeetingRepository,
    private val imageStorage: ImageStorage,
    private val imageShareHelper: ImageShareHelper,
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
                imageStorage,
                imageShareHelper,
            ) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
