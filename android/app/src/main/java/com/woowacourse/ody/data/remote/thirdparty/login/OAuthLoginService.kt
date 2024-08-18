package com.woowacourse.ody.data.remote.thirdparty.login

interface OAuthLoginService {
    suspend fun login(): Result<AuthToken>
}
