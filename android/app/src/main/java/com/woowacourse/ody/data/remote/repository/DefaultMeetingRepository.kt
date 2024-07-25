package com.woowacourse.ody.data.remote.repository

import com.woowacourse.ody.data.model.meeting.MeetingRequest
import com.woowacourse.ody.data.model.meeting.MeetingResponse
import com.woowacourse.ody.data.model.toMeeting
import com.woowacourse.ody.data.remote.RetrofitClient
import com.woowacourse.ody.data.remote.service.MeetingService
import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.domain.repository.MeetingRepository

object DefaultMeetingRepository : MeetingRepository {
    private val service = RetrofitClient.retrofit.create(MeetingService::class.java)

    override suspend fun fetchInviteCodeValidity(inviteCode: String): Result<Unit> {
        return runCatching { service.getInviteCodeValidity(inviteCode) }
    }

    override suspend fun fetchMeeting(): Result<List<Meeting>> = runCatching { service.getMeeting().meetings.map { it.toMeeting() } }

    override suspend fun postMeeting(meetingRequest: MeetingRequest): Result<MeetingResponse> =
        runCatching { service.postMeeting(meetingRequest) }
}
