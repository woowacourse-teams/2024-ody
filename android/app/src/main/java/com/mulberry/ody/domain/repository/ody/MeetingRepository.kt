package com.mulberry.ody.domain.repository.ody

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.DetailMeeting
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.model.Meeting
import com.mulberry.ody.domain.model.MeetingCreationInfo
import com.mulberry.ody.domain.model.Nudge

interface MeetingRepository {
    suspend fun fetchInviteCodeValidity(inviteCode: String): ApiResult<Unit>

    suspend fun postMeeting(meetingCreationInfo: MeetingCreationInfo): ApiResult<String>

    suspend fun patchMatesEta(
        meetingId: Long,
        isMissing: Boolean,
        currentLatitude: String,
        currentLongitude: String,
    ): ApiResult<MateEtaInfo>

    suspend fun upsertMateEta(
        meetingId: Long,
        mateEtaInfo: MateEtaInfo,
    ): ApiResult<Unit>

    suspend fun fetchMeetings(): ApiResult<List<Meeting>>

    suspend fun fetchMeeting(meetingId: Long): ApiResult<DetailMeeting>

    suspend fun postNudge(nudge: Nudge): ApiResult<Unit>

    suspend fun exitMeeting(meetingId: Long): ApiResult<Unit>
}
