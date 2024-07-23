package com.woowacourse.ody.presentation.intro

sealed interface IntroNavigateAction {
    data object NavigateToInviteCode : IntroNavigateAction

    data object NavigateToMeetingInfo : IntroNavigateAction
}
