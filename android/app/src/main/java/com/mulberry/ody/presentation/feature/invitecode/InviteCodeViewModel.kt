package com.mulberry.ody.presentation.feature.invitecode

import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.presentation.common.BaseViewModel
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InviteCodeViewModel
    @Inject
    constructor(
        private val analyticsHelper: AnalyticsHelper,
        private val meetingRepository: MeetingRepository,
    ) : BaseViewModel() {
        private val _invalidCodeEvent: MutableSharedFlow<String> = MutableSharedFlow()
        val invalidCodeEvent: SharedFlow<String> get() = _invalidCodeEvent.asSharedFlow()

        private val _navigateAction: MutableSharedFlow<InviteCodeNavigateAction> = MutableSharedFlow()
        val navigateAction: SharedFlow<InviteCodeNavigateAction> get() = _navigateAction.asSharedFlow()

        fun checkInviteCode(inviteCode: String) {
            viewModelScope.launch {
                startLoading()
                meetingRepository.fetchInviteCodeValidity(inviteCode)
                    .onSuccess {
                        _navigateAction.emit(InviteCodeNavigateAction.CodeNavigateToJoin)
                    }.onFailure { code, errorMessage ->
                        _invalidCodeEvent.emit(errorMessage.toString())
                        analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                    }.onNetworkError {
                        handleNetworkError()
                        lastFailedAction = { checkInviteCode(inviteCode) }
                    }
                stopLoading()
            }
        }

        companion object {
            private const val TAG = "InviteCodeViewModel"
        }
    }
