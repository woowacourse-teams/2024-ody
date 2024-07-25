package com.woowacourse.ody.domain.repository

import com.woowacourse.ody.data.model.join.JoinRequest
import com.woowacourse.ody.data.model.meeting.MeetingResponse

interface JoinRepository {
    suspend fun postMates(joinRequest: JoinRequest): Result<MeetingResponse>
}
