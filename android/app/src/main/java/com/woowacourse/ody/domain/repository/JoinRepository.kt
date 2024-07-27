package com.woowacourse.ody.domain.repository

import com.woowacourse.ody.data.remote.entity.join.JoinRequest
import com.woowacourse.ody.data.remote.entity.meeting.MeetingResponse

interface JoinRepository {
    suspend fun postMates(joinRequest: JoinRequest): Result<MeetingResponse>
}
