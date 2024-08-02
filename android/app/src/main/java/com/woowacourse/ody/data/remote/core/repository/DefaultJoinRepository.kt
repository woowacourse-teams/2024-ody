package com.woowacourse.ody.data.remote.core.repository

import com.woowacourse.ody.data.remote.core.entity.join.request.JoinRequest
import com.woowacourse.ody.data.remote.core.entity.meeting.response.MeetingResponse
import com.woowacourse.ody.data.remote.core.service.JoinService
import com.woowacourse.ody.domain.repository.ody.JoinRepository

class DefaultJoinRepository(private val service: JoinService) : JoinRepository {
    override suspend fun postMates(joinRequest: JoinRequest): Result<MeetingResponse> = runCatching { service.postMates(joinRequest) }
}
