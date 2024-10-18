package com.mulberry.ody.data.local.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.mulberry.ody.data.local.db.EtaReserveDao
import com.mulberry.ody.data.local.entity.reserve.EtaReserveEntity
import com.mulberry.ody.data.local.service.EtaDashboardService.Companion.MEETING_ID_KEY
import com.mulberry.ody.domain.common.toMilliSeconds
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.math.max

class EtaDashboardAlarm
    @Inject
    constructor(
        private val context: Context,
        private val alarmManager: AlarmManager,
        private val etaReserveDao: EtaReserveDao,
    ) {
        suspend fun reserveEtaDashboard(
            meetingId: Long,
            meetingDateTime: LocalDateTime,
        ) {
            reserveEtaDashboardOpen(meetingId, meetingDateTime)
            reserveEtaDashboardClose(meetingId, meetingDateTime)
        }

        private suspend fun reserveEtaDashboardOpen(
            meetingId: Long,
            meetingDateTime: LocalDateTime,
        ) {
            val reserveMillis = meetingDateTime.etaDashboardOpenMillis()
            val reserveId = etaReserveDao.save(EtaReserveEntity(meetingId, reserveMillis))
            val pendingIntent = createOpenPendingIntent(meetingId, reserveId.toInt())
            reserve(reserveMillis, pendingIntent)
        }

        private fun LocalDateTime.etaDashboardOpenMillis(): Long {
            val openMillis = minusMinutes(ETA_OPEN_MINUTE).toMilliSeconds()
            val nowMillis = System.currentTimeMillis()
            return max(openMillis, nowMillis)
        }

        private fun createOpenPendingIntent(
            meetingId: Long,
            requestCode: Int,
            ): PendingIntent {
            val alarmIntent =
                Intent(context, EtaDashboardOpenBroadcastReceiver::class.java)
                    .putExtra(MEETING_ID_KEY, meetingId)

            return PendingIntent.getBroadcast(
                context,
                requestCode,
                alarmIntent,
                PendingIntent.FLAG_IMMUTABLE,
            )
        }

        private suspend fun reserveEtaDashboardClose(
            meetingId: Long,
            meetingDateTime: LocalDateTime,
        ) {
            val reserveMillis = meetingDateTime.etaDashboardCloseMillis()
            val reserveId = etaReserveDao.save(EtaReserveEntity(meetingId, reserveMillis))
            val pendingIntent = createClosePendingIntent(meetingId, reserveId.toInt())
            reserve(reserveMillis, pendingIntent)
        }

        private fun LocalDateTime.etaDashboardCloseMillis(): Long {
            return plusMinutes(ETA_CLOSE_MINUTE).toMilliSeconds()
        }

        private fun createClosePendingIntent(meetingId: Long, requestCode: Int): PendingIntent {
            val intent =
                Intent(context, EtaDashboardCloseBroadcastReceiver::class.java)
                    .putExtra(MEETING_ID_KEY, meetingId)

            return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE,
            )
        }

        @SuppressLint("ScheduleExactAlarm")
        private fun reserve(
            triggerAtMillis: Long,
            pendingIntent: PendingIntent,
        ) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent,
            )
        }

        companion object {
            private const val ETA_OPEN_MINUTE = 30L
            private const val ETA_CLOSE_MINUTE = 2L
        }
    }
