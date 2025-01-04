package com.mulberry.ody.data.local.service

import android.content.Context
import com.mulberry.ody.data.local.db.OdyDatastore
import com.mulberry.ody.domain.common.toMilliSeconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

class EtaDashboard
@Inject
constructor(
    val context: Context,
    val odyDatastore: OdyDatastore,
) {
    fun open(
        meetingId: Long,
        meetingTime: LocalDateTime,
    ) {
        CoroutineScope(Dispatchers.Default).launch {
            if (!isLoggedIn()) {
                return@launch
            }

            startEtaDashboardService(meetingId, meetingTime)
        }
    }

    private suspend fun isLoggedIn(): Boolean {
        return coroutineScope { odyDatastore.getAuthToken().first().isSuccess }
    }

    private fun startEtaDashboardService(
        meetingId: Long,
        meetingTime: LocalDateTime,
    ) {
        val meetingTimeMills = meetingTime.toMilliSeconds()
        val serviceIntent = EtaDashboardService.getIntent(context, meetingId, meetingTimeMills, isOpen = true)
        context.startForegroundService(serviceIntent)
    }
}
