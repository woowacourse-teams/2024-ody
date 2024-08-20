package com.woowacourse.ody.fake

import com.woowacourse.ody.domain.apiresult.ApiResult
import com.woowacourse.ody.presentation.common.image.ImageStorage

object FakeImageStorage : ImageStorage {
    override suspend fun upload(
        byteArray: ByteArray,
        fileName: String,
    ): ApiResult<String> {
        return ApiResult.Success("https://github.com/woowacourse-teams/2024-ody")
    }
}
