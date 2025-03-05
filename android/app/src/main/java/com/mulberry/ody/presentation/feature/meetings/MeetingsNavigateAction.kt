package com.mulberry.ody.presentation.feature.meetings

sealed interface MeetingsNavigateAction {
    data class NavigateToNotificationLog(val meetingId: Long) : MeetingsNavigateAction

    data class NavigateToEtaDashboard(val meetingId: Long) : MeetingsNavigateAction

    data object NavigateToLogin : MeetingsNavigateAction

    data object NavigateToJoinMeeting : MeetingsNavigateAction

    data object NavigateToCreateMeeting : MeetingsNavigateAction

    data object NavigateToSetting : MeetingsNavigateAction
}
