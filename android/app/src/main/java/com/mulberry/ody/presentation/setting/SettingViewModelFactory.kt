package com.mulberry.ody.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mulberry.ody.data.remote.thirdparty.login.kakao.KakaoLoginRepository
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper

class SettingViewModelFactory(
    private val analyticsHelper: AnalyticsHelper,
    private val kakaoLoginRepository: KakaoLoginRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            SettingViewModel(
                analyticsHelper,
                kakaoLoginRepository,
            ) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
