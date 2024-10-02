package com.mulberry.ody.data.remote.core.service

import com.mulberry.ody.domain.apiresult.ApiResult
import retrofit2.http.DELETE

interface MemberService {
    @DELETE("/members")
    suspend fun deleteMember(): ApiResult<Unit>
}
