package com.woowacourse.ody.data.remote.thirdparty.login.kakao

import android.content.Context
import com.woowacourse.ody.data.local.db.OdyDatastore
import com.woowacourse.ody.data.remote.core.entity.login.mapper.toAuthToken
import com.woowacourse.ody.data.remote.core.entity.login.request.LoginRequest
import com.woowacourse.ody.data.remote.core.service.LoginService
import com.woowacourse.ody.data.remote.thirdparty.login.entity.UserProfile
import com.woowacourse.ody.domain.apiresult.ApiResult
import com.woowacourse.ody.domain.apiresult.map
import com.woowacourse.ody.domain.common.flatMap
import com.woowacourse.ody.domain.model.AuthToken
import com.woowacourse.ody.domain.repository.ody.FCMTokenRepository

class KakaoLoginRepository(
    private val loginService: LoginService,
    private val odyDatastore: OdyDatastore,
    private val kakaoOAuthLoginService: KakaoOAuthLoginService,
    private val fcmTokenRepository: FCMTokenRepository,
) {
    fun checkIfLogined(): Boolean {
        return kakaoOAuthLoginService.checkIfLogined()
    }

    suspend fun login(context: Context): ApiResult<AuthToken> {
        val loginRequest = kakaoOAuthLoginService.login(context).flatMap { buildLoginRequest(it) }
        if (loginRequest.isFailure) {
            return ApiResult.Unexpected(
                loginRequest.exceptionOrNull().let {
                    it ?: Exception("LoginRequest Exception is null")
                },
            )
        }
        loginRequest.getOrNull().let { request ->
            if (request == null) return ApiResult.Unexpected(Exception("LoginRequest is null"))
            return loginService.loginWithKakao(request).map {
                val token = it.toAuthToken()
                odyDatastore.setAuthToken(token)
                token
            }
        }
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
}
