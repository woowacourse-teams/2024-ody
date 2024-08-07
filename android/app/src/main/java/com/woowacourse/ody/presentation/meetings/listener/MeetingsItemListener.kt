package com.woowacourse.ody.presentation.meetings.listener

import kotlinx.coroutines.Job

interface MeetingsItemListener {
    fun navigateToMeetingRoom(meetingId: Long)

    fun navigateToEta(meetingId: Long): Job

    fun toggleFold(position: Int)
}
