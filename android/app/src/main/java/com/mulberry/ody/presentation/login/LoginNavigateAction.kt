package com.mulberry.ody.presentation.login

sealed interface LoginNavigateAction {
    data object NavigateToMeetings : LoginNavigateAction
}
