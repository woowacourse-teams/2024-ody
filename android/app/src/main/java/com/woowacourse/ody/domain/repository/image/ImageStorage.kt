package com.woowacourse.ody.domain.repository.image

import com.woowacourse.ody.domain.apiresult.ApiResult

interface ImageStorage {
    suspend fun upload(
        byteArray: ByteArray,
        fileName: String,
    ): ApiResult<String>
}
