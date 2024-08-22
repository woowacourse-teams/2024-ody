package com.woowacourse.ody.fake

import com.woowacourse.ody.domain.apiresult.ApiResult
import com.woowacourse.ody.domain.model.MateEtaInfo
import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.domain.model.MeetingCatalog
import com.woowacourse.ody.domain.model.MeetingCreationInfo
import com.woowacourse.ody.domain.model.Nudge
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import com.woowacourse.ody.inviteCode
import com.woowacourse.ody.mateEtaInfo
import com.woowacourse.ody.meeting
import com.woowacourse.ody.meetingCatalogs

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
