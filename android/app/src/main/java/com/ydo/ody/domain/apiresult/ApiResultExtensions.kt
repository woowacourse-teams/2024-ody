package com.ydo.ody.domain.apiresult

fun <T> ApiResult<T>.onSuccess(block: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) {
        block(this.data)
    }
    return this
}

fun <T> ApiResult<T>.onFailure(block: (code: Int, errorMessage: String?) -> Unit): ApiResult<T> {
    if (this is ApiResult.Failure) {
        block(this.code, this.errorMessage)
    }
    return this
}

fun <T> ApiResult<T>.onNetworkError(block: (exception: Exception) -> Unit): ApiResult<T> {
    if (this is ApiResult.NetworkError) {
        block(this.exception)
    }
    return this
}

fun <T> ApiResult<T>.onUnexpected(block: (t: Throwable) -> Unit): ApiResult<T> {
    if (this is ApiResult.Unexpected) {
        block(this.t)
    }
    return this
}

suspend fun <T, R> ApiResult<T>.map(block: suspend (T) -> R): ApiResult<R> {
    return when (this) {
        is ApiResult.Success -> ApiResult.Success(block(this.data))
        is ApiResult.Failure -> ApiResult.Failure(this.code, this.errorMessage)
        is ApiResult.NetworkError -> ApiResult.NetworkError(this.exception)
        is ApiResult.Unexpected -> ApiResult.Unexpected(this.t)
    }
}
