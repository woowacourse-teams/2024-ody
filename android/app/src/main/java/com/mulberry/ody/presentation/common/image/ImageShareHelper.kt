package com.mulberry.ody.presentation.common.image

import com.mulberry.ody.domain.apiresult.ApiResult

interface ImageShareHelper {
    suspend fun share(imageShareContent: ImageShareContent): ApiResult<Unit>
}
