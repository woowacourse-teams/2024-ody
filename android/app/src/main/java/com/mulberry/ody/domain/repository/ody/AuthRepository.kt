package com.mulberry.ody.domain.repository.ody

import android.content.Context
import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.AuthToken

interface AuthRepository {
    suspend fun isLoggedIn(): Boolean

    suspend fun login(context: Context): ApiResult<AuthToken>

    suspend fun logout(): ApiResult<Unit>

    suspend fun withdrawAccount(): ApiResult<Unit>
}
