package com.mulberry.ody.domain.repository.ody

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.AuthToken

interface LoginRepository {
    suspend fun login(): ApiResult<AuthToken>
}
