package com.mulberry.ody.presentation.feature.login

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.repository.ody.AuthRepository
import com.mulberry.ody.presentation.common.BaseViewModel
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val analyticsHelper: AnalyticsHelper,
        private val authRepository: AuthRepository,
        savedStateHandle: SavedStateHandle,
    ) : BaseViewModel() {
        private val _navigatedReason: MutableSharedFlow<LoginNavigatedReason> = MutableSharedFlow()
        val navigatedReason: SharedFlow<LoginNavigatedReason> get() = _navigatedReason.asSharedFlow()

        private val _navigateAction: MutableSharedFlow<LoginNavigateAction> = MutableSharedFlow()
        val navigateAction: SharedFlow<LoginNavigateAction> get() = _navigateAction.asSharedFlow()

        private val loginNavigatedReason = savedStateHandle.get<LoginNavigatedReason>(NAVIGATED_REASON)

        fun verifyNavigation() {
            loginNavigatedReason ?: return
            viewModelScope.launch {
                _navigatedReason.subscriptionCount
                    .filter { it > 0 }
                    .first()
                _navigatedReason.emit(loginNavigatedReason)
            }
        }

        fun verifyLogin() {
            viewModelScope.launch {
                if (authRepository.isLoggedIn()) {
                    navigateToMeetings()
                }
            }
        }

        fun login(context: Context) {
            viewModelScope.launch {
                startLoading()
                authRepository.login(context)
                    .onSuccess {
                        navigateToMeetings()
                    }.onFailure { code, errorMessage ->
                        analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                        handleError()
                    }.onNetworkError {
                        handleNetworkError()
                        lastFailedAction = { login(context) }
                    }
                stopLoading()
            }
        }

        private fun navigateToMeetings() {
            viewModelScope.launch {
                _navigateAction.emit(LoginNavigateAction.NavigateToMeetings)
            }
        }

        companion object {
            private const val TAG = "LoginViewModel"
            private const val NAVIGATED_REASON = "NAVIGATED_REASON"
        }
    }
