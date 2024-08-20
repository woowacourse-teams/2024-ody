package com.woowacourse.ody.domain.repository.ody

import com.woowacourse.ody.domain.apiresult.ApiResult
import com.woowacourse.ody.domain.model.AuthToken

interface AuthTokenRepository {
    suspend fun fetchAuthToken(): Result<AuthToken>

    suspend fun refreshAuthToken(): ApiResult<AuthToken>
}
