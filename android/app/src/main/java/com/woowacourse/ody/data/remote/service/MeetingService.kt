package com.woowacourse.ody.data.remote.service

import com.woowacourse.ody.data.model.meeting.MeetingRequest
import com.woowacourse.ody.data.model.meeting.MeetingResponse
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
    suspend fun getMeeting(): List<MeetingResponse>

    @POST("/meetings")
    suspend fun postMeeting(
        @Body meetingRequest: MeetingRequest,
    ): MeetingResponse

    companion object {
        const val MEETING_PATH = "/meetings/me"
    }
}
