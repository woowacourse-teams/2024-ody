package com.woowacourse.ody.fake

import com.woowacourse.ody.domain.apiresult.ApiResult
import com.woowacourse.ody.domain.model.MeetingJoinInfo
import com.woowacourse.ody.domain.model.ReserveInfo
import com.woowacourse.ody.domain.repository.ody.JoinRepository
import java.time.LocalDateTime

class FakeJoinRepository(private val meetingId: Long) : JoinRepository {
    override suspend fun postMates(meetingJoinInfo: MeetingJoinInfo): ApiResult<ReserveInfo> {
        return ApiResult.Success(
            ReserveInfo(
                meetingId = meetingId,
                meetingDateTime = LocalDateTime.of(2024, 7, 28, 18, 0),
            ),
        )
    }
}
