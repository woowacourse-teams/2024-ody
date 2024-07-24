package com.woowacourse.ody.data.remote.service

import retrofit2.http.GET
import retrofit2.http.Path

interface MeetingService {
    @GET("invite-codes/{inviteCode}/validate")
    suspend fun getInviteCodeValidity(
        @Path(value = "inviteCode") inviteCode: String,
    )
    @GET(MEETING_PATH)
    suspend fun getMeeting(): List<MeetingEntity>

    companion object {
        const val MEETING_PATH = "/meetings/me"
    }
}
