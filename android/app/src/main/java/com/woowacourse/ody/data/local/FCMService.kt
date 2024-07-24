package com.woowacourse.ody.data.local

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.woowacourse.ody.data.remote.RetrofitClient
import com.woowacourse.ody.data.remote.service.MemberService
import com.woowacourse.ody.presentation.intro.IntroActivity
import kotlinx.coroutines.runBlocking

class FCMService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.getNotification()?.title
        val msg = message.getNotification()?.body
        notify(title, msg)
    }

    private fun notify(
        title: String?,
        msg: String?,
    ) {
        val intent = Intent(this, IntroActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val contentIntent =
            PendingIntent.getActivity(
                this,
                0,
                Intent(
                    this,
                    IntroActivity::class.java,
                ),
                PendingIntent.FLAG_IMMUTABLE,
            )

        val mBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.sym_def_app_icon)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(longArrayOf(1, 1000))

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, mBuilder.build())

        mBuilder.setContentIntent(contentIntent)
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
        val retrofit = RetrofitClient.retrofit
        val memberService = retrofit.create(MemberService::class.java)
        runBlocking {
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
