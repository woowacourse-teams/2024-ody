package com.woowacourse.ody.domain.repository

import com.woowacourse.ody.data.remote.entity.meeting.MeetingRequest
import com.woowacourse.ody.data.remote.entity.meeting.MeetingResponse
import com.woowacourse.ody.domain.model.Meeting

interface MeetingRepository {
    suspend fun fetchInviteCodeValidity(inviteCode: String): Result<Unit>

    suspend fun fetchMeeting(): Result<List<Meeting>>

    suspend fun postMeeting(meetingRequest: MeetingRequest): Result<MeetingResponse>
}
