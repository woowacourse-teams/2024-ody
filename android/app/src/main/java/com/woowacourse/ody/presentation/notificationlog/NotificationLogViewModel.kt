package com.woowacourse.ody.presentation.notificationlog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import com.woowacourse.ody.domain.repository.ody.NotificationLogRepository
import com.woowacourse.ody.presentation.notificationlog.uimodel.MeetingUiModel
import com.woowacourse.ody.presentation.notificationlog.uimodel.NotificationLogUiModel
import com.woowacourse.ody.presentation.notificationlog.uimodel.toMeetingUiModel
import com.woowacourse.ody.presentation.notificationlog.uimodel.toNotificationUiModels
import kotlinx.coroutines.launch
import timber.log.Timber

class NotificationLogViewModel(
    private val notificationLogRepository: NotificationLogRepository,
    private val meetingRepository: MeetingRepository,
) : ViewModel() {
    private val _meeting = MutableLiveData<MeetingUiModel>()
    val meeting: LiveData<MeetingUiModel> = _meeting

    private val _notificationLogs = MutableLiveData<List<NotificationLogUiModel>>()
    val notificationLogs: LiveData<List<NotificationLogUiModel>> = _notificationLogs

    private fun fetchNotificationLogs(meetingId: Long) =
        viewModelScope.launch {
            notificationLogRepository.fetchNotificationLogs(meetingId).let { notificationLogs ->
                _notificationLogs.postValue(notificationLogs.toNotificationUiModels())
            }
        }

    private fun fetchMeeting() =
        viewModelScope.launch {
            meetingRepository.fetchMeeting().let { meeting ->
                meeting
                    .onSuccess {
                        _meeting.postValue(it.first().toMeetingUiModel())
                        fetchNotificationLogs(it.first().id)
                    }.onFailure {
                        Timber.e(it.message.toString())
                    }
            }
        }

    fun initialize() {
        fetchMeeting()
    }
}
