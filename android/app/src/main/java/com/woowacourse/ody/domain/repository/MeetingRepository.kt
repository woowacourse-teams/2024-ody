package com.woowacourse.ody.domain.repository

import com.woowacourse.ody.data.model.meeting.MeetingRequest
import com.woowacourse.ody.data.model.meeting.MeetingResponse
import com.woowacourse.ody.domain.Meeting

interface MeetingRepository {
    suspend fun fetchInviteCodeValidity(inviteCode: String): Result<Unit>

    suspend fun fetchMeeting(): Result<List<Meeting>>

    suspend fun postMeeting(meetingRequest: MeetingRequest): Result<MeetingResponse>
}
