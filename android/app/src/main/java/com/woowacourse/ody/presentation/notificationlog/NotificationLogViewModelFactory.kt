package com.woowacourse.ody.presentation.notificationlog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.ody.domain.NotificationLogRepository
import com.woowacourse.ody.domain.repository.MeetingRepository

class NotificationLogViewModelFactory(
    private val notificationLogRepository: NotificationLogRepository,
    private val meetingRepository: MeetingRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(NotificationLogViewModel::class.java)) {
            NotificationLogViewModel(
                notificationLogRepository,
                meetingRepository,
            ) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
