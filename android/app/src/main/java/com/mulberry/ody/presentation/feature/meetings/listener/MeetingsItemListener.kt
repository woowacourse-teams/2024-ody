package com.mulberry.ody.presentation.feature.meetings.listener

interface MeetingsItemListener {
    fun navigateToNotificationLog(meetingId: Long)

    fun navigateToEtaDashboard(meetingId: Long)
}
