package com.mulberry.ody.data.remote.thirdparty.fcm.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mulberry.ody.domain.model.NotificationType
import com.mulberry.ody.domain.repository.ody.FCMTokenRepository
import com.mulberry.ody.presentation.notification.FCMNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {
    @Inject
    lateinit var fcmTokenRepository: FCMTokenRepository

    @Inject
    lateinit var fcmNotification: FCMNotification

    override fun onMessageReceived(message: RemoteMessage) {
        val type =
            message.data["type"]?.let { NotificationType.from(it) } ?: NotificationType.DEFAULT
        val nickname = message.data["nickname"] ?: ""
        val meetingId = message.data["meetingId"] ?: ""
        val meetingName = message.data["meetingName"] ?: ""
        fcmNotification.showNotification(type, nickname, meetingId, meetingName)
    }

    override fun onNewToken(token: String) {
        runBlocking {
            fcmTokenRepository.postFCMToken(token)
        }
    }
}
