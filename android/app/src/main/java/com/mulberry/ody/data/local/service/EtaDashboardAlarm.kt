package com.mulberry.ody.data.local.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.mulberry.ody.data.local.service.EtaDashboardService.Companion.MEETING_ID_KEY
import javax.inject.Inject

class EtaDashboardAlarm
    @Inject
    constructor(
        private val context: Context,
        private val alarmManager: AlarmManager,
    ) {
        private val pendingIntents: MutableList<PendingIntent> = mutableListOf()

        fun reserve(
            meetingId: Long,
            reserveMillis: Long,
            isOpen: Boolean,
            reservationId: Long,
        ) {
            val pendingIntent = createPendingIntent(meetingId, isOpen, reservationId)
            reserveAlarm(reserveMillis, pendingIntent)
        }

        private fun createPendingIntent(
            meetingId: Long,
            isOpen: Boolean,
            reservationId: Long,
        ): PendingIntent {
            val intentClass =
                if (isOpen) EtaDashboardOpenBroadcastReceiver::class else EtaDashboardCloseBroadcastReceiver::class
            val intent =
                Intent(context, intentClass.java)
                    .putExtra(MEETING_ID_KEY, meetingId)

            return PendingIntent.getBroadcast(
                context,
                reservationId.toInt(),
                intent,
                PendingIntent.FLAG_IMMUTABLE,
            )
        }

        @SuppressLint("ScheduleExactAlarm")
        private fun reserveAlarm(
            triggerAtMillis: Long,
            pendingIntent: PendingIntent,
        ) {
            pendingIntents.add(pendingIntent)
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent,
            )
        }

        fun cancelAll() {
            pendingIntents.forEach {
                alarmManager.cancel(it)
            }
            pendingIntents.clear()
        }
    }
