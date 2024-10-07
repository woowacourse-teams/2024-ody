package com.mulberry.ody.presentation.creation

sealed interface MeetingCreationNavigateAction {
    data object NavigateToMeetings : MeetingCreationNavigateAction

    data class NavigateToMeetingJoin(val inviteCode: String) : MeetingCreationNavigateAction
}
