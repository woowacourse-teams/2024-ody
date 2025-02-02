package com.mulberry.ody.presentation.login

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.repository.ody.AuthRepository
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
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
class LoginViewModel
    @Inject
    constructor(
        private val analyticsHelper: AnalyticsHelper,
        private val authRepository: AuthRepository,
        private val savedStateHandle: SavedStateHandle,
        private val matesEtaRepository: MatesEtaRepository,
    ) : BaseViewModel() {
        private val _navigatedReason: MutableSharedFlow<LoginNavigatedReason> =
            MutableSharedFlow()
        val navigatedReason: SharedFlow<LoginNavigatedReason> get() = _navigatedReason.asSharedFlow()

        private val _navigateAction: MutableSharedFlow<LoginNavigateAction> =
            MutableSharedFlow()
        val navigateAction: SharedFlow<LoginNavigateAction> get() = _navigateAction.asSharedFlow()

        fun verifyNavigation() {
            savedStateHandle.get<LoginNavigatedReason>(NAVIGATED_REASON)?.let { reason ->
                viewModelScope.launch {
                    _navigatedReason.emit(reason)
                }
            }
        }

        fun verifyLogin() {
            viewModelScope.launch {
                if (authRepository.isLoggedIn()) {
                    navigateToMeetings()
                }
            }
        }

        fun loginWithKakao(context: Context) {
            viewModelScope.launch {
                startLoading()
                authRepository.login(context)
                    .onSuccess {
                        navigateToMeetings()
                        matesEtaRepository.reserveAllEtaReservation()
                    }.onFailure { code, errorMessage ->
                        analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                        handleError()
                    }.onNetworkError {
                        handleNetworkError()
                        lastFailedAction = { loginWithKakao(context) }
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
            private const val TAG = "LOGIN_VIEWMODEL"
            private const val NAVIGATED_REASON = "NAVIGATED_REASON"
        }
    }
