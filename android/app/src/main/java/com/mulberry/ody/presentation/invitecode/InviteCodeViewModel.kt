package com.mulberry.ody.presentation.invitecode

import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.suspendOnFailure
import com.mulberry.ody.domain.apiresult.suspendOnSuccess
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.presentation.common.BaseViewModel
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InviteCodeViewModel
    @Inject
    constructor(
        private val analyticsHelper: AnalyticsHelper,
        private val meetingRepository: MeetingRepository,
    ) : BaseViewModel() {
        val inviteCode: MutableStateFlow<String> = MutableStateFlow("")
        val hasInviteCode: StateFlow<Boolean> =
            inviteCode.map { it.isNotEmpty() }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(STATE_FLOW_SUBSCRIPTION_TIMEOUT_MILLIS),
                    initialValue = false,
                )

        private val _invalidInviteCodeEvent: MutableSharedFlow<Unit> = MutableSharedFlow()
        val invalidInviteCodeEvent: SharedFlow<Unit> get() = _invalidInviteCodeEvent.asSharedFlow()

        private val _alreadyParticipatedEvent: MutableSharedFlow<Unit> = MutableSharedFlow()
        val alreadyParticipatedEvent: SharedFlow<Unit> get() = _alreadyParticipatedEvent.asSharedFlow()

        private val _navigateAction: MutableSharedFlow<InviteCodeNavigateAction> = MutableSharedFlow()
        val navigateAction: SharedFlow<InviteCodeNavigateAction> get() = _navigateAction.asSharedFlow()

        fun clearInviteCode() {
            inviteCode.value = ""
        }

        fun checkInviteCode() {
            viewModelScope.launch {
                val inviteCode = inviteCode.value.ifBlank { return@launch }
                startLoading()
                meetingRepository.fetchInviteCodeValidity(inviteCode)
                    .suspendOnSuccess {
                        _navigateAction.emit(InviteCodeNavigateAction.CodeNavigateToJoin)
                    }.suspendOnFailure { code, errorMessage ->
                        when (code) {
                            400 -> _alreadyParticipatedEvent.emit(Unit)
                            404 -> _invalidInviteCodeEvent.emit(Unit)
                        }
                        analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                        Timber.e("$code $errorMessage")
                    }.onNetworkError {
                        handleNetworkError()
                        lastFailedAction = { checkInviteCode() }
                    }
                stopLoading()
            }
        }

        companion object {
            private const val TAG = "InviteCodeViewModel"
            private const val STATE_FLOW_SUBSCRIPTION_TIMEOUT_MILLIS = 5000L
        }
    }
