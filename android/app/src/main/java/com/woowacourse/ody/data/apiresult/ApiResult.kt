package com.woowacourse.ody.data.apiresult

import java.io.IOException

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()

    data class Failure(val code: Int, val error: String?) : ApiResult<Nothing>()

    data class NetworkError(val exception: IOException) : ApiResult<Nothing>()

    class Unexpected(val t: Throwable) : ApiResult<Nothing>()
}
