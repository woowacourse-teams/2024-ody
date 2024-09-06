package com.ydo.ody.data.remote.thirdparty.fcm.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ydo.ody.OdyApplication
import com.ydo.ody.domain.model.NotificationType
import kotlinx.coroutines.runBlocking

class FCMService : FirebaseMessagingService() {
    private val odyApplication by lazy { applicationContext as OdyApplication }
    private val notificationHelper by lazy { odyApplication.notificationHelper }

    override fun onMessageReceived(message: RemoteMessage) {
        val type =
            message.data["type"]?.let { NotificationType.from(it) } ?: NotificationType.DEFAULT
        val nickname = message.data["nickname"] ?: ""
        notificationHelper.showTypedNotification(type, nickname)
    }

    override fun onNewToken(token: String) {
        runBlocking {
            odyApplication.fcmTokenRepository.postFCMToken(token)
        }
    }
}
