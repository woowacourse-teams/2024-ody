package com.woowacourse.ody.data.remote.service

import com.woowacourse.ody.data.model.join.JoinRequest
import com.woowacourse.ody.data.model.meeting.MeetingResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface JoinService {
    @POST("mates")
    suspend fun postMates(
        @Body joinRequest: JoinRequest,
    ): MeetingResponse
}
