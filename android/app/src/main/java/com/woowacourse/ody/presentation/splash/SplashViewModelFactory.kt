package com.woowacourse.ody.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.ody.domain.repository.ody.MeetingRepository

class SplashViewModelFactory(
    private val meetingRepository: MeetingRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(meetingRepository) as T
        }
        return super.create(modelClass)
    }
}
