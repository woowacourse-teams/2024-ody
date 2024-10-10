package com.mulberry.ody.data.local.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.mulberry.ody.data.local.service.EtaDashboardService.Companion.MEETING_ID_KEY
import com.mulberry.ody.domain.common.toMilliSeconds
import java.time.LocalDateTime
import kotlin.math.max

class EtaDashboardAlarm(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun reserveEtaDashboard(
        meetingId: Long,
        meetingDateTime: LocalDateTime,
    ) {
        val initialTime = meetingDateTime.minusMinutes(BEFORE_MINUTE).toMilliSeconds()
        val endTime = meetingDateTime.plusMinutes(AFTER_MINUTE).toMilliSeconds()
        val nowTime = System.currentTimeMillis()
        val startTime = max(initialTime, nowTime)

        reserveEtaDashboardOpen(meetingId, startTime)
        reserveEtaDashboardClose(meetingId, endTime)
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun reserveEtaDashboardOpen(
        meetingId: Long,
        reserveMillis: Long,
    ) {
        val alarmPendingIntent: PendingIntent = createOpenPendingIntent(meetingId)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reserveMillis,
            alarmPendingIntent
        )
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

    @SuppressLint("ScheduleExactAlarm")
    private fun reserveEtaDashboardClose(
        meetingId: Long,
        reserveMillis: Long,
    ) {
        val alarmPendingIntent: PendingIntent = createClosePendingIntent(meetingId)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reserveMillis,
            alarmPendingIntent
        )
    }

    private fun createClosePendingIntent(meetingId: Long): PendingIntent {
        val intent =
            Intent(context, EtaDashboardCloseBroadcastReceiver::class.java)
                .putExtra(MEETING_ID_KEY, meetingId)

        return PendingIntent.getBroadcast(
            context,
            meetingId.toInt() * -1,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        private const val BEFORE_MINUTE = 30L
        private const val AFTER_MINUTE = 1L
    }
}
