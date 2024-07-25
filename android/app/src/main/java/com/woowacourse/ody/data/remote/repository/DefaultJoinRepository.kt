package com.woowacourse.ody.data.remote.repository

import com.woowacourse.ody.data.model.join.JoinRequest
import com.woowacourse.ody.data.model.meeting.MeetingResponse
import com.woowacourse.ody.data.remote.RetrofitClient
import com.woowacourse.ody.data.remote.service.JoinService
import com.woowacourse.ody.domain.repository.JoinRepository

object DefaultJoinRepository : JoinRepository {
    private val service = RetrofitClient.retrofit.create(JoinService::class.java)

    override suspend fun postMates(joinRequest: JoinRequest): Result<MeetingResponse> = runCatching { service.postMates(joinRequest) }
}
