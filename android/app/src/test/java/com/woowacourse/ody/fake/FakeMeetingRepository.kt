package com.woowacourse.ody.fake

import com.woowacourse.ody.domain.apiresult.ApiResult
import com.woowacourse.ody.domain.model.EtaType
import com.woowacourse.ody.domain.model.Mate
import com.woowacourse.ody.domain.model.MateEta
import com.woowacourse.ody.domain.model.MateEtaInfo
import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.domain.model.MeetingCatalog
import com.woowacourse.ody.domain.model.MeetingCreationInfo
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import com.woowacourse.ody.inviteCode
import com.woowacourse.ody.meeting
import java.time.LocalDate
import java.time.LocalTime

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
        return Result.success(
            MateEtaInfo(
                userNickname = "mateA",
                mateEtas =
                    listOf(
                        MateEta("mateA", EtaType.LATE, 1L),
                        MateEta("mateB", EtaType.LATE, 1L),
                        MateEta("mateC", EtaType.LATE, 1L),
                    ),
            ),
        )
    }

    override suspend fun fetchMeetingCatalogs(): Result<List<MeetingCatalog>> =
        Result.success(
            emptyList(),
        )

    override suspend fun fetchMeetingCatalogs2(): ApiResult<List<MeetingCatalog>> =
        ApiResult.Success(
            emptyList(),
        )

    override suspend fun fetchMeeting(meetingId: Long): Result<Meeting> = Result.success(meeting)
}
