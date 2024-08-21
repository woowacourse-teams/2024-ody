package com.woowacourse.ody.data.remote.thirdparty.login.kakao

import android.content.Context
import com.kakao.sdk.auth.TokenManagerProvider
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.woowacourse.ody.data.remote.thirdparty.login.entity.UserProfile
import com.woowacourse.ody.domain.common.flatMap
import com.woowacourse.ody.domain.model.AuthToken
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class KakaoOAuthLoginService {
    suspend fun login(context: Context): Result<UserProfile> {
        return loginWithKakao(context).flatMap { requestUserProfile() }
    }

    fun checkIfLogined(): Boolean = TokenManagerProvider.instance.manager.getToken() != null

    private suspend fun loginWithKakao(context: Context): Result<AuthToken> =
        loginWithKakaoTalk(context).onFailure { error ->
            if (error !is ClientError || error.reason != ClientErrorCause.Cancelled) {
                return loginWithKakaoAccount(context)
            }
        }

    private suspend fun loginWithKakaoTalk(context: Context) =
        suspendCoroutine<Result<AuthToken>> { continuation ->
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    continuation.resume(Result.failure(error))
                } else if (token != null) {
                    continuation.resume(Result.success(token.toAuthToken()))
                }
            }
        }

    private suspend fun loginWithKakaoAccount(context: Context) =
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
