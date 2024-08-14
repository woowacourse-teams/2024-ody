package com.woowacourse.ody.presentation.meetings.listener

interface MeetingsItemListener {
    fun navigateToNotificationLog(meetingId: Long)

    fun navigateToEtaDashboard(meetingId: Long)

    fun toggleFold(position: Int)
}
