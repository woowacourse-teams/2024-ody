package com.mulberry.ody.fake

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.model.DetailMeeting
import com.mulberry.ody.domain.model.MeetingCatalog
import com.mulberry.ody.domain.model.MeetingCreationInfo
import com.mulberry.ody.domain.model.Nudge
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.inviteCode
import com.mulberry.ody.mateEtaInfo
import com.mulberry.ody.detailMeeting
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
    ): ApiResult<MateEtaInfo> {
        return ApiResult.Success(mateEtaInfo)
    }

    override suspend fun upsertMateEta(
        meetingId: Long,
        mateEtaInfo: MateEtaInfo,
    ): ApiResult<Unit> {
        return ApiResult.Success(Unit)
    }

    override suspend fun fetchMeetingCatalogs(): ApiResult<List<MeetingCatalog>> {
        return ApiResult.Success(meetingCatalogs)
    }

    override suspend fun fetchMeeting(meetingId: Long): ApiResult<DetailMeeting> = ApiResult.Success(detailMeeting)

    override suspend fun postNudge(nudge: Nudge): ApiResult<Unit> = ApiResult.Success(Unit)

    override suspend fun exitMeeting(meetingId: Long): ApiResult<Unit> = ApiResult.Success(Unit)
}
