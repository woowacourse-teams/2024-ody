package com.woowacourse.ody.presentation.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.domain.apiresult.onFailure
import com.woowacourse.ody.domain.apiresult.onNetworkError
import com.woowacourse.ody.domain.apiresult.onSuccess
import com.woowacourse.ody.domain.model.MateEtaInfo
import com.woowacourse.ody.domain.repository.ody.MatesEtaRepository
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import com.woowacourse.ody.domain.repository.ody.NotificationLogRepository
import com.woowacourse.ody.presentation.common.BaseViewModel
import com.woowacourse.ody.presentation.common.MutableSingleLiveData
import com.woowacourse.ody.presentation.common.SingleLiveData
import com.woowacourse.ody.presentation.common.analytics.AnalyticsHelper
import com.woowacourse.ody.presentation.common.analytics.logButtonClicked
import com.woowacourse.ody.presentation.common.analytics.logNetworkErrorEvent
import com.woowacourse.ody.presentation.room.etadashboard.listener.NudgeListener
import com.woowacourse.ody.presentation.room.etadashboard.model.MateEtaUiModel
import com.woowacourse.ody.presentation.room.etadashboard.model.toMateEtaUiModels
import com.woowacourse.ody.presentation.room.log.model.MateUiModel
import com.woowacourse.ody.presentation.room.log.model.MeetingDetailUiModel
import com.woowacourse.ody.presentation.room.log.model.NotificationLogUiModel
import com.woowacourse.ody.presentation.room.log.model.toMateUiModels
import com.woowacourse.ody.presentation.room.log.model.toMeetingUiModel
import com.woowacourse.ody.presentation.room.log.model.toNotificationUiModels
import kotlinx.coroutines.launch
import timber.log.Timber

class MeetingRoomViewModel(
    private val analyticsHelper: AnalyticsHelper,
    private val meetingId: Long,
    matesEtaRepository: MatesEtaRepository,
    private val notificationLogRepository: NotificationLogRepository,
    private val meetingRepository: MeetingRepository,
) : BaseViewModel(), NudgeListener {
    private val matesEta: LiveData<MateEtaInfo?> =
        matesEtaRepository.fetchMatesEta(meetingId = meetingId)

    val mateEtaUiModels: LiveData<List<MateEtaUiModel>?> =
        matesEta.map {
            val mateEtaInfo = it ?: return@map null
            mateEtaInfo.toMateEtaUiModels()
        }

    private val _meeting: MutableLiveData<MeetingDetailUiModel> =
        MutableLiveData(MeetingDetailUiModel())
    val meeting: LiveData<MeetingDetailUiModel> = _meeting

    private val _mates: MutableLiveData<List<MateUiModel>> = MutableLiveData()
    val mates: LiveData<List<MateUiModel>> = _mates

    private val _notificationLogs = MutableLiveData<List<NotificationLogUiModel>>()
    val notificationLogs: LiveData<List<NotificationLogUiModel>> = _notificationLogs

    private val _navigateToEtaDashboardEvent: MutableSingleLiveData<Unit> =
        MutableSingleLiveData()
    val navigateToEtaDashboardEvent: SingleLiveData<Unit> get() = _navigateToEtaDashboardEvent

    private val _nudgeSuccessMate: MutableSingleLiveData<String> = MutableSingleLiveData()
    val nudgeSuccessMate: SingleLiveData<String> get() = _nudgeSuccessMate

    init {
        fetchMeeting()
    }

    private fun fetchNotificationLogs() {
        viewModelScope.launch {
            notificationLogRepository.fetchNotificationLogs(meetingId)
                .onSuccess {
                    _notificationLogs.value = it.toNotificationUiModels()
                }.onFailure { code, errorMessage ->
                    handleError()
                    analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                    Timber.e("$code $errorMessage")
                }.onNetworkError {
                    handleNetworkError()
                    lastFailedAction = { fetchNotificationLogs() }
                }
        }
    }

    private fun fetchMeeting() {
        viewModelScope.launch {
            meetingRepository.fetchMeeting(meetingId)
                .onSuccess {
                    _meeting.value = it.toMeetingUiModel()
                    _mates.value = it.toMateUiModels()
                    fetchNotificationLogs()
                }.onFailure { code, errorMessage ->
                    handleError()
                    analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                    Timber.e("$code $errorMessage")
                }.onNetworkError {
                    handleNetworkError()
                    lastFailedAction = { fetchMeeting() }
                }
        }
    }

    fun navigateToEtaDashboard() {
        analyticsHelper.logButtonClicked(
            eventName = "eta_button_from_notification_log",
            location = TAG,
        )
        _navigateToEtaDashboardEvent.setValue(Unit)
    }

    override fun nudgeMate(mateId: Long) {
        viewModelScope.launch {
            meetingRepository.fetchNudge(mateId)
                .onSuccess {
                    val mateNickname =
                        matesEta.value?.mateEtas?.find { it.mateId == mateId }?.nickname
                            ?: return@onSuccess
                    _nudgeSuccessMate.postValue(mateNickname)
                }.onFailure { code, errorMessage ->
                    handleError()
                    analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                    Timber.e("$code $errorMessage")
                }.onNetworkError {
                    handleNetworkError()
                    lastFailedAction = { nudgeMate(mateId) }
                }
        }
    }

    companion object {
        private const val TAG = "MeetingRoomViewModel"
    }
}
