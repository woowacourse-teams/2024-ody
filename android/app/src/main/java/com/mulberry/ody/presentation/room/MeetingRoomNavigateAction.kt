package com.mulberry.ody.presentation.room

sealed interface MeetingRoomNavigateAction {
    data object NavigateToEtaDashboard : MeetingRoomNavigateAction

    data object NavigateToNotificationLog : MeetingRoomNavigateAction
}
