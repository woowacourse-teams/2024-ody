package com.woowacourse.ody.presentation.notificationlog

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.domain.repository.MeetingRepository
import com.woowacourse.ody.domain.repository.NotificationLogRepository
import com.woowacourse.ody.presentation.notificationlog.uimodel.MeetingUiModel
import com.woowacourse.ody.presentation.notificationlog.uimodel.NotificationLogUiModel
import com.woowacourse.ody.presentation.notificationlog.uimodel.toMeetingUiModel
import com.woowacourse.ody.presentation.notificationlog.uimodel.toNotificationUiModels
import kotlinx.coroutines.launch

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
                meeting.onSuccess {
                    Log.e("TEST", "onSuccess")
                    _meeting.postValue(it.first().toMeetingUiModel())
                    fetchNotificationLogs(it.first().id)
                }.onFailure {
                    Log.e("TEST", "onFailure dddd ${it.message}")
                }
            }
        }

    fun initialize() {
        fetchMeeting()
    }
}
