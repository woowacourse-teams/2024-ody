package com.mulberry.ody.presentation.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.mulberry.ody.R
import com.mulberry.ody.data.local.db.OdyDatastore
import com.mulberry.ody.domain.model.NotificationType
import com.mulberry.ody.presentation.feature.meetings.MeetingsActivity
import com.mulberry.ody.presentation.feature.room.MeetingRoomActivity
import com.mulberry.ody.presentation.feature.room.MeetingRoomActivity.Companion.NAVIGATE_TO_DETAIL_MEETING
import com.mulberry.ody.presentation.feature.room.MeetingRoomActivity.Companion.NAVIGATE_TO_ETA_DASHBOARD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class FCMNotification
    @Inject
    constructor(
        private val context: Context,
        private val odyDatastore: OdyDatastore,
        private val notificationManager: NotificationManager,
    ) {
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

            notificationManager.createNotificationChannel(channel)
        }

        fun showNotification(
            type: NotificationType,
            nickname: String,
            meetingId: String,
            meetingName: String,
        ) {
            CoroutineScope(Dispatchers.Default).launch {
                if (isNotificationBlock(type)) {
                    return@launch
                }

                val message = getNotificationMessage(type, nickname, meetingName)
                val pendingIntent = getPendingIntent(type, meetingId)
                showNotification(message, pendingIntent)
            }
        }

        private suspend fun isNotificationBlock(type: NotificationType): Boolean {
            if (type == NotificationType.DEPARTURE_REMINDER && !odyDatastore.getIsNotificationOn(type).first()) {
                return true
            }
            if (type == NotificationType.ENTRY && !odyDatastore.getIsNotificationOn(type).first()) {
                return true
            }
            return false
        }

        private fun getNotificationMessage(
            type: NotificationType,
            nickname: String,
            meetingName: String,
        ): String =
            when (type) {
                NotificationType.ENTRY -> context.getString(R.string.fcm_notification_entry, nickname)
                NotificationType.DEPARTURE_REMINDER ->
                    context.getString(R.string.fcm_notification_departure_reminder, nickname)

                NotificationType.NUDGE -> context.getString(R.string.fcm_notification_nudge, nickname)
                NotificationType.ETA_NOTICE ->
                    context.getString(R.string.fcm_notification_eta_notice, meetingName)

                NotificationType.DEFAULT -> ""
            }

        private fun getPendingIntent(
            type: NotificationType,
            meetingId: String,
        ): PendingIntent? {
            val navigationTarget =
                when (type) {
                    NotificationType.ENTRY, NotificationType.DEPARTURE_REMINDER -> NAVIGATE_TO_DETAIL_MEETING
                    NotificationType.NUDGE, NotificationType.ETA_NOTICE -> NAVIGATE_TO_ETA_DASHBOARD
                    NotificationType.DEFAULT -> ""
                }

            val stackBuilder = TaskStackBuilder.create(context)
            val parentIntent = MeetingsActivity.getIntent(context)
            stackBuilder.addNextIntent(parentIntent)

            val meetingRoomIntent = MeetingRoomActivity.getIntent(context, meetingId.toLong(), navigationTarget)
            stackBuilder.addNextIntent(meetingRoomIntent)

            return stackBuilder.getPendingIntent(
                NOTIFICATION_REQUEST_CODE,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
        }

        private fun showNotification(
            msg: String?,
            intent: PendingIntent?,
        ) {
            val notificationBuilder =
                NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText(msg)
                    .setContentIntent(intent)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setVibrate(VIBRATE_PATTERN)

            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        }

        companion object {
            private const val NOTIFICATION_ID = 0
            private const val NOTIFICATION_CHANNEL_NAME = "notification_channel_name"
            private const val NOTIFICATION_DESCRIPTION = "notification_description_name"
            private const val NOTIFICATION_CHANNEL_ID = "notification_id"
            private const val NOTIFICATION_REQUEST_CODE = 1000
            private val VIBRATE_PATTERN: LongArray = longArrayOf(1, 1000)
        }
    }
