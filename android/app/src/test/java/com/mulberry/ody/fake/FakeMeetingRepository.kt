package com.mulberry.ody.fake

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.model.Meeting
import com.mulberry.ody.domain.model.MeetingCatalog
import com.mulberry.ody.domain.model.MeetingCreationInfo
import com.mulberry.ody.domain.model.Nudge
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.inviteCode
import com.mulberry.ody.mateEtaInfo
import com.mulberry.ody.meeting
import com.mulberry.ody.meetingCatalogs

object FakeMeetingRepository : MeetingRepository {
    override suspend fun fetchInviteCodeValidity(inviteCode: String): ApiResult<Unit> =
        if (inviteCode.length <= 8) {
            ApiResult.Success(Unit)
        } else {
            ApiResult.Failure(404, "")
        }

    override suspend fun postMeeting(meetingCreationInfo: MeetingCreationInfo): ApiResult<String> {
        return ApiResult.Success(inviteCode)
    }

    override suspend fun patchMatesEta(
        meetingId: Long,
        isMissing: Boolean,
        currentLatitude: String,
        currentLongitude: String,
    ): Result<MateEtaInfo> {
        return Result.success(mateEtaInfo)
    }

    override suspend fun fetchMeetingCatalogs(): ApiResult<List<MeetingCatalog>> {
        return ApiResult.Success(meetingCatalogs)
    }

    override suspend fun fetchMeeting(meetingId: Long): ApiResult<Meeting> = ApiResult.Success(meeting)

    override suspend fun postNudge(nudge: Nudge): ApiResult<Unit> = ApiResult.Success(Unit)
}
