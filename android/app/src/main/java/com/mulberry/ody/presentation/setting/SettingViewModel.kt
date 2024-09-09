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
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import kotlinx.coroutines.launch
import timber.log.Timber

class SettingViewModel(
    private val analyticsHelper: AnalyticsHelper,
    private val authTokenRepository: AuthTokenRepository,
    private val kakaoLoginRepository: KakaoLoginRepository,
) : BaseViewModel() {
    private val _isSuccessWithdrawal: MutableLiveData<Unit> = MutableLiveData()
    val isSuccessWithdrawal: LiveData<Unit> = _isSuccessWithdrawal

    fun withdrawAccount() {
        viewModelScope.launch {
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
