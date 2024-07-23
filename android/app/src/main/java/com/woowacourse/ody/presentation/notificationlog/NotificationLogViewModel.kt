package com.woowacourse.ody.presentation.notificationlog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.domain.NotificationLogRepository
import com.woowacourse.ody.presentation.notificationlog.uimodel.NotificationLogUiModel
import com.woowacourse.ody.presentation.notificationlog.uimodel.toNotificationUiModels
import kotlinx.coroutines.launch

class NotificationLogViewModel(
    private val notificationLogRepository: NotificationLogRepository,
) : ViewModel() {
    private val _notificationLogs = MutableLiveData<List<NotificationLogUiModel>>()
    val notificationLogs: LiveData<List<NotificationLogUiModel>> = _notificationLogs

    fun fetchNotificationLogs() =
        viewModelScope.launch {
            notificationLogRepository.getNotificationLogs().let { notificationLogs ->
                _notificationLogs.postValue(notificationLogs.toNotificationUiModels())
            }
        }
}
