package com.mulberry.ody.data.local.service

import android.content.Context
import com.mulberry.ody.data.local.db.OdyDatastore
import com.mulberry.ody.domain.common.toMilliSeconds
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import javax.inject.Inject

class EtaDashboard @Inject constructor(
    val context: Context,
    val odyDatastore: OdyDatastore,
) {
    fun open(
        meetingId: Long,
        meetingTime: LocalDateTime,
    ) {
        if (!isLoggedIn()) {
            return
        }
        val meetingTimeMills = meetingTime.toMilliSeconds()
        val serviceIntent = EtaDashboardService.getIntent(context, meetingId, meetingTimeMills, isOpen = true)
        context.startForegroundService(serviceIntent)
    }

    private fun isLoggedIn(): Boolean {
        return runBlocking { odyDatastore.getAuthToken().first().isSuccess }
    }
}
