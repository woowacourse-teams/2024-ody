package com.woowacourse.ody.data.remote.core.repository

import com.woowacourse.ody.data.remote.core.entity.meeting.request.toMeetingRequest
import com.woowacourse.ody.data.remote.core.entity.meeting.response.toMeeting
import com.woowacourse.ody.data.remote.core.service.MeetingService
import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.domain.model.MeetingCreationInfo
import com.woowacourse.ody.domain.repository.ody.MeetingRepository

class DefaultMeetingRepository(private val service: MeetingService) : MeetingRepository {
    override suspend fun fetchInviteCodeValidity(inviteCode: String): Result<Unit> {
        return runCatching { service.getInviteCodeValidity(inviteCode) }
    }

    override suspend fun fetchMeeting(): Result<List<Meeting>> = runCatching { service.getMeeting().meetings.map { it.toMeeting() } }

    override suspend fun postMeeting(meetingCreationInfo: MeetingCreationInfo): Result<Meeting> =
        runCatching { service.postMeeting(meetingCreationInfo.toMeetingRequest()).toMeeting() }
}
