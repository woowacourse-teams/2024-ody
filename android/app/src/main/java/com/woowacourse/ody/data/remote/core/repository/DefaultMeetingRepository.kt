package com.woowacourse.ody.data.remote.core.repository

import com.woowacourse.ody.data.remote.core.entity.meeting.mapper.toMateEtas
import com.woowacourse.ody.data.remote.core.entity.meeting.mapper.toMeeting
import com.woowacourse.ody.data.remote.core.entity.meeting.mapper.toMeetingCatalogs
import com.woowacourse.ody.data.remote.core.entity.meeting.mapper.toMeetingRequest
import com.woowacourse.ody.data.remote.core.entity.meeting.request.MatesEtaRequest
import com.woowacourse.ody.data.remote.core.service.MeetingService
import com.woowacourse.ody.domain.model.MateEta
import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.domain.model.MeetingCatalog
import com.woowacourse.ody.domain.model.MeetingCreationInfo
import com.woowacourse.ody.domain.repository.ody.MeetingRepository

class DefaultMeetingRepository(private val service: MeetingService) : MeetingRepository {
    override suspend fun fetchInviteCodeValidity(inviteCode: String): Result<Unit> {
        return runCatching { service.getInviteCodeValidity(inviteCode) }
    }

    override suspend fun fetchMeeting(): Result<List<Meeting>> {
        return runCatching { service.getMeeting().meetings.map { it.toMeeting() } }
    }

    override suspend fun postMeeting(meetingCreationInfo: MeetingCreationInfo): Result<String> =
        runCatching { service.postMeeting(meetingCreationInfo.toMeetingRequest()).inviteCode }

    override suspend fun patchMatesEta(
        meetingId: Long,
        isMissing: Boolean,
        currentLatitude: String,
        currentLongitude: String,
    ): Result<List<MateEta>> {
        return runCatching {
            service.patchMatesEta(
                meetingId,
                MatesEtaRequest(isMissing, currentLatitude, currentLongitude),
            ).toMateEtas()
        }
    }

    override suspend fun fetchMeetingCatalogs(): Result<List<MeetingCatalog>> =
        runCatching {
            service.fetchMeetingCatalogs().toMeetingCatalogs()
        }
}
