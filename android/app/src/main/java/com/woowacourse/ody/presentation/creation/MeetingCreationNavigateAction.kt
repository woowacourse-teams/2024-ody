package com.woowacourse.ody.presentation.creation

sealed interface MeetingCreationNavigateAction {
    data object navigateToMeetings : MeetingCreationNavigateAction

    data object NavigateToCreationComplete : MeetingCreationNavigateAction
}
