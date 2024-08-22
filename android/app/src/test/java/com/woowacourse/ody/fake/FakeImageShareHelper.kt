package com.woowacourse.ody.fake

import com.woowacourse.ody.domain.apiresult.ApiResult
import com.woowacourse.ody.presentation.common.image.ImageShareContent
import com.woowacourse.ody.presentation.common.image.ImageShareHelper

object FakeImageShareHelper : ImageShareHelper {
    override suspend fun share(imageShareContent: ImageShareContent): ApiResult<Unit> {
        return ApiResult.Success(Unit)
    }
}
