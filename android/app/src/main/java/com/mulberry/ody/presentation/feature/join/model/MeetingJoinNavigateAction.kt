package com.mulberry.ody.presentation.feature.join.model

sealed interface MeetingJoinNavigateAction {
    data class JoinNavigateToRoom(val meetingId: Long) : MeetingJoinNavigateAction

    data object JoinNavigateToJoinComplete : MeetingJoinNavigateAction
}
