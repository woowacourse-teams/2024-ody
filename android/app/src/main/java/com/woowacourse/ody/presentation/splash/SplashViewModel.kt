package com.woowacourse.ody.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.domain.repository.ody.FCMTokenRepository
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import com.woowacourse.ody.presentation.common.MutableSingleLiveData
import com.woowacourse.ody.presentation.common.SingleLiveData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(
    private val fcmTokenRepository: FCMTokenRepository,
    private val meetingRepository: MeetingRepository,
) : ViewModel() {
    private val _navigateAction: MutableSingleLiveData<SplashNavigateAction> = MutableSingleLiveData()
    val navigateAction: SingleLiveData<SplashNavigateAction> get() = _navigateAction

    init {
        fetchFCMToken()
    }

    private fun fetchFCMToken() {
        viewModelScope.launch {
            delay(1500)
            fcmTokenRepository.fetchFCMToken()
                .onSuccess {
                    fetchMeeting()
                }.onFailure {
                    _navigateAction.setValue(SplashNavigateAction.NavigateToIntro)
                }
        }
    }

    private fun fetchMeeting() =
        viewModelScope.launch {
            meetingRepository.fetchMeeting()
                .onSuccess {
                    if (it.isEmpty()) {
                        _navigateAction.setValue(SplashNavigateAction.NavigateToIntro)
                    } else {
                        _navigateAction.setValue(SplashNavigateAction.NavigateToMeetingRoom)
                    }
                }
        }
}
