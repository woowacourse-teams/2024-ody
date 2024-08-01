package com.woowacourse.ody.presentation.join

sealed interface MeetingJoinNavigateAction {
    data object JoinNavigateToRoom : MeetingJoinNavigateAction

    data object JoinNavigateToJoinComplete : MeetingJoinNavigateAction
}
