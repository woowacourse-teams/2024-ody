package com.mulberry.ody.domain.repository.ody

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.MeetingJoinInfo
import com.mulberry.ody.domain.model.EtaOpenInfo

interface JoinRepository {
    suspend fun joinMeeting(meetingJoinInfo: MeetingJoinInfo): ApiResult<EtaOpenInfo>
}
