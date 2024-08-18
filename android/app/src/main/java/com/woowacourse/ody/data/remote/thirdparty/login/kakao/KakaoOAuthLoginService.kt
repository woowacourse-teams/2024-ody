package com.woowacourse.ody.data.remote.thirdparty.login.kakao

import android.content.Context
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.woowacourse.ody.data.remote.thirdparty.login.ThirdPartyLoginService
import com.woowacourse.ody.data.remote.thirdparty.login.model.AuthToken
import com.woowacourse.ody.data.remote.thirdparty.login.model.UserProfile
import com.woowacourse.ody.domain.common.flatMap
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class KakaoOAuthLoginService(private val context: Context) : ThirdPartyLoginService {
    override suspend fun login(): Result<UserProfile> = loginWithKakao().flatMap { requestUserProfile() }

    private suspend fun loginWithKakao(): Result<AuthToken> =
        loginWithKakaoTalk().onFailure { error ->
            if (error !is ClientError || error.reason != ClientErrorCause.Cancelled) {
                return loginWithKakaoAccount()
            }
        }

    private suspend fun loginWithKakaoTalk() =
        suspendCoroutine<Result<AuthToken>> { continuation ->
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    continuation.resume(Result.failure(error))
                } else if (token != null) {
                    continuation.resume(Result.success(token.toAuthToken()))
                }
            }
        }

    private suspend fun loginWithKakaoAccount() =
        suspendCoroutine<Result<AuthToken>> { continuation ->
            UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
                if (error != null) {
                    continuation.resume(Result.failure(error))
                } else if (token != null) {
                    continuation.resume(Result.success(token.toAuthToken()))
                }
            }
        }

    private suspend fun requestUserProfile() =
        suspendCoroutine<Result<UserProfile>> { continuation ->
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    continuation.resume(Result.failure(error))
                } else if (user != null) {
                    continuation.resume(Result.success(user.toUserProfile()))
                }
            }
        }
}
