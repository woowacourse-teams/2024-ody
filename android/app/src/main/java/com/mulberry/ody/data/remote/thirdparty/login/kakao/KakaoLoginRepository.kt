package com.mulberry.ody.data.remote.thirdparty.login.kakao

import android.content.Context
import com.mulberry.ody.data.local.db.OdyDatastore
import com.mulberry.ody.data.remote.core.entity.login.mapper.toAuthToken
import com.mulberry.ody.data.remote.core.entity.login.request.LoginRequest
import com.mulberry.ody.data.remote.core.service.LoginService
import com.mulberry.ody.data.remote.core.service.LogoutService
import com.mulberry.ody.data.remote.core.service.MemberService
import com.mulberry.ody.data.remote.thirdparty.login.entity.UserProfile
import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.apiresult.map
import com.mulberry.ody.domain.common.flatMap
import com.mulberry.ody.domain.model.AuthToken
import com.mulberry.ody.domain.repository.ody.AuthTokenRepository
import com.mulberry.ody.domain.repository.ody.FCMTokenRepository
import com.mulberry.ody.domain.repository.ody.LoginRepository
import javax.inject.Inject

class KakaoLoginRepository
    @Inject
    constructor(
        private val loginService: LoginService,
        private val logoutService: LogoutService,
        private val memberService: MemberService,
        private val odyDatastore: OdyDatastore,
        private val authTokenRepository: AuthTokenRepository,
        private val kakaoOAuthLoginService: KakaoOAuthLoginService,
        private val fcmTokenRepository: FCMTokenRepository,
    ) : LoginRepository {
        override suspend fun checkIfLogined(): Boolean {
            val isKakaoLogined = kakaoOAuthLoginService.checkIfLogined()
            val isOdyLogined = authTokenRepository.fetchAuthToken().getOrNull() != null
            return isKakaoLogined && isOdyLogined
        }

        override suspend fun login(context: Context): ApiResult<AuthToken> {
            val loginRequest = kakaoOAuthLoginService.login(context).flatMap { buildLoginRequest(it) }

            if (loginRequest.isFailure) {
                val loginRequestException =
                    loginRequest.exceptionOrNull() ?: Exception("LoginRequest exception이 null입니다")
                return ApiResult.Unexpected(loginRequestException)
            }

            val result =
                loginRequest.getOrNull() ?: return ApiResult.Unexpected(
                    Exception("LoginRequest 가 null 입니다"),
                )

            return loginService.postLoginWithKakao(result).map {
                val token = it.toAuthToken()
                odyDatastore.setAuthToken(token)
                token
            }
        }

        override suspend fun logout(): ApiResult<Unit> {
            val kakaoLogoutRequest = kakaoOAuthLoginService.logout()
            val logoutRequest = logoutService.postLogout()
            odyDatastore.removeAuthToken()

            if (kakaoLogoutRequest.isFailure) {
                val exception =
                    kakaoLogoutRequest.exceptionOrNull() ?: return ApiResult.Unexpected(
                        Exception("kakaoLogoutRequest가 null입니다"),
                    )
                return ApiResult.Unexpected(exception)
            }

            return logoutRequest
        }

        private suspend fun buildLoginRequest(userProfile: UserProfile): Result<LoginRequest> =
            fcmTokenRepository.fetchFCMToken().map { deviceToken ->
                LoginRequest(
                    deviceToken,
                    userProfile.providerId,
                    userProfile.nickname,
                    userProfile.imageUrl,
                )
            }

        override suspend fun withdrawAccount(): ApiResult<Unit> {
            return memberService.deleteMember().also {
                if (it.isSuccess) odyDatastore.removeAuthToken()
            }
        }
    }
