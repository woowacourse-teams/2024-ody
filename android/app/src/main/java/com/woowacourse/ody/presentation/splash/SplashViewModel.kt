package com.woowacourse.ody.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.presentation.common.MutableSingleLiveData
import com.woowacourse.ody.presentation.common.SingleLiveData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val _navigateAction: MutableSingleLiveData<SplashNavigateAction> = MutableSingleLiveData()
    val navigateAction: SingleLiveData<SplashNavigateAction> get() = _navigateAction

    fun navigateToMeetings() {
        viewModelScope.launch {
            delay(1500)
            _navigateAction.setValue(SplashNavigateAction.NavigateToMeetings)
        }
    }
}
