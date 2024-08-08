package com.woowacourse.ody.presentation.meetings

sealed interface MeetingsNavigateAction {
    data class NavigateToNotificationLog(val meetingId: Long) : MeetingsNavigateAction

    data class NavigateToEta(val meetingId: Long, val inviteCode: String, val title: String) : MeetingsNavigateAction
}
