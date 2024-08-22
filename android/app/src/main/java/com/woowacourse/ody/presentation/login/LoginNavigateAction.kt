package com.woowacourse.ody.presentation.login

sealed interface LoginNavigateAction {
    data object NavigateToMeetings : LoginNavigateAction
}
