package com.woowacourse.ody.data.remote.ody.repository

import com.woowacourse.ody.data.remote.ody.entity.join.request.JoinRequest
import com.woowacourse.ody.data.remote.ody.entity.meeting.response.MeetingResponse
import com.woowacourse.ody.data.remote.RetrofitClient
import com.woowacourse.ody.data.remote.ody.service.JoinService
import com.woowacourse.ody.domain.repository.ody.JoinRepository

object DefaultJoinRepository : JoinRepository {
    private val service = RetrofitClient.retrofit.create(JoinService::class.java)

    override suspend fun postMates(joinRequest: JoinRequest): Result<MeetingResponse> = runCatching { service.postMates(joinRequest) }
}
