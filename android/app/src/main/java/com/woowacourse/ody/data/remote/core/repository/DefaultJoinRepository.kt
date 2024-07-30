package com.woowacourse.ody.data.remote.core.repository

import com.woowacourse.ody.data.remote.core.RetrofitClient
import com.woowacourse.ody.data.remote.core.entity.join.request.JoinRequest
import com.woowacourse.ody.data.remote.core.entity.meeting.response.MeetingResponse
import com.woowacourse.ody.data.remote.core.service.JoinService
import com.woowacourse.ody.domain.repository.ody.JoinRepository

object DefaultJoinRepository : JoinRepository {
    private val service = RetrofitClient.retrofit.create(JoinService::class.java)

    override suspend fun postMates(joinRequest: JoinRequest): Result<MeetingResponse> = runCatching { service.postMates(joinRequest) }
}
