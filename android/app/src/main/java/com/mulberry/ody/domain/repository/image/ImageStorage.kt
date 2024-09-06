package com.mulberry.ody.domain.repository.image

import com.mulberry.ody.domain.apiresult.ApiResult

interface ImageStorage {
    suspend fun upload(
        byteArray: ByteArray,
        fileName: String,
    ): ApiResult<String>
}
