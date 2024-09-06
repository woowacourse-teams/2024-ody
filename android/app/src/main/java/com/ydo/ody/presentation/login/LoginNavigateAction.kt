package com.ydo.ody.presentation.login

sealed interface LoginNavigateAction {
    data object NavigateToMeetings : LoginNavigateAction
}
