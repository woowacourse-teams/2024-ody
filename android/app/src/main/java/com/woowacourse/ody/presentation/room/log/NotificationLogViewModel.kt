package com.woowacourse.ody.presentation.room.log

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import com.woowacourse.ody.domain.repository.ody.NotificationLogRepository
import com.woowacourse.ody.presentation.room.log.model.MeetingUiModel
import com.woowacourse.ody.presentation.room.log.model.NotificationLogUiModel
import com.woowacourse.ody.presentation.room.log.model.toNotificationUiModels
import kotlinx.coroutines.launch
import timber.log.Timber

class NotificationLogViewModel(
    private val notificationLogRepository: NotificationLogRepository,
    private val meetingRepository: MeetingRepository,
) : ViewModel() {
    init {
        fetchMeeting()
    }

    private val _meeting = MutableLiveData<MeetingUiModel>()
    val meeting: LiveData<MeetingUiModel> = _meeting

    private val _notificationLogs = MutableLiveData<List<NotificationLogUiModel>>()
    val notificationLogs: LiveData<List<NotificationLogUiModel>> = _notificationLogs

    private fun fetchNotificationLogs(meetingId: Long) =
        viewModelScope.launch {
            notificationLogRepository.fetchNotificationLogs(meetingId)
                .onSuccess {
                    _notificationLogs.postValue(it.toNotificationUiModels())
                }.onFailure {
                    Timber.e(it.message)
                }
        }

    private fun fetchMeeting() =
        viewModelScope.launch {
            meetingRepository.fetchMeeting()
                .onSuccess {
//                    _meeting.postValue(it.first().toMeetingUiModel())
                    fetchNotificationLogs(it.first().id)
                }.onFailure {
                    Timber.e(it.message)
                }
        }
}
