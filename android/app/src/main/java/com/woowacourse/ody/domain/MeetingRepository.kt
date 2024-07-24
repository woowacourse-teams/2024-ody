package com.woowacourse.ody.domain

interface MeetingRepository {
    suspend fun fetchMeeting(): Meeting
}
