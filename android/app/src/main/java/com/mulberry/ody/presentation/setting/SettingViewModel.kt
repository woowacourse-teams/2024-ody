package com.mulberry.ody.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulberry.ody.data.remote.thirdparty.login.kakao.KakaoLoginRepository
import com.mulberry.ody.presentation.common.MutableSingleLiveData
import com.mulberry.ody.presentation.common.SingleLiveData
import com.mulberry.ody.presentation.login.LoginNavigatedReason
import kotlinx.coroutines.launch

class SettingViewModel(
    private val kakaoLoginRepository: KakaoLoginRepository,
) : ViewModel() {
    private val _loginNavigateEvent: MutableSingleLiveData<LoginNavigatedReason> = MutableSingleLiveData()
    val loginNavigateEvent: SingleLiveData<LoginNavigatedReason> get() = _loginNavigateEvent

    fun kakaoLogout() =
        viewModelScope.launch {
            kakaoLoginRepository.logout()
            _loginNavigateEvent.setValue(LoginNavigatedReason.LOGOUT)
        }
}
