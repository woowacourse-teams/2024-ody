package com.woowacourse.ody.data.remote.core.repository

import com.woowacourse.ody.data.ApiResult.ApiResult
import com.woowacourse.ody.data.ApiResult.map
import com.woowacourse.ody.data.remote.core.entity.meeting.mapper.toMateEtaInfo
import com.woowacourse.ody.data.remote.core.entity.meeting.mapper.toMeeting
import com.woowacourse.ody.data.remote.core.entity.meeting.mapper.toMeetingCatalogs
import com.woowacourse.ody.data.remote.core.entity.meeting.mapper.toMeetingRequest
import com.woowacourse.ody.data.remote.core.entity.meeting.request.MatesEtaRequest
import com.woowacourse.ody.data.remote.core.service.MeetingService
import com.woowacourse.ody.domain.model.MateEtaInfo
import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.domain.model.MeetingCatalog
import com.woowacourse.ody.domain.model.MeetingCreationInfo
import com.woowacourse.ody.domain.repository.ody.MeetingRepository

class DefaultMeetingRepository(private val service: MeetingService) : MeetingRepository {
    override suspend fun fetchInviteCodeValidity(inviteCode: String): Result<Unit> {
        return runCatching { service.getInviteCodeValidity(inviteCode) }
    }

    override suspend fun fetchMeeting(meetingId: Long): Result<Meeting> = runCatching { service.fetchMeeting(meetingId).toMeeting() }

    override suspend fun postMeeting(meetingCreationInfo: MeetingCreationInfo): Result<String> =
        runCatching { service.postMeeting(meetingCreationInfo.toMeetingRequest()).inviteCode }

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

    override suspend fun fetchMeetingCatalogs(): Result<List<MeetingCatalog>> =
        runCatching { service.fetchMeetingCatalogs().toMeetingCatalogs() }

    override suspend fun fetchMeetingCatalogs2(): ApiResult<List<MeetingCatalog>> =
        service.fetchMeetingCatalogs2().map { it.toMeetingCatalogs() }

    private fun compress(coordinate: String): String {
        val endIndex = minOf(9, coordinate.length)
        return coordinate.substring(0, endIndex)
    }
}
