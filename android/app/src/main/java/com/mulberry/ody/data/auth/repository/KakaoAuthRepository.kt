package com.mulberry.ody.data.auth.repository

import android.content.Context
import com.mulberry.ody.data.auth.source.local.LocalAuthDataSource
import com.mulberry.ody.data.auth.source.remote.RemoteAuthDataSource
import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.apiresult.getOrNull
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.model.AuthToken
import com.mulberry.ody.domain.repository.ody.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class KakaoAuthRepository
    @Inject
    constructor(
        private val localAuthDataSource: LocalAuthDataSource,
        private val remoteAuthDataSource: RemoteAuthDataSource,
        @ApplicationContext private val context: Context,
    ) : AuthRepository {
        override suspend fun isLoggedIn(): Boolean {
            if (!remoteAuthDataSource.isLoggedIn() || !localAuthDataSource.isLoggedIn()) {
                return false
            }
            val authToken = remoteAuthDataSource.postAuthToken().getOrNull() ?: return false
            localAuthDataSource.postAuthToken(authToken)
            return true
        }

        override suspend fun login(): ApiResult<AuthToken> {
            val fcmToken = localAuthDataSource.fetchFCMToken().getOrNull() ?: return ApiResult.Unexpected(Exception("FCM 토큰이 존재하지 않습니다."))
            return remoteAuthDataSource.login(fcmToken, context).onSuccess {
                localAuthDataSource.postAuthToken(it)
            }
        }

        override suspend fun logout(): ApiResult<Unit> {
            localAuthDataSource.removeAuthToken()
            return remoteAuthDataSource.logout()
        }

        override suspend fun withdrawAccount(): ApiResult<Unit> {
            return remoteAuthDataSource.withdraw().onSuccess {
                localAuthDataSource.removeAuthToken()
            }
        }
    }
