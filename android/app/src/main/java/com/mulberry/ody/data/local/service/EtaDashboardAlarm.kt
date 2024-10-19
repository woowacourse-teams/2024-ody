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

        fun reserveEtaDashboardOpen(
            meetingId: Long,
            reserveMillis: Long,
            etaReservationId: Long,
        ) {
            val pendingIntent = createEtaDashboardPendingIntent(meetingId, etaReservationId, isOpen = true)
            reserve(reserveMillis, pendingIntent)
        }

        fun reserveEtaDashboardClose(
            meetingId: Long,
            reserveMillis: Long,
            etaReservationId: Long,
        ) {
            val pendingIntent = createEtaDashboardPendingIntent(meetingId, etaReservationId, isOpen = false)
            reserve(reserveMillis, pendingIntent)
        }

        @SuppressLint("ScheduleExactAlarm")
        private fun reserve(
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

        private fun createEtaDashboardPendingIntent(
            meetingId: Long,
            etaReservationId: Long,
            isOpen: Boolean,
        ): PendingIntent {
            val intentClass =
                if (isOpen) EtaDashboardOpenBroadcastReceiver::class else EtaDashboardCloseBroadcastReceiver::class
            val intent =
                Intent(context, intentClass.java)
                    .putExtra(MEETING_ID_KEY, meetingId)

            return PendingIntent.getBroadcast(
                context,
                etaReservationId.toInt(),
                intent,
                PendingIntent.FLAG_IMMUTABLE,
            )
        }

        fun cancelAll() {
            pendingIntents.forEach {
                alarmManager.cancel(it)
            }
            pendingIntents.clear()
        }

        fun reserveEtaDashboard(
            meetingId: Long,
            reserveMillis: Long,
            isOpen: Boolean,
            etaReservationId: Long,
        ) {
            val pendingIntent = createEtaDashboardPendingIntent(meetingId, etaReservationId, isOpen)
            reserve(reserveMillis, pendingIntent)
        }
    }
