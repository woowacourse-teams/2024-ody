package com.woowacourse.ody.presentation.notificationlog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.ody.domain.NotificationLogRepository

class NotificationLogViewModelFactory(
    private val notificationLogRepository: NotificationLogRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(NotificationLogViewModel::class.java)) {
            NotificationLogViewModel(notificationLogRepository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
