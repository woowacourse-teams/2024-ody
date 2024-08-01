package com.woowacourse.ody.data.remote.thirdparty.fcm.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.woowacourse.ody.OdyApplication
import com.woowacourse.ody.R
import com.woowacourse.ody.data.remote.core.RetrofitClient
import com.woowacourse.ody.data.remote.core.service.MemberService
import com.woowacourse.ody.domain.model.NotificationType
import kotlinx.coroutines.runBlocking

class FCMService : FirebaseMessagingService() {
    private val odyApplication by lazy { applicationContext as OdyApplication }

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.getNotification()?.title
        val type =
            message.data["type"]?.let { NotificationType.from(it) } ?: NotificationType.DEFAULT
        val nickname = message.data["nickname"] ?: ""
        showNotification(title, makeNotificationMessage(type, nickname))
    }

    private fun makeNotificationMessage(
        type: NotificationType,
        nickname: String,
    ): String =
        when (type) {
            NotificationType.ENTRY -> {
                getString(R.string.fcm_notification_entry, nickname)
            }

            NotificationType.DEPARTURE_REMINDER -> {
                getString(R.string.fcm_notification_departure_reminder, nickname)
            }

            NotificationType.DEPARTURE -> {
                getString(R.string.fcm_notification_departure, nickname)
            }

            NotificationType.DEFAULT -> {
                ""
            }
        }

    private fun showNotification(
        title: String?,
        msg: String?,
    ) {
        val mBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(longArrayOf(1, 1000))

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

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

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    override fun onNewToken(token: String) {
        val retrofit = RetrofitClient().retrofit
        val memberService = retrofit.create(MemberService::class.java)
        runBlocking {
            odyApplication.fcmTokenRepository.postFCMToken(token)
            memberService.postMember()
        }
        setNotificationChannel()
    }

    companion object {
        const val NOTIFICATION_CHANNEL_NAME = "notification_channel_name"
        const val NOTIFICATION_DESCRIPTION = "notification_description_name"
        const val NOTIFICATION_CHANNEL_ID = "notification_id"
    }
}
