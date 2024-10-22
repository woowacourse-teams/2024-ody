package com.mulberry.ody.presentation.room

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.apiresult.onUnexpected
import com.mulberry.ody.domain.apiresult.suspendOnSuccess
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.model.Nudge
import com.mulberry.ody.domain.repository.image.ImageStorage
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.domain.repository.ody.NotificationLogRepository
import com.mulberry.ody.presentation.common.BaseViewModel
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logButtonClicked
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import com.mulberry.ody.presentation.common.image.ImageShareContent
import com.mulberry.ody.presentation.common.image.ImageShareHelper
import com.mulberry.ody.presentation.room.etadashboard.listener.NudgeListener
import com.mulberry.ody.presentation.room.etadashboard.model.MateEtaUiModel
import com.mulberry.ody.presentation.room.etadashboard.model.toMateEtaUiModels
import com.mulberry.ody.presentation.room.log.model.MateUiModel
import com.mulberry.ody.presentation.room.log.model.MeetingDetailUiModel
import com.mulberry.ody.presentation.room.log.model.NotificationLogUiModel
import com.mulberry.ody.presentation.room.log.model.toMateUiModels
import com.mulberry.ody.presentation.room.log.model.toMeetingUiModel
import com.mulberry.ody.presentation.room.log.model.toNotificationUiModels
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime

class MeetingRoomViewModel
@AssistedInject
constructor(
    private val analyticsHelper: AnalyticsHelper,
    @Assisted private val meetingId: Long,
    private val matesEtaRepository: MatesEtaRepository,
    private val notificationLogRepository: NotificationLogRepository,
    private val meetingRepository: MeetingRepository,
    private val imageStorage: ImageStorage,
    private val imageShareHelper: ImageShareHelper,
) : BaseViewModel(), NudgeListener {
    private val matesEta: Flow<MateEtaInfo?> =
        matesEtaRepository.fetchMatesEtaInfo(meetingId = meetingId)

    val mateEtaUiModels: StateFlow<List<MateEtaUiModel>?> =
        matesEta.map {
            val mateEtaInfo = it ?: return@map null
            mateEtaInfo.toMateEtaUiModels()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_FLOW_SUBSCRIPTION_TIMEOUT_MILLIS),
            initialValue = null,
        )

    private val _meeting: MutableStateFlow<MeetingDetailUiModel> =
        MutableStateFlow(MeetingDetailUiModel())
    val meeting: StateFlow<MeetingDetailUiModel> = _meeting.asStateFlow()

    private val _mates: MutableStateFlow<List<MateUiModel>> = MutableStateFlow(listOf())
    val mates: StateFlow<List<MateUiModel>> = _mates.asStateFlow()

    private val _notificationLogs = MutableStateFlow<List<NotificationLogUiModel>>(listOf())
    val notificationLogs: StateFlow<List<NotificationLogUiModel>> = _notificationLogs.asStateFlow()

    private val _navigateToEtaDashboardEvent: MutableSharedFlow<Unit> = MutableSharedFlow()
    val navigateToEtaDashboardEvent: SharedFlow<Unit> get() = _navigateToEtaDashboardEvent.asSharedFlow()

    private val _nudgeSuccessMate: MutableSharedFlow<String> = MutableSharedFlow()
    val nudgeSuccessMate: SharedFlow<String> get() = _nudgeSuccessMate.asSharedFlow()

    private val _exitMeetingEvent: MutableSharedFlow<Unit> = MutableSharedFlow()
    val exitMeetingEvent: SharedFlow<Unit> get() = _exitMeetingEvent.asSharedFlow()

    init {
        fetchMeeting()
    }

    override fun nudgeMate(
        nudgeId: Long,
        mateId: Long,
    ) {
        viewModelScope.launch {
            meetingRepository.postNudge(Nudge(nudgeId, mateId))
                .suspendOnSuccess {
                    matesEta.collect { mateEta ->
                        val mateNickname = mateEta?.mateEtas?.find { it.mateId == mateId }?.nickname
                            ?: return@collect
                        _nudgeSuccessMate.emit(mateNickname)
                    }
                }.onFailure { code, errorMessage ->
                    handleError()
                    analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                    Timber.e("$code $errorMessage")
                }.onNetworkError {
                    handleNetworkError()
                    lastFailedAction = { nudgeMate(nudgeId, mateId) }
                }
        }
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
        viewModelScope.launch {
            analyticsHelper.logButtonClicked(
                eventName = "eta_button_from_notification_log",
                location = TAG,
            )
            _navigateToEtaDashboardEvent.emit(Unit)
        }
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
                            imageHeightPixel,
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

    fun exitRoom() {
        viewModelScope.launch {
            if (_meeting.value.isDefault()) {
                handleError()
                return@launch
            }

            startLoading()
            meetingRepository.exitMeeting(_meeting.value.id)
                .suspendOnSuccess {
                    _exitMeetingEvent.emit(Unit)
                }.onFailure { code, errorMessage ->
                    handleError()
                    analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                    Timber.e("$code $errorMessage")
                }.onNetworkError {
                    handleNetworkError()
                    lastFailedAction = { exitRoom() }
                }
            stopLoading()
        }
    }

    @AssistedFactory
    interface MeetingViewModelFactory {
        fun create(meetingId: Long): MeetingRoomViewModel
    }

    companion object {
        private const val TAG = "MeetingRoomViewModel"
        private const val STATE_FLOW_SUBSCRIPTION_TIMEOUT_MILLIS = 5000L

        fun provideFactory(
            assistedFactory: MeetingViewModelFactory,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
            meetingId: Long,
        ): AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle,
                ): T {
                    return assistedFactory.create(meetingId) as T
                }
            }
    }
}
