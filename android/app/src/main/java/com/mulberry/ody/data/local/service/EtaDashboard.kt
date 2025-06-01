package com.mulberry.ody.data.local.service

import android.content.Context
import com.mulberry.ody.data.local.db.OdyDataStore
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
        private val context: Context,
        private val odyDataStore: OdyDataStore,
    ) {
        fun open(
            meetingId: Long,
            meetingTime: LocalDateTime,
        ) {
            CoroutineScope(Dispatchers.Default).launch {
                if (!isLoggedIn() || !meetingTime.isOpenTime()) {
                    return@launch
                }

                startEtaDashboardService(meetingId, meetingTime)
            }
        }

        private suspend fun isLoggedIn(): Boolean {
            return coroutineScope { odyDataStore.getAuthToken().first().isSuccess }
        }

        private fun LocalDateTime.isOpenTime(): Boolean {
            return LocalDateTime.now() >= this.minusMinutes(ETA_OPEN_MINUTE)
        }

        private fun startEtaDashboardService(
            meetingId: Long,
            meetingTime: LocalDateTime,
        ) {
            val meetingTimeMills = meetingTime.toMilliSeconds()
            val serviceIntent = EtaDashboardService.getIntent(context, meetingId, meetingTimeMills, isOpen = true)
            context.startForegroundService(serviceIntent)
        }

        companion object {
            private const val ETA_OPEN_MINUTE = 30L
        }
    }
