package com.woowacourse.ody.data.remote.core.service

import com.woowacourse.ody.data.remote.core.entity.meeting.request.MatesEtaRequest
import com.woowacourse.ody.data.remote.core.entity.meeting.request.MeetingRequest
import com.woowacourse.ody.data.remote.core.entity.meeting.response.MatesEtaResponse
import com.woowacourse.ody.data.remote.core.entity.meeting.response.MeetingCatalogsResponse
import com.woowacourse.ody.data.remote.core.entity.meeting.response.MeetingCreationResponse
import com.woowacourse.ody.data.remote.core.entity.meeting.response.MeetingResponse
import com.woowacourse.ody.domain.apiresult.ApiResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface MeetingService {
    @GET("invite-codes/{inviteCode}/validate")
    suspend fun getInviteCodeValidity(
        @Path(value = "inviteCode") inviteCode: String,
    ): ApiResult<Unit>

    @POST("/v1/meetings")
    suspend fun postMeeting(
        @Body meetingRequest: MeetingRequest,
    ): ApiResult<MeetingCreationResponse>

    @PATCH("/v2/meetings/{meetingId}/mates/etas")
    suspend fun patchMatesEta(
        @Path(value = "meetingId") meetingId: Long,
        @Body matesEtaRequest: MatesEtaRequest,
    ): MatesEtaResponse

    @GET("/v1/meetings/me")
    suspend fun fetchMeetingCatalogs(): ApiResult<MeetingCatalogsResponse>

    @GET("/v1/meetings/{meetingId}")
    suspend fun fetchMeeting(
        @Path(value = "meetingId") meetingId: Long,
    ): ApiResult<MeetingResponse>
}
