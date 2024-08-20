package com.woowacourse.ody.data.remote.core.repository

import com.woowacourse.ody.data.remote.core.entity.meeting.mapper.toMateEtaInfo
import com.woowacourse.ody.data.remote.core.entity.meeting.mapper.toMeeting
import com.woowacourse.ody.data.remote.core.entity.meeting.mapper.toMeetingCatalogs
import com.woowacourse.ody.data.remote.core.entity.meeting.mapper.toMeetingRequest
import com.woowacourse.ody.data.remote.core.entity.meeting.request.MatesEtaRequest
import com.woowacourse.ody.data.remote.core.service.MeetingService
import com.woowacourse.ody.domain.apiresult.ApiResult
import com.woowacourse.ody.domain.apiresult.map
import com.woowacourse.ody.domain.model.MateEtaInfo
import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.domain.model.MeetingCatalog
import com.woowacourse.ody.domain.model.MeetingCreationInfo
import com.woowacourse.ody.domain.repository.ody.MeetingRepository

class DefaultMeetingRepository(private val service: MeetingService) : MeetingRepository {
    override suspend fun fetchInviteCodeValidity(inviteCode: String): ApiResult<Unit> {
        return service.fetchInviteCodeValidity(inviteCode)
    }

    override suspend fun fetchMeeting(meetingId: Long): ApiResult<Meeting> {
        return service.fetchMeeting(meetingId).map { it.toMeeting() }
    }

    override suspend fun fetchNudge(mateId: Long): ApiResult<Unit> = service.fetchNudge(mateId)

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
                MatesEtaRequest(isMissing, compress(currentLatitude), compress(currentLongitude)),
            ).toMateEtaInfo()
        }
    }

    override suspend fun fetchMeetingCatalogs(): ApiResult<List<MeetingCatalog>> =
        service.fetchMeetingCatalogs().map { it.toMeetingCatalogs() }

    private fun compress(coordinate: String): String {
        val endIndex = minOf(9, coordinate.length)
        return coordinate.substring(0, endIndex)
    }
}
