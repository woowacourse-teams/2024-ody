package com.mulberry.ody.domain.apiresult

fun <T> ApiResult<T>.onSuccess(block: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) {
        block(this.data)
    }
    return this
}

fun <T> ApiResult<T>.onFailure(block: (code: Int?, errorMessage: String?) -> Unit): ApiResult<T> {
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

suspend fun <T> ApiResult<T>.suspendOnSuccess(block: suspend (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) {
        block(this.data)
    }
    return this
}

suspend fun <T> ApiResult<T>.suspendOnFailure(block: suspend (code: Int?, errorMessage: String?) -> Unit): ApiResult<T> {
    if (this is ApiResult.Failure) {
        block(this.code, this.errorMessage)
    }
    return this
}

suspend fun <T> ApiResult<T>.suspendOnNetworkError(block: suspend (exception: Exception) -> Unit): ApiResult<T> {
    if (this is ApiResult.NetworkError) {
        block(this.exception)
    }
    return this
}

suspend fun <T> ApiResult<T>.suspendOnUnexpected(block: suspend (t: Throwable) -> Unit): ApiResult<T> {
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

fun <T> ApiResult<T>.getOrNull(): T? {
    return if (this is ApiResult.Success) data else null
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

suspend fun <R, T> ApiResult<T>.suspendFold(
    onSuccess: suspend (data: T) -> R,
    onFailure: suspend (t: Throwable) -> R,
): R {
    return when (val exception = exceptionOrNull()) {
        null -> onSuccess(this.getOrNull() as T)
        else -> onFailure(exception)
    }
}
