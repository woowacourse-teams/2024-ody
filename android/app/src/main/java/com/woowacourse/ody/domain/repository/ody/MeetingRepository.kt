package com.woowacourse.ody.domain.repository.ody

import com.woowacourse.ody.data.remote.ody.entity.meeting.request.MeetingRequest
import com.woowacourse.ody.data.remote.ody.entity.meeting.response.MeetingResponse
import com.woowacourse.ody.domain.model.Meeting

interface MeetingRepository {
    suspend fun fetchInviteCodeValidity(inviteCode: String): Result<Unit>

    suspend fun fetchMeeting(): Result<List<Meeting>>

    suspend fun postMeeting(meetingRequest: MeetingRequest): Result<MeetingResponse>
}
