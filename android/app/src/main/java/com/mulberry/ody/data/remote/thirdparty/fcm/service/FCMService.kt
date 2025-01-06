package com.mulberry.ody.data.remote.thirdparty.fcm.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mulberry.ody.data.local.db.OdyDatastore
import com.mulberry.ody.data.local.service.EtaDashboard
import com.mulberry.ody.domain.model.FCMMessageType
import com.mulberry.ody.domain.model.FCMNotificationType
import com.mulberry.ody.domain.model.FCMType
import com.mulberry.ody.presentation.notification.FCMNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {
    @Inject
    lateinit var odyDatastore: OdyDatastore

    @Inject
    lateinit var fcmNotification: FCMNotification

    @Inject
    lateinit var etaDashboard: EtaDashboard

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

        if (fcmType == FCMMessageType.ETA_SCHEDULING_NOTICE) {
            etaDashboard.open(meetingId.toLong(), meetingTime ?: return)
        }
    }

    override fun onNewToken(token: String) {
        CoroutineScope(Dispatchers.Default).launch {
            odyDatastore.setFCMToken(token)
        }
    }
}

private fun String.toLocalDateTime(): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") // 형식 지정
    return LocalDateTime.parse(this, formatter)
}
