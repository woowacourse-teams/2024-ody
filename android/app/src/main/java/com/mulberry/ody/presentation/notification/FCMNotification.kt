package com.mulberry.ody.presentation.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.mulberry.ody.R
import com.mulberry.ody.domain.model.NotificationType
import com.mulberry.ody.presentation.room.MeetingRoomActivity
import com.mulberry.ody.presentation.room.MeetingRoomActivity.Companion.NAVIGATE_TO_ETA_DASHBOARD
import com.mulberry.ody.presentation.room.MeetingRoomActivity.Companion.NAVIGATE_TO_NOTIFICATION_LOG

class FCMNotification(private val context: Context) {
    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel =
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH,
            )
        channel.description = NOTIFICATION_DESCRIPTION

        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun showNotification(
        type: NotificationType,
        nickname: String,
        meetingId: String,
        meetingName: String,
    ) {
        val message = getNotificationMessage(type, nickname, meetingName)
        val pendingIntent = getPendingIntent(type, meetingId)
        showNotification(message, pendingIntent)
    }

    private fun getNotificationMessage(
        type: NotificationType,
        nickname: String,
        meetingName: String,
    ): String =
        when (type) {
            NotificationType.ENTRY -> context.getString(R.string.fcm_notification_entry, nickname)
            NotificationType.DEPARTURE_REMINDER -> context.getString(R.string.fcm_notification_departure_reminder, nickname)
            NotificationType.NUDGE -> context.getString(R.string.fcm_notification_nudge, nickname)
            NotificationType.ETA_NOTICE -> context.getString(R.string.fcm_notification_eta_notice, meetingName)
            NotificationType.DEFAULT -> ""
        }

    private fun getPendingIntent(
        type: NotificationType,
        meetingId: String,
    ): PendingIntent? {
        val navigationTarget =
            when (type) {
                NotificationType.ENTRY,
                NotificationType.DEPARTURE_REMINDER,
                -> NAVIGATE_TO_NOTIFICATION_LOG
                NotificationType.NUDGE,
                NotificationType.ETA_NOTICE,
                -> NAVIGATE_TO_ETA_DASHBOARD
                NotificationType.DEFAULT -> ""
            }

        val intent = MeetingRoomActivity.getIntent(context, meetingId.toLong(), navigationTarget)
        return PendingIntent.getActivity(
            context,
            NOTIFICATION_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }

    private fun showNotification(
        msg: String?,
        intent: PendingIntent?,
    ) {
        val mBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(context.getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(msg)
                .setContentIntent(intent)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(longArrayOf(1, 1000))

        val notificationManager: NotificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, mBuilder.build())
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_NAME = "notification_channel_name"
        private const val NOTIFICATION_DESCRIPTION = "notification_description_name"
        private const val NOTIFICATION_CHANNEL_ID = "notification_id"
        private const val NOTIFICATION_REQUEST_CODE = 1000
    }
}
