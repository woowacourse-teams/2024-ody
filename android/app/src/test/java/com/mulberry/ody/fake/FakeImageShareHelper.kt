package com.mulberry.ody.fake

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.presentation.common.image.ImageShareContent
import com.mulberry.ody.presentation.common.image.ImageShareHelper

object FakeImageShareHelper : ImageShareHelper {
    override suspend fun share(imageShareContent: ImageShareContent): ApiResult<Unit> {
        return ApiResult.Success(Unit)
    }
}
