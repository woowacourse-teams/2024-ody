package com.woowacourse.ody.data.remote.thirdparty.login.kakao

import android.content.Context
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.woowacourse.ody.data.remote.thirdparty.login.AuthToken
import com.woowacourse.ody.data.remote.thirdparty.login.OAuthLoginService
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class KakaoLoginService(private val context: Context) : OAuthLoginService {
    override suspend fun login(): Result<AuthToken> =
        loginWithKakaoTalk().onFailure { error ->
            Timber.tag(TAG).e(error)
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

    companion object {
        private const val TAG = "KakaoLoginService"
    }
}
