package com.mulberry.ody.data.auth.source

import com.mulberry.ody.data.local.db.OdyDatastore
import com.mulberry.ody.domain.model.AuthToken
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DefaultLocalAuthDataSource
    @Inject
    constructor(
        private val odyDatastore: OdyDatastore,
    ) : LocalAuthDataSource {
        override suspend fun fetchAuthToken(): Result<AuthToken> {
            return odyDatastore.getAuthToken().first()
        }

        override suspend fun removeAuthToken() {
            odyDatastore.removeAuthToken()
        }

        override suspend fun postAuthToken(authToken: AuthToken) {
            odyDatastore.setAuthToken(authToken)
        }

        override suspend fun fetchFCMToken(): Result<String> {
            return odyDatastore.getFCMToken().first()
        }

        override suspend fun postFCMToken(fcmToken: String) = odyDatastore.setFCMToken(fcmToken)
    }
