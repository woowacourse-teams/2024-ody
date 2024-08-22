package com.woowacourse.ody.domain.common

class Provider<T>(private val creator: () -> T) {
    private var instance: T? = null

    fun get(): T {
        if (instance == null) {
            instance = creator()
        }
        return instance!!
    }
}
