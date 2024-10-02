package com.mulberry.ody.presentation.login

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.repository.ody.AuthTokenRepository
import com.mulberry.ody.domain.repository.ody.LoginRepository
import com.mulberry.ody.presentation.common.BaseViewModel
import com.mulberry.ody.presentation.common.MutableSingleLiveData
import com.mulberry.ody.presentation.common.SingleLiveData
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val analyticsHelper: AnalyticsHelper,
        private val authTokenRepository: AuthTokenRepository,
        private val loginRepository: LoginRepository,
        private val savedStateHandle: SavedStateHandle,
    ) : BaseViewModel() {
        private val _navigatedReason: MutableSingleLiveData<LoginNavigatedReason> = MutableSingleLiveData()
        val navigatedReason: SingleLiveData<LoginNavigatedReason> get() = _navigatedReason

        private val _navigateAction: MutableSingleLiveData<LoginNavigateAction> =
            MutableSingleLiveData()
        val navigateAction: SingleLiveData<LoginNavigateAction> get() = _navigateAction

        fun checkIfNavigated() {
            savedStateHandle.get<LoginNavigatedReason>(NAVIGATED_REASON) ?.let { reason ->
                _navigatedReason.setValue(reason)
            }
        }

        fun checkIfLogined() {
            viewModelScope.launch {
                authTokenRepository.fetchAuthToken().onSuccess {
                    if (loginRepository.checkIfLogined() && it.accessToken.isNotEmpty()) {
                        navigateToMeetings()
                    }
                }
            }
        }

        fun loginWithKakao(context: Context) {
            viewModelScope.launch {
                startLoading()
                loginRepository.login(context)
                    .onSuccess {
                        navigateToMeetings()
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
            _navigateAction.setValue(LoginNavigateAction.NavigateToMeetings)
        }

        companion object {
            private const val TAG = "LOGIN_VIEWMODEL"
            private const val NAVIGATED_REASON = "NAVIGATED_REASON"
        }
    }
