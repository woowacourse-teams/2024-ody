package com.woowacourse.ody.presentation.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.domain.apiresult.onFailure
import com.woowacourse.ody.domain.apiresult.onNetworkError
import com.woowacourse.ody.domain.apiresult.onSuccess
import com.woowacourse.ody.domain.apiresult.onUnexpected
import com.woowacourse.ody.domain.model.MateEtaInfo
import com.woowacourse.ody.domain.repository.image.ImageStorage
import com.woowacourse.ody.domain.repository.ody.MatesEtaRepository
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import com.woowacourse.ody.domain.repository.ody.NotificationLogRepository
import com.woowacourse.ody.presentation.common.BaseViewModel
import com.woowacourse.ody.presentation.common.MutableSingleLiveData
import com.woowacourse.ody.presentation.common.SingleLiveData
import com.woowacourse.ody.presentation.common.analytics.AnalyticsHelper
import com.woowacourse.ody.presentation.common.analytics.logButtonClicked
import com.woowacourse.ody.presentation.common.analytics.logNetworkErrorEvent
import com.woowacourse.ody.presentation.common.image.ImageShareContent
import com.woowacourse.ody.presentation.common.image.ImageShareHelper
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
import java.time.LocalDateTime

class MeetingRoomViewModel(
    private val analyticsHelper: AnalyticsHelper,
    private val meetingId: Long,
    matesEtaRepository: MatesEtaRepository,
    private val notificationLogRepository: NotificationLogRepository,
    private val meetingRepository: MeetingRepository,
    private val imageStorage: ImageStorage,
    private val imageShareHelper: ImageShareHelper,
) : BaseViewModel() {
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
        MutableSingleLiveData<Unit>()
    val navigateToEtaDashboardEvent: SingleLiveData<Unit> get() = _navigateToEtaDashboardEvent

    init {
        fetchMeeting()
    }

    private fun fetchNotificationLogs() {
        viewModelScope.launch {
            startLoading()
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
            stopLoading()
        }
    }

    private fun fetchMeeting() {
        viewModelScope.launch {
            startLoading()
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
            stopLoading()
        }
    }

    fun navigateToEtaDashboard() {
        analyticsHelper.logButtonClicked(
            eventName = "eta_button_from_notification_log",
            location = TAG,
        )
        _navigateToEtaDashboardEvent.setValue(Unit)
    }

    fun shareEtaDashboard(
        title: String,
        buttonTitle: String,
        imageByteArray: ByteArray,
        imageWidthPixel: Int,
        imageHeightPixel: Int,
    ) {
        viewModelScope.launch {
            startLoading()
            imageStorage.upload(
                byteArray = imageByteArray,
                fileName = LocalDateTime.now().toString(),
            )
                .onSuccess {
                    val imageShareContent =
                        ImageShareContent(
                            title = title,
                            buttonTitle = buttonTitle,
                            imageUrl = it,
                            imageWidthPixel = imageWidthPixel,
                            imageHeightPixel = imageHeightPixel,
                            link = "https://github.com/woowacourse-teams/2024-ody",
                        )
                    shareImage(imageShareContent)
                }.onNetworkError {
                    handleNetworkError()
                    lastFailedAction = {
                        shareEtaDashboard(
                            title,
                            buttonTitle,
                            imageByteArray,
                            imageWidthPixel,
                            imageHeightPixel
                        )
                    }
                }
            stopLoading()
        }
    }

    fun shareInviteCode(
        title: String,
        description: String,
        buttonTitle: String,
        imageUrl: String,
    ) {
        startLoading()
        val imageShareContent =
            ImageShareContent(
                title = title,
                description = description,
                buttonTitle = buttonTitle,
                imageUrl = imageUrl,
                link = "https://github.com/woowacourse-teams/2024-ody",
            )
        shareImage(imageShareContent)
        stopLoading()
    }

    private fun shareImage(imageShareContent: ImageShareContent) {
        viewModelScope.launch {
            imageShareHelper.share(imageShareContent)
                .onUnexpected {
                    handleError()
                }
        }
    }

    companion object {
        private const val TAG = "MeetingRoomViewModel"
    }
}
