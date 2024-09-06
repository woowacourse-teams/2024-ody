package com.mulberry.ody.domain.repository.ody

interface FCMTokenRepository {
    suspend fun fetchFCMToken(): Result<String>

    suspend fun postFCMToken(fcmToken: String)
}
