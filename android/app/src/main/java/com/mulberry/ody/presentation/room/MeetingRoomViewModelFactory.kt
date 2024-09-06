package com.mulberry.ody.presentation.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.mulberry.ody.domain.repository.image.ImageStorage
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.domain.repository.ody.NotificationLogRepository
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.image.ImageShareHelper

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
