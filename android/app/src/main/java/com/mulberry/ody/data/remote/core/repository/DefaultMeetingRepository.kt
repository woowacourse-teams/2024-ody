package com.mulberry.ody.data.remote.core.repository

import com.mulberry.ody.data.remote.core.entity.meeting.mapper.toMateEtaInfo
import com.mulberry.ody.data.remote.core.entity.meeting.mapper.toMeeting
import com.mulberry.ody.data.remote.core.entity.meeting.mapper.toMeetingCatalogs
import com.mulberry.ody.data.remote.core.entity.meeting.mapper.toMeetingRequest
import com.mulberry.ody.data.remote.core.entity.meeting.mapper.toNudgeRequest
import com.mulberry.ody.data.remote.core.entity.meeting.request.MatesEtaRequest
import com.mulberry.ody.data.remote.core.service.MeetingService
import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.apiresult.map
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.model.Meeting
import com.mulberry.ody.domain.model.MeetingCatalog
import com.mulberry.ody.domain.model.MeetingCreationInfo
import com.mulberry.ody.domain.model.Nudge
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import javax.inject.Inject

class DefaultMeetingRepository
    @Inject
    constructor(
        private val service: MeetingService,
    ) : MeetingRepository {
        override suspend fun fetchInviteCodeValidity(inviteCode: String): ApiResult<Unit> {
            return service.fetchInviteCodeValidity(inviteCode)
        }

        override suspend fun fetchMeeting(meetingId: Long): ApiResult<Meeting> {
            return service.fetchMeeting(meetingId).map { it.toMeeting() }
        }

        override suspend fun postNudge(nudge: Nudge): ApiResult<Unit> = service.postNudge(nudge.toNudgeRequest())

        override suspend fun postMeeting(meetingCreationInfo: MeetingCreationInfo): ApiResult<String> =
            service.postMeeting(meetingCreationInfo.toMeetingRequest()).map { it.inviteCode }

        override suspend fun patchMatesEta(
            meetingId: Long,
            isMissing: Boolean,
            currentLatitude: String,
            currentLongitude: String,
        ): Result<MateEtaInfo> {
            return runCatching {
                service.patchMatesEta(
                    meetingId,
                    MatesEtaRequest(isMissing, currentLatitude, currentLongitude),
                ).toMateEtaInfo()
            }
        }

        override suspend fun fetchMeetingCatalogs(): ApiResult<List<MeetingCatalog>> =
            service.fetchMeetingCatalogs().map { it.toMeetingCatalogs() }
    }
