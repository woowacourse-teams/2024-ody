package com.woowacourse.ody.domain.repository.ody

import com.woowacourse.ody.data.remote.core.entity.join.request.JoinRequest
import com.woowacourse.ody.data.remote.core.entity.meeting.response.MeetingResponse

interface JoinRepository {
    suspend fun postMates(joinRequest: JoinRequest): Result<MeetingResponse>
}
