package com.mulberry.ody.presentation.setting

import androidx.lifecycle.viewModelScope
import com.mulberry.ody.data.remote.thirdparty.login.kakao.KakaoLoginRepository
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.presentation.common.BaseViewModel
import com.mulberry.ody.presentation.common.MutableSingleLiveData
import com.mulberry.ody.presentation.common.SingleLiveData
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import com.mulberry.ody.presentation.login.LoginNavigatedReason
import kotlinx.coroutines.launch
import timber.log.Timber

class SettingViewModel(
    private val analyticsHelper: AnalyticsHelper,
    private val kakaoLoginRepository: KakaoLoginRepository,
) : BaseViewModel() {
    private val _loginNavigateEvent: MutableSingleLiveData<LoginNavigatedReason> = MutableSingleLiveData()
    val loginNavigateEvent: SingleLiveData<LoginNavigatedReason> get() = _loginNavigateEvent

    fun kakaoLogout() {
        viewModelScope.launch {
            kakaoLoginRepository.logout()
            _loginNavigateEvent.setValue(LoginNavigatedReason.LOGOUT)
        }
    }

    fun withdrawAccount() {
        viewModelScope.launch {
            startLoading()
            kakaoLoginRepository.withdrawAccount()
                .onSuccess {
                    _loginNavigateEvent.setValue(LoginNavigatedReason.WITHDRAWAL)
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
