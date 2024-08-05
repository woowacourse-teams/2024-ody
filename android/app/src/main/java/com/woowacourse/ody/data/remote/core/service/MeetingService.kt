package com.woowacourse.ody.data.remote.core.service

import com.woowacourse.ody.data.remote.core.entity.meeting.request.MeetingRequest
import com.woowacourse.ody.data.remote.core.entity.meeting.response.MeetingCreationResponse
import com.woowacourse.ody.data.remote.core.entity.meeting.response.MeetingsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MeetingService {
    @GET("invite-codes/{inviteCode}/validate")
    suspend fun getInviteCodeValidity(
        @Path(value = "inviteCode") inviteCode: String,
    )

    @GET(MEETING_PATH)
    suspend fun getMeeting(): MeetingsResponse

    @POST("/v1/meetings")
    suspend fun postMeeting(
        @Body meetingRequest: MeetingRequest,
    ): MeetingCreationResponse

    companion object {
        const val MEETING_PATH = "/meetings/me"
    }
}
