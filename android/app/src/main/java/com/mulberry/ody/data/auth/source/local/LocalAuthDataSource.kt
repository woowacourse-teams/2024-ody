package com.mulberry.ody.data.auth.source.local

import com.mulberry.ody.domain.model.AuthToken

interface LocalAuthDataSource {
    suspend fun isLoggedIn(): Boolean

    suspend fun removeAuthToken()

    suspend fun postAuthToken(authToken: AuthToken)

    suspend fun fetchFCMToken(): Result<String>

    suspend fun postFCMToken(fcmToken: String)
}
