package com.ydo.ody.fake

import com.ydo.ody.domain.apiresult.ApiResult
import com.ydo.ody.presentation.common.image.ImageShareContent
import com.ydo.ody.presentation.common.image.ImageShareHelper

object FakeImageShareHelper : ImageShareHelper {
    override suspend fun share(imageShareContent: ImageShareContent): ApiResult<Unit> {
        return ApiResult.Success(Unit)
    }
}
