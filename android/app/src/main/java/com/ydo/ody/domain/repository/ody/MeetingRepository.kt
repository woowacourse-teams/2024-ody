package com.ydo.ody.domain.repository.ody

import com.ydo.ody.domain.apiresult.ApiResult
import com.ydo.ody.domain.model.MateEtaInfo
import com.ydo.ody.domain.model.Meeting
import com.ydo.ody.domain.model.MeetingCatalog
import com.ydo.ody.domain.model.MeetingCreationInfo
import com.ydo.ody.domain.model.Nudge

interface MeetingRepository {
    suspend fun fetchInviteCodeValidity(inviteCode: String): ApiResult<Unit>

    suspend fun postMeeting(meetingCreationInfo: MeetingCreationInfo): ApiResult<String>

    suspend fun patchMatesEta(
        meetingId: Long,
        isMissing: Boolean,
        currentLatitude: String,
        currentLongitude: String,
    ): Result<MateEtaInfo>

    suspend fun fetchMeetingCatalogs(): ApiResult<List<MeetingCatalog>>

    suspend fun fetchMeeting(meetingId: Long): ApiResult<Meeting>

    suspend fun postNudge(nudge: Nudge): ApiResult<Unit>
}
