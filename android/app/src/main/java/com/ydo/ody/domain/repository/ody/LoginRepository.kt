package com.ydo.ody.domain.repository.ody

import com.ydo.ody.domain.apiresult.ApiResult
import com.ydo.ody.domain.model.AuthToken

interface LoginRepository {
    suspend fun login(): ApiResult<AuthToken>
}
