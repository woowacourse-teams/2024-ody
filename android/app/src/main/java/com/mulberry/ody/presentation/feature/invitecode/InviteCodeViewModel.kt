package com.mulberry.ody.presentation.feature.invitecode

import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.usecase.CheckInviteCodeUseCase
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
import javax.inject.Inject

@HiltViewModel
class InviteCodeViewModel
    @Inject
    constructor(
        private val analyticsHelper: AnalyticsHelper,
        private val checkInviteCodeUseCase: CheckInviteCodeUseCase,
    ) : BaseViewModel() {
        val inviteCode: MutableStateFlow<String> = MutableStateFlow("")
        val hasInviteCode: StateFlow<Boolean> =
            inviteCode.map { it.isNotEmpty() }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(STATE_FLOW_SUBSCRIPTION_TIMEOUT_MILLIS),
                    initialValue = false,
                )

        private val _invalidCodeEvent: MutableSharedFlow<String> = MutableSharedFlow()
        val invalidCodeEvent: SharedFlow<String> get() = _invalidCodeEvent.asSharedFlow()

        private val _navigateAction: MutableSharedFlow<InviteCodeNavigateAction> = MutableSharedFlow()
        val navigateAction: SharedFlow<InviteCodeNavigateAction> get() = _navigateAction.asSharedFlow()

        fun clearInviteCode() {
            inviteCode.value = ""
        }

        fun checkInviteCode() {
            viewModelScope.launch {
                val inviteCode = inviteCode.value.ifBlank { return@launch }
                startLoading()
                checkInviteCodeUseCase(inviteCode)
                    .onSuccess {
                        _navigateAction.emit(InviteCodeNavigateAction.CodeNavigateToJoin)
                    }.onFailure { code, errorMessage ->
                        _invalidCodeEvent.emit(errorMessage.toString())
                        analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
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
