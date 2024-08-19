package com.woowacourse.ody.presentation.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.woowacourse.ody.R
import com.woowacourse.ody.domain.model.NotificationType

class NotificationHelper(private val context: Context) {
    init {
        setNotificationChannel()
    }

    private fun makeNotificationMessage(
        type: NotificationType,
        nickname: String,
    ): String =
        when (type) {
            NotificationType.ENTRY -> {
                context.getString(R.string.fcm_notification_entry, nickname)
            }

            NotificationType.DEPARTURE_REMINDER -> {
                context.getString(R.string.fcm_notification_departure_reminder, nickname)
            }

            NotificationType.DEPARTURE -> {
                context.getString(R.string.fcm_notification_departure, nickname)
            }

            NotificationType.NUDGE -> {
                context.getString(R.string.fcm_notification_nudge)
            }

            NotificationType.DEFAULT -> {
                ""
            }
        }

    fun showTypedNotification(
        type: NotificationType,
        nickname: String,
    ) {
        val title = context.getString(R.string.app_name)
        val msg = makeNotificationMessage(type, nickname)
        showNotification(title, msg)
    }

    private fun showNotification(
        title: String?,
        msg: String?,
    ) {
        val mBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(longArrayOf(1, 1000))

        val notificationManager: NotificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, mBuilder.build())
    }

    private fun setNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel =
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                importance,
            )
        mChannel.description = NOTIFICATION_DESCRIPTION

        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    companion object {
        const val NOTIFICATION_CHANNEL_NAME = "notification_channel_name"
        const val NOTIFICATION_DESCRIPTION = "notification_description_name"
        const val NOTIFICATION_CHANNEL_ID = "notification_id"
    }
}
