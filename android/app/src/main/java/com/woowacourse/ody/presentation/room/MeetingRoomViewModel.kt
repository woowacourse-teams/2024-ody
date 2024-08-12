package com.woowacourse.ody.presentation.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.woowacourse.ody.domain.model.MateEtaInfo
import com.woowacourse.ody.domain.repository.ody.MatesEtaRepository
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import com.woowacourse.ody.domain.repository.ody.NotificationLogRepository
import com.woowacourse.ody.presentation.common.MutableSingleLiveData
import com.woowacourse.ody.presentation.common.SingleLiveData
import com.woowacourse.ody.presentation.common.analytics.logButtonClicked
import com.woowacourse.ody.presentation.common.analytics.logNetworkErrorEvent
import com.woowacourse.ody.presentation.room.etadashboard.model.MateEtaUiModel
import com.woowacourse.ody.presentation.room.etadashboard.model.toMateEtaUiModels
import com.woowacourse.ody.presentation.room.log.model.MeetingDetailUiModel
import com.woowacourse.ody.presentation.room.log.model.NotificationLogUiModel
import com.woowacourse.ody.presentation.room.log.model.toMeetingUiModel
import com.woowacourse.ody.presentation.room.log.model.toNotificationUiModels
import kotlinx.coroutines.launch
import timber.log.Timber

class MeetingRoomViewModel(
    private val firebaseAnalytics: FirebaseAnalytics,
    meetingId: Long,
    matesEtaRepository: MatesEtaRepository,
    private val notificationLogRepository: NotificationLogRepository,
    private val meetingRepository: MeetingRepository,
) : ViewModel() {
    private val matesEta: LiveData<MateEtaInfo?> =
        matesEtaRepository.fetchMatesEta(meetingId = meetingId)

    val mateEtaUiModels: LiveData<List<MateEtaUiModel>?> =
        matesEta.map {
            val mateEtaInfo = it ?: return@map null
            mateEtaInfo.mateEtas.toMateEtaUiModels(mateEtaInfo.userNickname)
        }

    private val _meeting = MutableLiveData(MeetingDetailUiModel())
    val meeting: LiveData<MeetingDetailUiModel> = _meeting

    private val _notificationLogs = MutableLiveData<List<NotificationLogUiModel>>()
    val notificationLogs: LiveData<List<NotificationLogUiModel>> = _notificationLogs

    private val _navigateToEtaEvent: MutableSingleLiveData<Unit> = MutableSingleLiveData<Unit>()
    val navigateToEtaEvent: SingleLiveData<Unit> get() = _navigateToEtaEvent

    init {
        fetchMeeting(meetingId)
    }

    private fun fetchNotificationLogs(meetingId: Long) =
        viewModelScope.launch {
            notificationLogRepository.fetchNotificationLogs(meetingId)
                .onSuccess {
                    _notificationLogs.postValue(it.toNotificationUiModels())
                }.onFailure {
                    firebaseAnalytics.logNetworkErrorEvent(TAG, it.message)
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

    fun navigateToEta() {
        firebaseAnalytics.logButtonClicked(
            eventName = "eta_button_from_notification_log",
            location = TAG,
        )
        _navigateToEtaEvent.setValue(Unit)
    }

    companion object {
        private const val TAG = "MeetingRoomViewModel"
    }
}
