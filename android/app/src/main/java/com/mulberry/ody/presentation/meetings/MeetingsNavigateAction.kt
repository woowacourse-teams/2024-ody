package com.mulberry.ody.presentation.meetings

sealed interface MeetingsNavigateAction {
    data class NavigateToNotificationLog(val meetingId: Long) : MeetingsNavigateAction

    data class NavigateToEtaDashboard(val meetingId: Long) : MeetingsNavigateAction

    data object NavigateToLogin : MeetingsNavigateAction
}
