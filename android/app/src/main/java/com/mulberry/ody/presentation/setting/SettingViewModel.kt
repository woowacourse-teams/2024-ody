package com.mulberry.ody.presentation.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mulberry.ody.data.remote.thirdparty.login.kakao.KakaoLoginRepository
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.repository.ody.AuthTokenRepository
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
    private val authTokenRepository: AuthTokenRepository,
    private val kakaoLoginRepository: KakaoLoginRepository,
) : BaseViewModel() {
    private val _loginNavigateEvent: MutableSingleLiveData<LoginNavigatedReason> = MutableSingleLiveData()
    val loginNavigateEvent: SingleLiveData<LoginNavigatedReason> get() = _loginNavigateEvent

    private val _isSuccessWithdrawal: MutableLiveData<Unit> = MutableLiveData()
    val isSuccessWithdrawal: LiveData<Unit> = _isSuccessWithdrawal

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
                    deleteAuthToken()
                    _isSuccessWithdrawal.value = Unit
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

    private fun deleteAuthToken() {
        viewModelScope.launch {
            authTokenRepository.deleteAuthToken()
        }
    }

    companion object {
        private val TAG = SettingViewModel::class.simpleName
    }
}
