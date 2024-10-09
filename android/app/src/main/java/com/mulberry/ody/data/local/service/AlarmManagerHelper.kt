package com.mulberry.ody.data.local.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

class AlarmManagerHelper(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @SuppressLint("ScheduleExactAlarm")
    fun reserveEtaDashboardOpen(
        meetingId: Long,
        reserveMillis: Long,
        reserveCount: Int,
    ) {
        val alarmPendingIntent: PendingIntent = createAlarmPendingIntent(meetingId, reserveCount)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reserveMillis,
            alarmPendingIntent
        )
    }

    private fun createAlarmPendingIntent(meetingId: Long, reserveCount: Int): PendingIntent {
        val alarmIntent =
            Intent(context, EtaDashboardBroadcastReceiver::class.java)
                .putExtra(MEETING_ID_KEY, meetingId)
                .putExtra(RESERVE_COUNT_KEY, reserveCount)

        return PendingIntent.getBroadcast(
            context,
            meetingId.toInt(),
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        const val MEETING_ID_KEY = "meeting_id"
        const val MEETING_ID_DEFAULT_VALUE = -1L

        const val RESERVE_COUNT_KEY = "reserve_count"
        const val RESERVE_COUNT_DEFAULT_VALUE = -1
    }
}
