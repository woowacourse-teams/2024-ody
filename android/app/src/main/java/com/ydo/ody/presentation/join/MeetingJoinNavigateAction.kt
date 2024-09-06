package com.ydo.ody.presentation.join

sealed interface MeetingJoinNavigateAction {
    data class JoinNavigateToRoom(val meetingId: Long) : MeetingJoinNavigateAction

    data object JoinNavigateToJoinComplete : MeetingJoinNavigateAction
}
