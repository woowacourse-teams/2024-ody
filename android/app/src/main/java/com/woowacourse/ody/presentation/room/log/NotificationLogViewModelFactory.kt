package com.woowacourse.ody.presentation.room.log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.firebase.analytics.FirebaseAnalytics
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import com.woowacourse.ody.domain.repository.ody.NotificationLogRepository
import com.woowacourse.ody.presentation.common.analytics.AnalyticsHelper

class NotificationLogViewModelFactory(
    private val analyticsHelper: AnalyticsHelper,
    private val notificationLogRepository: NotificationLogRepository,
    private val meetingRepository: MeetingRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras,
    ): T {
        return if (modelClass.isAssignableFrom(NotificationLogViewModel::class.java)) {
            NotificationLogViewModel(
                analyticsHelper,
                extras.createSavedStateHandle(),
                notificationLogRepository,
                meetingRepository,
            ) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
