package com.mulberry.ody.presentation.setting

import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.suspendOnSuccess
import com.mulberry.ody.domain.repository.ody.LoginRepository
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import com.mulberry.ody.presentation.common.BaseViewModel
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import com.mulberry.ody.presentation.login.LoginNavigatedReason
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingViewModel
    @Inject
    constructor(
        private val analyticsHelper: AnalyticsHelper,
        private val loginRepository: LoginRepository,
        private val matesEtaRepository: MatesEtaRepository,
    ) : BaseViewModel() {
        private val _loginNavigateEvent: MutableSharedFlow<LoginNavigatedReason> =
            MutableSharedFlow()
        val loginNavigateEvent: SharedFlow<LoginNavigatedReason> get() = _loginNavigateEvent.asSharedFlow()

        fun logout() {
            viewModelScope.launch {
                loginRepository.logout()
                _loginNavigateEvent.emit(LoginNavigatedReason.LOGOUT)
                matesEtaRepository.clearEtaReservation(isReservationPending = true)
            }
        }

        fun withdrawAccount() {
            viewModelScope.launch {
                startLoading()
                loginRepository.withdrawAccount()
                    .suspendOnSuccess {
                        _loginNavigateEvent.emit(LoginNavigatedReason.WITHDRAWAL)
                        matesEtaRepository.clearEtaFetchingJob()
                        matesEtaRepository.clearEtaReservation(isReservationPending = false)
                    }.onFailure { code, errorMessage ->
                        handleError()
                        analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                        Timber.e("$code $errorMessage")
                    }.onNetworkError {
                        handleNetworkError()
                        lastFailedAction = { withdrawAccount() }
                    }
                stopLoading()
            }
        }

        companion object {
            private const val TAG = "SettingViewModel"
        }
    }
