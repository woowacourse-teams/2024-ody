package com.mulberry.ody.presentation.setting

import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.repository.ody.LoginRepository
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import com.mulberry.ody.presentation.common.BaseViewModel
import com.mulberry.ody.presentation.common.MutableSingleLiveData
import com.mulberry.ody.presentation.common.SingleLiveData
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import com.mulberry.ody.presentation.login.LoginNavigatedReason
import dagger.hilt.android.lifecycle.HiltViewModel
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
        private val _loginNavigateEvent: MutableSingleLiveData<LoginNavigatedReason> =
            MutableSingleLiveData()
        val loginNavigateEvent: SingleLiveData<LoginNavigatedReason> get() = _loginNavigateEvent

        fun kakaoLogout() =
            viewModelScope.launch {
                loginRepository.logout()
                _loginNavigateEvent.setValue(LoginNavigatedReason.LOGOUT)
            }

        fun withdrawAccount() {
            viewModelScope.launch {
                startLoading()
                loginRepository.withdrawAccount()
                    .onSuccess {
                        _loginNavigateEvent.setValue(LoginNavigatedReason.WITHDRAWAL)
                        clearEtaFetchingJob()
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

        private fun clearEtaFetchingJob() =
            viewModelScope.launch {
                matesEtaRepository.clearEtaFetchingJob()
            }

        companion object {
            private const val TAG = "SettingViewModel"
        }
    }
