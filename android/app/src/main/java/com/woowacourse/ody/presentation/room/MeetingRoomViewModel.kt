package com.woowacourse.ody.presentation.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.domain.apiresult.onFailure
import com.woowacourse.ody.domain.apiresult.onNetworkError
import com.woowacourse.ody.domain.apiresult.onSuccess
import com.woowacourse.ody.domain.model.MateEtaInfo
import com.woowacourse.ody.domain.repository.ody.MatesEtaRepository
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import com.woowacourse.ody.domain.repository.ody.NotificationLogRepository
import com.woowacourse.ody.presentation.common.MutableSingleLiveData
import com.woowacourse.ody.presentation.common.SingleLiveData
import com.woowacourse.ody.presentation.common.analytics.AnalyticsHelper
import com.woowacourse.ody.presentation.common.analytics.logButtonClicked
import com.woowacourse.ody.presentation.common.analytics.logNetworkErrorEvent
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
) : ViewModel() {
    private val matesEta: LiveData<MateEtaInfo?> = matesEtaRepository.fetchMatesEta(meetingId = meetingId)

    val mateEtaUiModels: LiveData<List<MateEtaUiModel>?> =
        matesEta.map {
            val mateEtaInfo = it ?: return@map null
            mateEtaInfo.toMateEtaUiModels()
        }

    private val _meeting: MutableLiveData<MeetingDetailUiModel> = MutableLiveData(MeetingDetailUiModel())
    val meeting: LiveData<MeetingDetailUiModel> = _meeting

    private val _mates: MutableLiveData<List<MateUiModel>> = MutableLiveData()
    val mates: LiveData<List<MateUiModel>> = _mates

    private val _notificationLogs = MutableLiveData<List<NotificationLogUiModel>>()
    val notificationLogs: LiveData<List<NotificationLogUiModel>> = _notificationLogs

    private val _navigateToEtaDashboardEvent: MutableSingleLiveData<Unit> = MutableSingleLiveData<Unit>()
    val navigateToEtaDashboardEvent: SingleLiveData<Unit> get() = _navigateToEtaDashboardEvent

    private val _networkErrorEvent: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val networkErrorEvent: SingleLiveData<Unit> get() = _networkErrorEvent

    private val _errorEvent: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val errorEvent: SingleLiveData<Unit> get() = _errorEvent

    private var lastFailedAction: (() -> Unit)? = null

    init {
        fetchMeeting()
    }

    private fun fetchNotificationLogs(){
        viewModelScope.launch {
            notificationLogRepository.fetchNotificationLogs(meetingId)
                .onSuccess {
                    _notificationLogs.value = it.toNotificationUiModels()
                }.onFailure { code, errorMessage ->
                    _errorEvent.setValue(Unit)
                    analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                    Timber.e("$code $errorMessage")
                }.onNetworkError {
                    _networkErrorEvent.setValue(Unit)
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
                    _errorEvent.setValue(Unit)
                    analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                    Timber.e("$code $errorMessage")
                }.onNetworkError {
                    _networkErrorEvent.setValue(Unit)
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

    fun retryLastAction() {
        lastFailedAction?.invoke()
    }

    companion object {
        private const val TAG = "MeetingRoomViewModel"
    }
}
