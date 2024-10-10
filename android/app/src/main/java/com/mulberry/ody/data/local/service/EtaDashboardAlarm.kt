package com.mulberry.ody.data.local.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.mulberry.ody.data.local.service.EtaDashboardService.Companion.MEETING_ID_KEY
import com.mulberry.ody.domain.common.toMilliSeconds
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.math.max

class EtaDashboardAlarm
@Inject constructor(
    private val context: Context,
    private val alarmManager: AlarmManager,
) {
    fun reserveEtaDashboard(
        meetingId: Long,
        meetingDateTime: LocalDateTime,
    ) {
        reserveEtaDashboardOpen(meetingId, meetingDateTime)
        reserveEtaDashboardClose(meetingId, meetingDateTime)
    }

    private fun reserveEtaDashboardOpen(
        meetingId: Long,
        meetingDateTime: LocalDateTime,
    ) {
        val reserveMillis = meetingDateTime.etaDashboardOpenMillis()
        val pendingIntent = createOpenPendingIntent(meetingId)
        reserve(reserveMillis, pendingIntent)
    }

    private fun LocalDateTime.etaDashboardOpenMillis(): Long {
        val openMillis = minusMinutes(ETA_OPEN_MINUTE).toMilliSeconds()
        val nowMillis = System.currentTimeMillis()
        return max(openMillis, nowMillis)
    }

    private fun createOpenPendingIntent(meetingId: Long): PendingIntent {
        val alarmIntent =
            Intent(context, EtaDashboardOpenBroadcastReceiver::class.java)
                .putExtra(MEETING_ID_KEY, meetingId)

        return PendingIntent.getBroadcast(
            context,
            meetingId.toInt(),
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun reserveEtaDashboardClose(
        meetingId: Long,
        meetingDateTime: LocalDateTime,
    ) {
        val reserveMillis = meetingDateTime.etaDashboardCloseMillis()
        val pendingIntent = createClosePendingIntent(meetingId)
        reserve(reserveMillis, pendingIntent)
    }

    private fun LocalDateTime.etaDashboardCloseMillis(): Long {
        return plusMinutes(ETA_CLOSE_MINUTE).toMilliSeconds()
    }

    private fun createClosePendingIntent(meetingId: Long): PendingIntent {
        val intent =
            Intent(context, EtaDashboardCloseBroadcastReceiver::class.java)
                .putExtra(MEETING_ID_KEY, meetingId)

        return PendingIntent.getBroadcast(
            context,
            meetingId.toInt() * CLOSE_PENDING_INTENT_VALUE,
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
        private const val CLOSE_PENDING_INTENT_VALUE = -1
        private const val ETA_OPEN_MINUTE = 30L
        private const val ETA_CLOSE_MINUTE = 1L
    }
}
