package com.mulberry.ody.fake

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.EtaOpenInfo
import com.mulberry.ody.domain.model.MeetingDateTime
import com.mulberry.ody.domain.model.MeetingJoinInfo
import com.mulberry.ody.domain.repository.ody.JoinRepository
import java.time.LocalDateTime

class FakeJoinRepository(private val meetingId: Long) : JoinRepository {
    override suspend fun joinMeeting(meetingJoinInfo: MeetingJoinInfo): ApiResult<EtaOpenInfo> {
        return ApiResult.Success(
            EtaOpenInfo(
                meetingId = meetingId,
                meetingDateTime = MeetingDateTime(LocalDateTime.now()),
            ),
        )
    }
}
