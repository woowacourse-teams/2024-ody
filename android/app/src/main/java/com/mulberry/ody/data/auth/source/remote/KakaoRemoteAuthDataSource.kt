package com.mulberry.ody.data.auth.source.remote

import android.content.Context
import com.mulberry.ody.data.remote.core.entity.login.mapper.toAuthToken
import com.mulberry.ody.data.remote.core.entity.login.request.LoginRequest
import com.mulberry.ody.data.remote.core.service.AuthService
import com.mulberry.ody.data.remote.core.service.LoginService
import com.mulberry.ody.data.remote.core.service.RefreshTokenService
import com.mulberry.ody.data.remote.thirdparty.login.kakao.KakaoOAuthLoginService
import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.apiresult.flatMap
import com.mulberry.ody.domain.apiresult.map
import com.mulberry.ody.domain.apiresult.toApiResult
import com.mulberry.ody.domain.model.AuthToken
import javax.inject.Inject

class KakaoRemoteAuthDataSource
    @Inject
    constructor(
        private val loginService: LoginService,
        private val authService: AuthService,
        private val refreshTokenService: RefreshTokenService,
        private val kakaoOAuthLoginService: KakaoOAuthLoginService,
    ) : RemoteAuthDataSource {
        override suspend fun isLoggedIn(): Boolean {
            return kakaoOAuthLoginService.isLoggedIn()
        }

        override suspend fun postAuthToken(): ApiResult<AuthToken> {
            return refreshTokenService.postRefreshToken().map {
                it.toAuthToken()
            }
        }

        override suspend fun login(
            fcmToken: String,
            context: Context,
        ): ApiResult<AuthToken> {
            val loginResult =
                kakaoOAuthLoginService.login(context).toApiResult()
                    .map { LoginRequest(fcmToken, it.providerId, it.nickname, it.imageUrl) }

            return loginResult.flatMap { loginRequest ->
                loginService.postLoginWithKakao(loginRequest).map { it.toAuthToken() }
            }
        }

        override suspend fun logout(): ApiResult<Unit> {
            val kakaoLogoutResult = kakaoOAuthLoginService.logout()
            val odyLogoutResult = authService.postLogout()

            val exception = kakaoLogoutResult.exceptionOrNull()
            if (exception != null) {
                return ApiResult.Unexpected(exception)
            }

            return odyLogoutResult
        }

        override suspend fun withdraw(): ApiResult<Unit> {
            return authService.deleteMember()
        }
    }
