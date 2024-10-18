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
) {
    fun reserveEtaDashboardOpen(
        meetingId: Long,
        reserveMillis: Long,
        requestCode: Int
    ) {
        val pendingIntent = createOpenPendingIntent(meetingId, requestCode)
        reserve(reserveMillis, pendingIntent)
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

    fun reserveEtaDashboardClose(
        meetingId: Long,
        reserveMillis: Long,
        requestCode: Int,
    ) {
        val pendingIntent = createClosePendingIntent(meetingId, requestCode)
        reserve(reserveMillis, pendingIntent)
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
        const val ETA_RESERVATION_ID_KEY = "eta_reserve_id"
        const val ETA_RESERVATION_ID_DEFAULT_VALUE = -1L
    }
}
