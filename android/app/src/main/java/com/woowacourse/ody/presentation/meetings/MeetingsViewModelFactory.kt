package com.woowacourse.ody.presentation.meetings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.ody.data.remote.thirdparty.login.kakao.KakaoLoginRepository
import com.woowacourse.ody.domain.repository.ody.AuthTokenRepository
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import com.woowacourse.ody.presentation.common.analytics.AnalyticsHelper

class MeetingsViewModelFactory(
    private val analyticsHelper: AnalyticsHelper,
    private val meetingRepository: MeetingRepository,
    private val kakaoLoginRepository: KakaoLoginRepository,
    private val authTokenRepository: AuthTokenRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MeetingsViewModel::class.java)) {
            MeetingsViewModel(
                analyticsHelper,
                meetingRepository,
                kakaoLoginRepository,
                authTokenRepository,
            ) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
