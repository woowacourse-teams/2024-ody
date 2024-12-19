package com.mulberry.ody.data.auth.source

import com.mulberry.ody.domain.model.AuthToken

interface LocalAuthDataSource {
     suspend fun fetchAuthToken(): Result<AuthToken>

     suspend fun removeAuthToken()

     suspend fun postAuthToken(authToken: AuthToken)

     suspend fun fetchFCMToken(): Result<String>

     suspend fun postFCMToken(fcmToken: String)
}
