package com.woowacourse.ody.data.remote.core.repository

import com.woowacourse.ody.data.remote.core.RetrofitClient
import com.woowacourse.ody.data.remote.core.entity.meeting.request.MeetingRequest
import com.woowacourse.ody.data.remote.core.entity.meeting.response.MeetingResponse
import com.woowacourse.ody.data.remote.core.entity.toMeeting
import com.woowacourse.ody.data.remote.core.service.MeetingService
import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.domain.repository.ody.MeetingRepository

object DefaultMeetingRepository : MeetingRepository {
    private val service = RetrofitClient.retrofit.create(MeetingService::class.java)

    override suspend fun fetchInviteCodeValidity(inviteCode: String): Result<Unit> {
        return runCatching { service.getInviteCodeValidity(inviteCode) }
    }

    override suspend fun fetchMeeting(): Result<List<Meeting>> = runCatching { service.getMeeting().meetings.map { it.toMeeting() } }

    override suspend fun postMeeting(meetingRequest: MeetingRequest): Result<MeetingResponse> =
        runCatching { service.postMeeting(meetingRequest) }
}
