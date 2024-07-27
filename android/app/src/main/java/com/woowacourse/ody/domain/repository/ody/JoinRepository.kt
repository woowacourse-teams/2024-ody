package com.woowacourse.ody.domain.repository.ody

import com.woowacourse.ody.data.remote.ody.entity.join.request.JoinRequest
import com.woowacourse.ody.data.remote.ody.entity.meeting.response.MeetingResponse

interface JoinRepository {
    suspend fun postMates(joinRequest: JoinRequest): Result<MeetingResponse>
}
