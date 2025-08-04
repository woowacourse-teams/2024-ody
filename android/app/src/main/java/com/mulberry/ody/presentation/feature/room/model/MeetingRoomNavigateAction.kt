package com.mulberry.ody.presentation.feature.room.model

sealed interface MeetingRoomNavigateAction {
    data object NavigateToEtaDashboard : MeetingRoomNavigateAction

    data object NavigateToNotificationLog : MeetingRoomNavigateAction
}
