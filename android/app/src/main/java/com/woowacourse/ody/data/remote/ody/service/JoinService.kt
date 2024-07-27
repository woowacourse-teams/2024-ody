package com.woowacourse.ody.data.remote.ody.service

import com.woowacourse.ody.data.remote.ody.entity.join.request.JoinRequest
import com.woowacourse.ody.data.remote.ody.entity.meeting.response.MeetingResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface JoinService {
    @POST("mates")
    suspend fun postMates(
        @Body joinRequest: JoinRequest,
    ): MeetingResponse
}
