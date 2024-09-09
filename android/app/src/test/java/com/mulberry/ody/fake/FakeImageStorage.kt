package com.mulberry.ody.fake

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.repository.image.ImageStorage

object FakeImageStorage : ImageStorage {
    override suspend fun upload(
        byteArray: ByteArray,
        fileName: String,
    ): ApiResult<String> {
        return ApiResult.Success("https://github.com/woowacourse-teams/2024-ody")
    }
}
