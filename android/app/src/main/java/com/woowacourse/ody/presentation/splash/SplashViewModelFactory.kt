package com.woowacourse.ody.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.ody.domain.repository.ody.FCMTokenRepository
import com.woowacourse.ody.domain.repository.ody.MeetingRepository

class SplashViewModelFactory(
    private val fcmTokenRepository: FCMTokenRepository,
    private val meetingRepository: MeetingRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(fcmTokenRepository, meetingRepository) as T
        }
        return super.create(modelClass)
    }
}
