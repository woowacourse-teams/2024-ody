package com.woowacourse.ody.presentation.meetings.listener

interface MeetingsItemListener {
    fun navigateToMeetingRoom(meetingId: Long)

    fun navigateToEta(meetingId: Long)

    fun toggleFold(position: Int)
}
