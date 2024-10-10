package com.mulberry.ody.data.local.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.mulberry.ody.R
import com.mulberry.ody.presentation.meetings.MeetingsActivity
import javax.inject.Inject

class EtaDashboardNotification
    @Inject
    constructor(
        private val context: Context,
        private val notificationManager: NotificationManager,
    ) {
        init {
            createNotificationChannel()
        }

        private fun createNotificationChannel() {
            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH,
                )
            channel.description = CHANNEL_DESCRIPTION

            notificationManager.createNotificationChannel(channel)
        }

        fun createNotification(meetingId: Long): Notification {
            val pendingIntent = createPendingIntent(meetingId)

            return NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(context.getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(context.getString(R.string.eta_dashboard_background_location))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(VIBRATE_PATTERN)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()
        }

        private fun createPendingIntent(meetingId: Long): PendingIntent {
            val intent = Intent(context, MeetingsActivity::class.java)
            return PendingIntent.getActivity(
                context,
                meetingId.toInt(),
                intent,
                PendingIntent.FLAG_IMMUTABLE,
            )
        }

        companion object {
            private const val CHANNEL_ID = "channel_id"
            private const val CHANNEL_NAME = "channel_name"
            private const val CHANNEL_DESCRIPTION = "channel_description"
            private val VIBRATE_PATTERN: LongArray = longArrayOf(1, 1000)
        }
    }
