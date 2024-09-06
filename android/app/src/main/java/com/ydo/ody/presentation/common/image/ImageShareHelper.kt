package com.ydo.ody.presentation.common.image

import com.ydo.ody.domain.apiresult.ApiResult

interface ImageShareHelper {
    suspend fun share(imageShareContent: ImageShareContent): ApiResult<Unit>
}
