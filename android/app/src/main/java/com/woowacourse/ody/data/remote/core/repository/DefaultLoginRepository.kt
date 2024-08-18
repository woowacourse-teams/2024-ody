package com.woowacourse.ody.data.remote.core.repository

import com.woowacourse.ody.data.remote.core.entity.login.request.LoginRequest
import com.woowacourse.ody.data.remote.core.service.LoginService
import com.woowacourse.ody.data.remote.thirdparty.login.ThirdPartyLoginService
import com.woowacourse.ody.data.remote.thirdparty.login.model.UserProfile
import com.woowacourse.ody.domain.apiresult.ApiResult
import com.woowacourse.ody.domain.apiresult.map
import com.woowacourse.ody.domain.common.flatMap
import com.woowacourse.ody.domain.repository.ody.FCMTokenRepository

class DefaultLoginRepository(
    private val loginService: LoginService,
    private val kakaoOAuthLoginService: ThirdPartyLoginService,
    private val fcmTokenRepository: FCMTokenRepository,
) {
    suspend fun loginWithKakao(): ApiResult<Unit> {
        val loginRequest =
            kakaoOAuthLoginService
                .login()
                .flatMap { buildLoginRequest(it) }
        if (loginRequest.isFailure) return ApiResult.Unexpected(loginRequest.exceptionOrNull()!!)
        return loginService.loginWithKakao(loginRequest.getOrThrow()).map { }
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
