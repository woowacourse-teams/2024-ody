package com.woowacourse.ody.domain.repository.ody

import com.woowacourse.ody.data.apiresult.ApiResult
import com.woowacourse.ody.domain.model.MateEtaInfo
import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.domain.model.MeetingCatalog
import com.woowacourse.ody.domain.model.MeetingCreationInfo

interface MeetingRepository {
    suspend fun fetchInviteCodeValidity(inviteCode: String): Result<Unit>

    suspend fun postMeeting(meetingCreationInfo: MeetingCreationInfo): Result<String>

    suspend fun patchMatesEta(
        meetingId: Long,
        isMissing: Boolean,
        currentLatitude: String,
        currentLongitude: String,
    ): Result<MateEtaInfo>

    suspend fun fetchMeetingCatalogs(): Result<List<MeetingCatalog>>

    suspend fun fetchMeetingCatalogs2(): ApiResult<List<MeetingCatalog>>

    suspend fun fetchMeeting(meetingId: Long): Result<Meeting>
}
