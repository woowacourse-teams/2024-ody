package com.woowacourse.ody.presentation.splash

sealed interface SplashNavigateAction {
    data object NavigateToLogin : SplashNavigateAction
}
