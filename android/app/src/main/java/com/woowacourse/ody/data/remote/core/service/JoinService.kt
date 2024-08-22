package com.woowacourse.ody.data.remote.core.service

import com.woowacourse.ody.data.remote.core.entity.join.request.JoinRequest
import com.woowacourse.ody.data.remote.core.entity.join.response.JoinResponse
import com.woowacourse.ody.domain.apiresult.ApiResult
import retrofit2.http.Body
import retrofit2.http.POST

interface JoinService {
    @POST("/v2/mates")
    suspend fun postMates(
        @Body joinRequest: JoinRequest,
    ): ApiResult<JoinResponse>
}
