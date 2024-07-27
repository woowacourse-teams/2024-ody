package com.woowacourse.ody.presentation.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.woowacourse.ody.presentation.common.Event
import com.woowacourse.ody.presentation.common.emit

class IntroViewModel : ViewModel(), IntroListener {
    private val _navigateAction: MutableLiveData<Event<IntroNavigateAction>> = MutableLiveData(null)
    val navigateAction: LiveData<Event<IntroNavigateAction>> get() = _navigateAction

    override fun onClickInputInviteCodeButton() {
        _navigateAction.emit(IntroNavigateAction.NavigateToInviteCode)
    }

    override fun onClickMeetingInfoButton() {
        _navigateAction.emit(IntroNavigateAction.NavigateToMeetingInfo)
    }
}
