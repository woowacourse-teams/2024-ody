package com.mulberry.ody.presentation.feature.creation.model

sealed interface MeetingCreationNavigateAction {
    data object NavigateToMeetings : MeetingCreationNavigateAction

    data class NavigateToMeetingJoin(val inviteCode: String) : MeetingCreationNavigateAction
}
