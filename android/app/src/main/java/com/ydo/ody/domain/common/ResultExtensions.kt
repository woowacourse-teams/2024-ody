package com.ydo.ody.domain.common

inline fun <T, R> Result<T>.flatMap(block: (T) -> Result<R>): Result<R> =
    this.mapCatching {
        block(it).getOrThrow()
    }
