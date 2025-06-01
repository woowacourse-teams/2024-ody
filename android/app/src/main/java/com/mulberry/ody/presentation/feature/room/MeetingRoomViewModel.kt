package com.mulberry.ody.presentation.feature.room

import android.graphics.Bitmap
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
import com.mulberry.ody.presentation.common.image.toByteArray
import com.mulberry.ody.presentation.feature.room.detail.model.DetailMeetingUiModel
import com.mulberry.ody.presentation.feature.room.detail.model.InviteCodeCopyInfo
import com.mulberry.ody.presentation.feature.room.detail.model.MateUiModel
import com.mulberry.ody.presentation.feature.room.detail.model.toDetailMeetingUiModel
import com.mulberry.ody.presentation.feature.room.detail.model.toMateUiModels
import com.mulberry.ody.presentation.feature.room.etadashboard.model.MateEtaUiModel
import com.mulberry.ody.presentation.feature.room.etadashboard.model.toMateEtaUiModels
import com.mulberry.ody.presentation.feature.room.log.model.NotificationLogUiModel
import com.mulberry.ody.presentation.feature.room.log.model.toNotificationUiModels
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
import java.time.Duration
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
    ) : BaseViewModel() {
        private val matesEta: Flow<MateEtaInfo?> = matesEtaRepository.fetchMatesEtaInfo(meetingId = meetingId)

        val mateEtaUiModels: StateFlow<List<MateEtaUiModel>> =
            matesEta.map {
                val mateEtaInfo = it ?: return@map emptyList()
                mateEtaInfo.toMateEtaUiModels()
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STATE_FLOW_SUBSCRIPTION_TIMEOUT_MILLIS),
                initialValue = emptyList(),
            )

        private val _meeting: MutableStateFlow<DetailMeetingUiModel> = MutableStateFlow(DetailMeetingUiModel.DEFAULT)
        val meeting: StateFlow<DetailMeetingUiModel> = _meeting.asStateFlow()

        private val _mates: MutableStateFlow<List<MateUiModel>> = MutableStateFlow(listOf())
        val mates: StateFlow<List<MateUiModel>> = _mates.asStateFlow()

        private val _notificationLogs = MutableStateFlow<List<NotificationLogUiModel>>(listOf())
        val notificationLogs: StateFlow<List<NotificationLogUiModel>> = _notificationLogs.asStateFlow()

        private val _navigationEvent: MutableSharedFlow<MeetingRoomNavigateAction> = MutableSharedFlow()
        val navigationEvent: SharedFlow<MeetingRoomNavigateAction> get() = _navigationEvent.asSharedFlow()

        private val _nudgeSuccessMate: MutableSharedFlow<String> = MutableSharedFlow()
        val nudgeSuccessMate: SharedFlow<String> get() = _nudgeSuccessMate.asSharedFlow()

        private val _expiredNudgeTimeLimit: MutableSharedFlow<Unit> = MutableSharedFlow()
        val expiredNudgeTimeLimit: SharedFlow<Unit> get() = _expiredNudgeTimeLimit.asSharedFlow()

        private val _nudgeFailMate: MutableSharedFlow<Int> = MutableSharedFlow()
        val nudgeFailMate: SharedFlow<Int> get() = _nudgeFailMate.asSharedFlow()

        private val _exitMeetingRoomEvent: MutableSharedFlow<Unit> = MutableSharedFlow()
        val exitMeetingRoomEvent: SharedFlow<Unit> get() = _exitMeetingRoomEvent.asSharedFlow()

        private val _copyInviteCodeEvent: MutableSharedFlow<InviteCodeCopyInfo> = MutableSharedFlow()
        val copyInviteCodeEvent: SharedFlow<InviteCodeCopyInfo> get() = _copyInviteCodeEvent.asSharedFlow()

        private val matesNudgeTimes: MutableMap<Long, LocalDateTime> = mutableMapOf()

        private val _isVisibleNavigation: MutableStateFlow<Boolean> = MutableStateFlow(false)
        val isVisibleNavigation: StateFlow<Boolean> get() = _isVisibleNavigation

        private val _inaccessibleEtaEvent: MutableSharedFlow<Unit> = MutableSharedFlow()
        val inaccessibleEtaEvent: SharedFlow<Unit> get() = _inaccessibleEtaEvent

        init {
            fetchMeeting()
        }

        fun nudgeMate(
            userId: Long,
            mateId: Long,
        ) {
            viewModelScope.launch {
                val targetMate = mateEtaUiModels.value.find { it.mateId == mateId } ?: return@launch
                handleNudgeAction(userId, mateId, targetMate.nickname)
            }
        }

        private suspend fun handleNudgeAction(
            userId: Long,
            mateId: Long,
            mateNickname: String,
        ) {
            val recentNudgeTime = matesNudgeTimes.getOrDefault(mateId, DEFAULT_NUDGE_TIME)
            val currentTime = LocalDateTime.now()
            val elapsedSeconds = Duration.between(recentNudgeTime, currentTime).seconds
            val remainingCooldown = NUDGE_DELAY_SECONDS - elapsedSeconds

            if (recentNudgeTime == DEFAULT_NUDGE_TIME || elapsedSeconds >= NUDGE_DELAY_SECONDS) {
                matesNudgeTimes[mateId] = currentTime
                performNudge(userId, mateId, mateNickname)
                return
            }

            _nudgeFailMate.emit(remainingCooldown.toInt())
        }

        private suspend fun performNudge(
            userId: Long,
            mateId: Long,
            mateNickname: String,
        ) {
            meetingRepository.postNudge(Nudge(userId, mateId))
                .onSuccess {
                    _nudgeSuccessMate.emit(mateNickname)
                }.onFailure { code, errorMessage ->
                    when (code) {
                        400 -> _expiredNudgeTimeLimit.emit(Unit)
                        else -> handleError()
                    }
                    analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                }.onNetworkError {
                    handleNetworkError()
                    lastFailedAction = { nudgeMate(userId, mateId) }
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
                        _meeting.value = it.toDetailMeetingUiModel()
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
                if (!meeting.value.isEtaAccessible()) {
                    _inaccessibleEtaEvent.emit(Unit)
                    return@launch
                }

                analyticsHelper.logButtonClicked(
                    eventName = "navigate_to_eta_dashboard",
                    location = TAG,
                )
                _navigationEvent.emit(MeetingRoomNavigateAction.NavigateToEtaDashboard)
            }
        }

        fun navigateToNotificationLog() {
            viewModelScope.launch {
                analyticsHelper.logButtonClicked(
                    eventName = "navigate_to_notification_log",
                    location = TAG,
                )
                _navigationEvent.emit(MeetingRoomNavigateAction.NavigateToNotificationLog)
            }
        }

        fun shareEtaDashboard(
            title: String,
            buttonTitle: String,
            bitmap: Bitmap,
        ) {
            viewModelScope.launch {
                startLoading()
                imageStorage.upload(
                    byteArray = bitmap.toByteArray(),
                    fileName = LocalDateTime.now().toString(),
                ).onSuccess {
                    val imageShareContent =
                        ImageShareContent(
                            title = title,
                            buttonTitle = buttonTitle,
                            imageUrl = it,
                            imageWidthPixel = bitmap.width,
                            imageHeightPixel = bitmap.height,
                            link = ODY_PLAY_STORE_LINK,
                        )
                    shareImage(imageShareContent)
                }.onNetworkError {
                    handleNetworkError()
                    lastFailedAction = { shareEtaDashboard(title, buttonTitle, bitmap) }
                }
                stopLoading()
            }
        }

        fun copyInviteCode() {
            val meeting = meeting.value
            if (meeting.isDefault()) return
            val inviteCodeCopyInfo = InviteCodeCopyInfo(meeting.name, meeting.inviteCode)

            viewModelScope.launch {
                _copyInviteCodeEvent.emit(inviteCodeCopyInfo)
            }
        }

        private fun shareImage(imageShareContent: ImageShareContent) {
            viewModelScope.launch {
                imageShareHelper.share(imageShareContent)
                    .onUnexpected {
                        handleError()
                    }
            }
        }

        fun exitMeetingRoom() {
            viewModelScope.launch {
                if (_meeting.value.isDefault()) {
                    handleError()
                    return@launch
                }

                startLoading()
                meetingRepository.exitMeeting(_meeting.value.id)
                    .onSuccess {
                        matesEtaRepository.closeEtaDashboard(meetingId)
                        _exitMeetingRoomEvent.emit(Unit)
                    }.onFailure { code, errorMessage ->
                        handleError()
                        analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                        Timber.e("$code $errorMessage")
                    }.onNetworkError {
                        handleNetworkError()
                        lastFailedAction = { exitMeetingRoom() }
                    }
                stopLoading()
            }
        }

        fun handleNavigationVisibility() {
            _isVisibleNavigation.value = !_isVisibleNavigation.value
        }

        @AssistedFactory
        interface MeetingViewModelFactory {
            fun create(meetingId: Long): MeetingRoomViewModel
        }

        companion object {
            private const val TAG = "MeetingRoomViewModel"
            private const val STATE_FLOW_SUBSCRIPTION_TIMEOUT_MILLIS = 5000L
            private const val NUDGE_DELAY_SECONDS = 10L
            private val DEFAULT_NUDGE_TIME = LocalDateTime.of(2000, 1, 1, 1, 1)
            private const val ODY_PLAY_STORE_LINK = "https://play.google.com/store/apps/details?id=com.mulberry.ody"

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
