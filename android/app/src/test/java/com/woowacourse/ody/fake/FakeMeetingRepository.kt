package com.woowacourse.ody.fake

import com.woowacourse.ody.domain.apiresult.ApiResult
import com.woowacourse.ody.domain.model.MateEtaInfo
import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.domain.model.MeetingCatalog
import com.woowacourse.ody.domain.model.MeetingCreationInfo
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import com.woowacourse.ody.inviteCode
import com.woowacourse.ody.mateEtaInfo
import com.woowacourse.ody.meeting
import com.woowacourse.ody.meetingCatalogs

object FakeMeetingRepository : MeetingRepository {
    override suspend fun fetchInviteCodeValidity(inviteCode: String): Result<Unit> =
        if (inviteCode.length <= 8) {
            Result.success(Unit)
        } else {
            Result.failure(Exception())
        }

    override suspend fun postMeeting(meetingCreationInfo: MeetingCreationInfo): Result<String> {
        return Result.success(inviteCode)
    }

    override suspend fun patchMatesEta(
        meetingId: Long,
        isMissing: Boolean,
        currentLatitude: String,
        currentLongitude: String,
    ): Result<MateEtaInfo> {
        return Result.success(mateEtaInfo)
    }

    override suspend fun fetchMeetingCatalogs(): Result<List<MeetingCatalog>> {
        return Result.success(meetingCatalogs)
    }

    override suspend fun fetchMeetingCatalogs2(): ApiResult<List<MeetingCatalog>> {
        return ApiResult.Success(meetingCatalogs)
    }

    override suspend fun fetchMeeting(meetingId: Long): Result<Meeting> = Result.success(meeting)
}
