package com.mulberry.ody.data.local.service

import android.util.Log

class TestPeriodicWork : PeriodicWork {
    override suspend fun doWork(): Any {
        Log.d("TestPeriodicWork", "doWork: start")
        return "test"
    }
}
