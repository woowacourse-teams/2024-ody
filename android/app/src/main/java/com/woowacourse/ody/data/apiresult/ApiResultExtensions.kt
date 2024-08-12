package com.woowacourse.ody.data.apiresult

fun <T> ApiResult<T>.onSuccess(func: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) {
        func(this.data)
    }
    return this
}

fun <T> ApiResult<T>.onFailure(func: (code: Int, error: String?) -> Unit): ApiResult<T> {
    if (this is ApiResult.Failure) {
        func(this.code, this.error)
    }
    return this
}

fun <T> ApiResult<T>.onNetworkError(func: (exception: Exception) -> Unit): ApiResult<T> {
    if (this is ApiResult.NetworkError) {
        func(this.exception)
    }
    return this
}

fun <T> ApiResult<T>.onUnexpected(func: (t: Throwable) -> Unit): ApiResult<T> {
    if (this is ApiResult.Unexpected) {
        func(this.t)
    }
    return this
}

suspend fun <T, R> ApiResult<T>.map(func: suspend (T) -> R): ApiResult<R> {
    return when (this) {
        is ApiResult.Success -> ApiResult.Success(func(this.data))
        is ApiResult.Failure -> ApiResult.Failure(this.code, this.error)
        is ApiResult.NetworkError -> ApiResult.NetworkError(this.exception)
        is ApiResult.Unexpected -> ApiResult.Unexpected(this.t)
    }
}
