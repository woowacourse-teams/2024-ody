package com.woowacourse.ody.presentation.common.capture

import com.woowacourse.ody.domain.apiresult.ApiResult

interface ImageStorage {
    suspend fun upload(
        byteArray: ByteArray,
        fileName: String,
    ): ApiResult<String>
}
