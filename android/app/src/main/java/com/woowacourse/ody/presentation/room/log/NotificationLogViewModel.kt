package com.woowacourse.ody.presentation.room.log

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import com.woowacourse.ody.domain.repository.ody.NotificationLogRepository
import com.woowacourse.ody.presentation.common.analytics.AnalyticsHelper
import com.woowacourse.ody.presentation.common.analytics.logNetworkErrorEvent
import com.woowacourse.ody.presentation.room.log.model.MeetingDetailUiModel
import com.woowacourse.ody.presentation.room.log.model.NotificationLogUiModel
import com.woowacourse.ody.presentation.room.log.model.toMeetingUiModel
import com.woowacourse.ody.presentation.room.log.model.toNotificationUiModels
import kotlinx.coroutines.launch
import timber.log.Timber

class NotificationLogViewModel(
    private val analyticsHelper: AnalyticsHelper,
    savedStateHandle: SavedStateHandle,
    private val notificationLogRepository: NotificationLogRepository,
    private val meetingRepository: MeetingRepository,
) : ViewModel() {
    private val _meeting = MutableLiveData(MeetingDetailUiModel())
    val meeting: LiveData<MeetingDetailUiModel> = _meeting

    private val _notificationLogs = MutableLiveData<List<NotificationLogUiModel>>()
    val notificationLogs: LiveData<List<NotificationLogUiModel>> = _notificationLogs

    private val meetingId: Long = savedStateHandle[PUT_EXTRA_MEETING_ID] ?: -1L

    init {
        fetchMeeting(meetingId)
    }

    private fun fetchNotificationLogs(meetingId: Long) =
        viewModelScope.launch {
            notificationLogRepository.fetchNotificationLogs(meetingId)
                .onSuccess {
                    _notificationLogs.postValue(it.toNotificationUiModels())
                }.onFailure {
                    analyticsHelper.logNetworkErrorEvent(TAG, it.message)
                    Timber.e(it.message)
                }
        }

    private fun fetchMeeting(meetingId: Long) =
        viewModelScope.launch {
            meetingRepository.fetchMeeting(meetingId)
                .onSuccess {
                    _meeting.postValue(it.toMeetingUiModel())
                    fetchNotificationLogs(meetingId)
                }.onFailure {
                    Timber.e(it.message)
                }
        }

    companion object {
        private const val TAG = "NotificationLogViewModel"
        private const val PUT_EXTRA_MEETING_ID = "meeting_id"
    }
}
