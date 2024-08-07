package com.woowacourse.ody.presentation.meetings.listener

interface MeetingsListener {
    fun onFab()

    fun onJoinMeeting()

    fun onCreateMeeting()

    fun navigateToMeetingRoom(meetingId: Long)

    fun guideItemDisabled()
}
