package com.mulberry.ody.data.auth.source.local

import com.mulberry.ody.data.local.db.OdyDataStore
import com.mulberry.ody.domain.model.AuthToken
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class KakaoLocalAuthDataSource
    @Inject
    constructor(
        private val odyDataStore: OdyDataStore,
    ) : LocalAuthDataSource {
        override suspend fun isLoggedIn(): Boolean {
            return odyDataStore.getAuthToken().first().isSuccess
        }

        override suspend fun removeAuthToken() {
            odyDataStore.removeAuthToken()
        }

        override suspend fun postAuthToken(authToken: AuthToken) {
            odyDataStore.setAuthToken(authToken)
        }

        override suspend fun fetchFCMToken(): Result<String> {
            return odyDataStore.getFCMToken().first()
        }

        override suspend fun postFCMToken(fcmToken: String) = odyDataStore.setFCMToken(fcmToken)
    }
