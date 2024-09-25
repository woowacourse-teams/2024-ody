package com.mulberry.ody.data.local.service

interface PeriodicWork {
    suspend fun doWork(): Any
}
