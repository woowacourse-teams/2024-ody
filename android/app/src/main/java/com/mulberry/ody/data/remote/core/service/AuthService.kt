package com.mulberry.ody.data.remote.core.service

import com.mulberry.ody.domain.apiresult.ApiResult
import retrofit2.http.DELETE
import retrofit2.http.POST

interface AuthService {
    @POST("/v1/auth/logout")
    suspend fun postLogout(): ApiResult<Unit>

    @DELETE("/members")
    suspend fun deleteMember(): ApiResult<Unit>
}
