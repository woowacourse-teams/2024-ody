package com.woowacourse.ody.data.remote.core.service

import com.woowacourse.ody.data.remote.core.entity.join.request.JoinRequest
import com.woowacourse.ody.data.remote.core.entity.meeting.response.MeetingResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface JoinService {
    @POST("mates")
    suspend fun postMates(
        @Body joinRequest: JoinRequest,
    ): MeetingResponse
}
