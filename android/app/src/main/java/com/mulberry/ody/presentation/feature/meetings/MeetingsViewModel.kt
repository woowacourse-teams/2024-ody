package com.mulberry.ody.presentation.feature.meetings

import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.presentation.common.BaseViewModel
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import com.mulberry.ody.presentation.feature.meetings.model.MeetingUiModel
import com.mulberry.ody.presentation.feature.meetings.model.MeetingsUiState
import com.mulberry.ody.presentation.feature.meetings.model.toMeetingUiModels
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MeetingsViewModel
    @Inject
    constructor(
        private val analyticsHelper: AnalyticsHelper,
        private val meetingRepository: MeetingRepository,
    ) : BaseViewModel() {
        private val _meetingsUiState: MutableStateFlow<MeetingsUiState> = MutableStateFlow(MeetingsUiState.Empty)
        val meetingsUiState: StateFlow<MeetingsUiState> get() = _meetingsUiState.asStateFlow()

        private val _navigateAction = MutableSharedFlow<MeetingsNavigateAction>()
        val navigateAction: SharedFlow<MeetingsNavigateAction> = _navigateAction.asSharedFlow()

        private val _inaccessibleEtaEvent: MutableSharedFlow<Unit> = MutableSharedFlow()
        val inaccessibleEtaEvent: SharedFlow<Unit> get() = _inaccessibleEtaEvent.asSharedFlow()

        fun fetchMeetings() {
            viewModelScope.launch {
                startLoading()
                meetingRepository.fetchMeetings()
                    .onSuccess {
                        val meetings = it.toMeetingUiModels()
                        _meetingsUiState.value = if (meetings.isEmpty()) MeetingsUiState.Empty else MeetingsUiState.Meetings(meetings)
                    }.onFailure { code, errorMessage ->
                        handleError()
                        analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                        Timber.e("$code $errorMessage")
                    }.onNetworkError {
                        handleNetworkError()
                        lastFailedAction = { fetchMeetings() }
                    }
                stopLoading()
            }
        }

        fun onJoinMeeting() {
            viewModelScope.launch {
                _navigateAction.emit(MeetingsNavigateAction.NavigateToJoinMeeting)
            }
        }

        fun onCreateMeeting() {
            viewModelScope.launch {
                _navigateAction.emit(MeetingsNavigateAction.NavigateToCreateMeeting)
            }
        }

        fun navigateToEta(meeting: MeetingUiModel) {
            viewModelScope.launch {
                if (meeting.isAccessible()) {
                    _inaccessibleEtaEvent.emit(Unit)
                    return@launch
                }
                _navigateAction.emit(MeetingsNavigateAction.NavigateToEtaDashboard(meeting.id))
            }
        }

        fun navigateToNotificationLog(meetingId: Long) {
            viewModelScope.launch {
                _navigateAction.emit(MeetingsNavigateAction.NavigateToNotificationLog(meetingId))
            }
        }

        fun navigateToSetting() {
            viewModelScope.launch {
                _navigateAction.emit(MeetingsNavigateAction.NavigateToSetting)
            }
        }

        companion object {
            private const val TAG = "MeetingsViewModel"
        }
    }
