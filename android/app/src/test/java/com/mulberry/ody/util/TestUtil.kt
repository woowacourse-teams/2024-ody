package com.mulberry.ody.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.mulberry.ody.presentation.common.Event
import com.mulberry.ody.presentation.common.SingleLiveData
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/* Copyright 2019 Google LLC.
   SPDX-License-Identifier: Apache-2.0 */
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer =
        object : Observer<T> {
            override fun onChanged(value: T) {
                data = value
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }

    this.observeForever(observer)

    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        throw TimeoutException("LiveData value was never set.")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}

fun <T> SingleLiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
): T {
    var event: Event<T>? = null
    val latch = CountDownLatch(1)
    val observer =
        object : Observer<Event<T>> {
            override fun onChanged(value: Event<T>) {
                event = value
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }

    this.observeForever(observer)

    if (!latch.await(time, timeUnit)) {
        throw TimeoutException("SingleLiveData value was never set.")
    }
    val data = event?.peekContent()

    @Suppress("UNCHECKED_CAST")
    return data as T
}

fun SingleLiveData<Unit>.getIfHandled(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
): Boolean {
    var event: Event<Unit>? = null
    val latch = CountDownLatch(1)
    val observer =
        object : Observer<Event<Unit>> {
            override fun onChanged(value: Event<Unit>) {
                event = value
                latch.countDown()
                this@getIfHandled.removeObserver(this)
            }
        }

    this.observeForever(observer)

    if (!latch.await(time, timeUnit)) {
        throw TimeoutException("SingleLiveData value was never set.")
    }
    @Suppress("UNCHECKED_CAST")
    return event?.hasBeenHandled ?: false
}
