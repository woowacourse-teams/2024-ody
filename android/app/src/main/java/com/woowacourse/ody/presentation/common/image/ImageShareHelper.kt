package com.woowacourse.ody.presentation.common.image

import com.woowacourse.ody.domain.apiresult.ApiResult

interface ImageShareHelper {
    suspend fun share(imageShareContent: ImageShareContent): ApiResult<Unit>
}
