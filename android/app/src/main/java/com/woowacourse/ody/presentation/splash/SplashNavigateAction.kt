package com.woowacourse.ody.presentation.splash

sealed interface SplashNavigateAction {
    data object NavigateToIntro : SplashNavigateAction

    data object NavigateToNotificationLog : SplashNavigateAction
}
