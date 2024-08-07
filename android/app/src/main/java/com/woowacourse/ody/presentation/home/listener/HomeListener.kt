package com.woowacourse.ody.presentation.home.listener

interface HomeListener {
    fun onFab()

    fun onJoinMeeting()

    fun onCreateMeeting()

    fun navigateToMeetingRoom(meetingId: Long)

    fun guideItemDisabled()
}
