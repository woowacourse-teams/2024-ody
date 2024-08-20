package com.woowacourse.ody.domain.repository.ody

import com.woowacourse.ody.domain.apiresult.ApiResult
import com.woowacourse.ody.domain.model.AuthToken

interface LoginRepository {
    suspend fun login(): ApiResult<AuthToken>
}
