package com.mulberry.ody.domain.apiresult

import java.lang.IllegalArgumentException

inline fun <T> ApiResult<T>.onSuccess(block: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) {
        block(this.data)
    }
    return this
}

inline fun <T> ApiResult<T>.onFailure(block: (code: Int?, errorMessage: String?) -> Unit): ApiResult<T> {
    if (this is ApiResult.Failure) {
        block(this.code, this.errorMessage)
    }
    return this
}

inline fun <T> ApiResult<T>.onNetworkError(block: (exception: Exception) -> Unit): ApiResult<T> {
    if (this is ApiResult.NetworkError) {
        block(this.exception)
    }
    return this
}

inline fun <T> ApiResult<T>.onUnexpected(block: (t: Throwable) -> Unit): ApiResult<T> {
    if (this is ApiResult.Unexpected) {
        block(this.throwable)
    }
    return this
}

inline fun <T, R> ApiResult<T>.map(block: (T) -> R): ApiResult<R> {
    return when (this) {
        is ApiResult.Success -> ApiResult.Success(block(this.data))
        is ApiResult.Failure -> ApiResult.Failure(this.code, this.errorMessage)
        is ApiResult.NetworkError -> ApiResult.NetworkError(this.exception)
        is ApiResult.Unexpected -> ApiResult.Unexpected(this.throwable)
    }
}

fun <T> ApiResult<T>.getOrNull(): T? {
    return if (this is ApiResult.Success) data else null
}

fun <T> ApiResult<T>.getOrThrow(): T {
    when (this) {
        is ApiResult.Failure -> throw IllegalArgumentException(errorMessage)
        is ApiResult.NetworkError -> throw exception
        is ApiResult.Unexpected -> throw throwable
        is ApiResult.Success -> return data
    }
}

fun <T> Result<T>.toApiResult(): ApiResult<T> {
    return fold(
        onSuccess = { data -> ApiResult.Success(data) },
        onFailure = { t -> ApiResult.Unexpected(t) },
    )
}

fun <T> ApiResult<T>.exceptionOrNull(): Throwable? {
    var exception: Throwable? = null
    onUnexpected {
        exception = it
    }.onNetworkError {
        exception = it
    }
    return exception
}

inline fun <R, T> ApiResult<T>.fold(
    onSuccess: (data: T) -> R,
    onFailure: (t: Throwable) -> R,
): R {
    return when (val exception = exceptionOrNull()) {
        null -> onSuccess(this.getOrNull() as T)
        else -> onFailure(exception)
    }
}

inline fun <T, R> ApiResult<T>.flatMap(block: (T) -> ApiResult<R>): ApiResult<R> {
    return when (this) {
        is ApiResult.Success -> block(this.data)
        is ApiResult.Failure -> ApiResult.Failure(this.code, this.errorMessage)
        is ApiResult.NetworkError -> ApiResult.NetworkError(this.exception)
        is ApiResult.Unexpected -> ApiResult.Unexpected(this.throwable)
    }
}
