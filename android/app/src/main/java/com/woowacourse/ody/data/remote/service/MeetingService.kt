package com.woowacourse.ody.data.remote.service

import com.woowacourse.ody.data.entity.meeting.MeetingRequest
import com.woowacourse.ody.data.entity.meeting.MeetingResponse
import com.woowacourse.ody.data.entity.meeting.MeetingsResponse
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

    @POST("meetings")
    suspend fun postMeeting(
        @Body meetingRequest: MeetingRequest,
    ): MeetingResponse

    companion object {
        const val MEETING_PATH = "/meetings/me"
    }
}
