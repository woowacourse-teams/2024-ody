package com.ydo.ody.fake

import com.ydo.ody.domain.apiresult.ApiResult
import com.ydo.ody.domain.model.MateEtaInfo
import com.ydo.ody.domain.model.Meeting
import com.ydo.ody.domain.model.MeetingCatalog
import com.ydo.ody.domain.model.MeetingCreationInfo
import com.ydo.ody.domain.model.Nudge
import com.ydo.ody.domain.repository.ody.MeetingRepository
import com.ydo.ody.inviteCode
import com.ydo.ody.mateEtaInfo
import com.ydo.ody.meeting
import com.ydo.ody.meetingCatalogs

object FakeMeetingRepository : MeetingRepository {
    override suspend fun fetchInviteCodeValidity(inviteCode: String): ApiResult<Unit> =
        if (inviteCode.length <= 8) {
            ApiResult.Success(Unit)
        } else {
            ApiResult.Failure(400, "")
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
