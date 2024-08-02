package com.woowacourse.ody.presentation.intro

import androidx.lifecycle.ViewModel
import com.woowacourse.ody.presentation.common.MutableSingleLiveData
import com.woowacourse.ody.presentation.common.SingleLiveData
import com.woowacourse.ody.presentation.intro.listener.IntroListener

class IntroViewModel : ViewModel(), IntroListener {
    private val _navigateAction: MutableSingleLiveData<IntroNavigateAction> = MutableSingleLiveData()
    val navigateAction: SingleLiveData<IntroNavigateAction> get() = _navigateAction

    override fun onClickInputInviteCodeButton() {
        _navigateAction.setValue(IntroNavigateAction.NavigateToInviteCode)
    }

    override fun onClickMeetingInfoButton() {
        _navigateAction.setValue(IntroNavigateAction.NavigateToMeetingInfo)
    }
}
