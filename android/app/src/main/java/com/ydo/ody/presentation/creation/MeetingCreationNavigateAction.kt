package com.ydo.ody.presentation.creation

sealed interface MeetingCreationNavigateAction {
    data object NavigateToMeetings : MeetingCreationNavigateAction

    data object NavigateToCreationComplete : MeetingCreationNavigateAction
}
