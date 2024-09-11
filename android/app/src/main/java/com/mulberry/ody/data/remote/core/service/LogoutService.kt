package com.mulberry.ody.data.remote.core.service

import com.mulberry.ody.domain.apiresult.ApiResult
import retrofit2.http.POST

interface LogoutService {
    @POST("/v1/auth/logout")
    suspend fun postLogout(): ApiResult<Unit>
}
