package com.mulberry.ody.presentation.feature.room

sealed interface MeetingRoomNavigateAction {
    data object NavigateToEtaDashboard : MeetingRoomNavigateAction

    data object NavigateToNotificationLog : MeetingRoomNavigateAction
}
