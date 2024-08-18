package com.woowacourse.ody.data.remote.thirdparty.login

import com.woowacourse.ody.data.remote.thirdparty.login.model.UserProfile

interface ThirdPartyLoginService {
    suspend fun login(): Result<UserProfile>
}
