package com.mulberry.ody.domain.apiresult

import java.io.IOException

sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>

    data class Failure(val code: Int?, val errorMessage: String?) : ApiResult<Nothing>

    data class NetworkError(val exception: IOException) : ApiResult<Nothing>

    class Unexpected(val throwable: Throwable) : ApiResult<Nothing>

    val isSuccess: Boolean
        get() = this is Success

    val isFailure: Boolean
        get() = this is Failure

    val isNetworkError: Boolean
        get() = this is NetworkError

    val isUnexpected: Boolean
        get() = this is Unexpected
}
