package com.mulberry.ody.data.remote.thirdparty.fcm.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mulberry.ody.data.local.db.OdyDatastore
import com.mulberry.ody.domain.model.FCMNotificationType
import com.mulberry.ody.domain.model.FCMType
import com.mulberry.ody.presentation.notification.FCMNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {
    @Inject
    lateinit var odyDatastore: OdyDatastore

    @Inject
    lateinit var fcmNotification: FCMNotification

    override fun onMessageReceived(message: RemoteMessage) {
        val type = message.data["type"] ?: return
        val fcmType = FCMType.from(type)
        val nickname = message.data["nickname"] ?: ""
        val meetingId = message.data["meetingId"] ?: ""
        val meetingName = message.data["meetingName"] ?: ""
        val meetingTime = message.data["meetingTime"]?.toLocalDateTime()

        if (fcmType is FCMNotificationType) {
            fcmNotification.showNotification(fcmType, nickname, meetingId, meetingName)
        }

        if (fcmType.isEtaType()) {
            // service start
        }
    }

    override fun onNewToken(token: String) {
        runBlocking {
            odyDatastore.setFCMToken(token)
        }
    }
}

private fun String.toLocalDateTime(): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") // 형식 지정
    return LocalDateTime.parse(this, formatter)
}
