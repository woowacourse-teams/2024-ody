package com.mulberry.ody.presentation.feature.login

sealed interface LoginNavigateAction {
    data object NavigateToMeetings : LoginNavigateAction
}
