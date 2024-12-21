package com.mulberry.ody.data.auth.source.remote

import android.content.Context
import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.AuthToken

interface RemoteAuthDataSource {
    suspend fun checkIfLoggedIn(): Boolean

    suspend fun postAuthToken(): ApiResult<AuthToken>

    suspend fun login(
        fcmToken: String,
        context: Context,
    ): ApiResult<AuthToken>

    suspend fun logout(): ApiResult<Unit>

    suspend fun withdraw(): ApiResult<Unit>
}
