package com.mulberry.ody.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> SharedFlow<T>.valueOnAction(action: () -> Unit): T? {
    val value = mutableListOf<T>()
    runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            this@valueOnAction.toList(value)
        }

        action()
    }
    return value.firstOrNull()
}
