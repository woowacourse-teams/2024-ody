package com.woowacourse.ody.data.remote.thirdparty.login

import com.woowacourse.ody.data.remote.thirdparty.login.entity.UserProfile

interface ThirdPartyLoginService {
    suspend fun login(): Result<UserProfile>
}
