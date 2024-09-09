package com.mulberry.ody.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulberry.ody.data.remote.thirdparty.login.kakao.KakaoLoginRepository
import kotlinx.coroutines.launch

class SettingViewModel(
    private val kakaoLoginRepository: KakaoLoginRepository,
) : ViewModel() {
    fun kakaoLogout() =
        viewModelScope.launch {
            kakaoLoginRepository.logout()
        }
}
