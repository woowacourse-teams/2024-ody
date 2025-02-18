package com.mulberry.ody.presentation.meetings

import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.presentation.common.BaseViewModel
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import com.mulberry.ody.presentation.meetings.listener.MeetingsItemListener
import com.mulberry.ody.presentation.meetings.listener.MeetingsListener
import com.mulberry.ody.presentation.meetings.model.MeetingUiModel
import com.mulberry.ody.presentation.meetings.model.toMeetingUiModels
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
    ) : BaseViewModel(), MeetingsItemListener, MeetingsListener {
        private val _meetings: MutableStateFlow<List<MeetingUiModel>> = MutableStateFlow(listOf())
        val meetings: StateFlow<List<MeetingUiModel>> get() = _meetings.asStateFlow()

        private val _navigateAction = MutableSharedFlow<MeetingsNavigateAction>()
        val navigateAction: SharedFlow<MeetingsNavigateAction> = _navigateAction.asSharedFlow()

        private val _isMeetingsEmpty: MutableStateFlow<Boolean> = MutableStateFlow(true)
        val isMeetingsEmpty: StateFlow<Boolean> get() = _isMeetingsEmpty.asStateFlow()

        private val _isSelectedFloatingNavigator: MutableStateFlow<Boolean> = MutableStateFlow(false)
        val isSelectedFloatingNavigator: StateFlow<Boolean> get() = _isSelectedFloatingNavigator.asStateFlow()

        private val _inaccessibleEtaEvent: MutableSharedFlow<Unit> = MutableSharedFlow()
        val inaccessibleEtaEvent: SharedFlow<Unit> get() = _inaccessibleEtaEvent.asSharedFlow()

        fun fetchMeetings() {
            viewModelScope.launch {
                startLoading()
                meetingRepository.fetchMeetings()
                    .onSuccess {
                        val meetings = it.toMeetingUiModels()
                        _meetings.value = meetings
                        _isMeetingsEmpty.value = meetings.isEmpty()
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

        override fun navigateToEtaDashboard(meetingId: Long) {
            viewModelScope.launch {
                _navigateAction.emit(MeetingsNavigateAction.NavigateToEtaDashboard(meetingId))
            }
        }

        override fun navigateToNotificationLog(meetingId: Long) {
            viewModelScope.launch {
                _navigateAction.emit(MeetingsNavigateAction.NavigateToNotificationLog(meetingId))
            }
        }

        override fun toggleFold(id: Long) {
            val meetings = _meetings.value.toMutableList()
            val index = meetings.indexOfFirst { it.id == id }
            meetings[index] = meetings[index].copy(isFolded = !meetings[index].isFolded)
            _meetings.value = meetings
        }

        fun selectFloatingNavigator() {
            viewModelScope.launch {
                _isSelectedFloatingNavigator.emit(!_isSelectedFloatingNavigator.value)
            }
        }

        override fun onJoinMeeting() {
            viewModelScope.launch {
                _navigateAction.emit(MeetingsNavigateAction.NavigateToJoinMeeting)
                _isSelectedFloatingNavigator.emit(false)
            }
        }

        override fun onCreateMeeting() {
            viewModelScope.launch {
                _navigateAction.emit(MeetingsNavigateAction.NavigateToCreateMeeting)
                _isSelectedFloatingNavigator.emit(false)
            }
        }

        override fun guideItemDisabled() {
            viewModelScope.launch {
                _inaccessibleEtaEvent.emit(Unit)
            }
        }

        override fun onClickSetting() {
            viewModelScope.launch {
                _navigateAction.emit(MeetingsNavigateAction.NavigateToSetting)
            }
        }

        companion object {
            private const val TAG = "MeetingsViewModel"
        }
    }
