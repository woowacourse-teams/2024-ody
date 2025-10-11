package com.mulberry.ody.fake

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.EtaOpenInfo
import com.mulberry.ody.domain.model.MeetingJoinInfo
import com.mulberry.ody.domain.repository.ody.JoinRepository
import java.time.LocalDateTime

class FakeJoinRepository(private val meetingId: Long) : JoinRepository {
    override suspend fun postMates(meetingJoinInfo: MeetingJoinInfo): ApiResult<EtaOpenInfo> {
        return ApiResult.Success(
            EtaOpenInfo(
                meetingId = meetingId,
                meetingDateTime = LocalDateTime.of(2024, 7, 28, 18, 0),
            ),
        )
    }
}
