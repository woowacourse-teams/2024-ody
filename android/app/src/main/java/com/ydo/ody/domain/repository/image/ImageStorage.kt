package com.ydo.ody.domain.repository.image

import com.ydo.ody.domain.apiresult.ApiResult

interface ImageStorage {
    suspend fun upload(
        byteArray: ByteArray,
        fileName: String,
    ): ApiResult<String>
}
