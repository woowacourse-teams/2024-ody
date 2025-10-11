package com.mulberry.ody.domain.repository.ody

import com.mulberry.ody.domain.apiresult.ApiResult

interface AuthRepository {
    suspend fun isLoggedIn(): Boolean

    suspend fun login(): ApiResult<Unit>

    suspend fun logout(): ApiResult<Unit>

    suspend fun withdrawAccount(): ApiResult<Unit>
}
