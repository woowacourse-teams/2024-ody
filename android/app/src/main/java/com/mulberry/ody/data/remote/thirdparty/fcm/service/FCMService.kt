package com.mulberry.ody.data.remote.thirdparty.fcm.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mulberry.ody.data.local.db.OdyDataStore
import com.mulberry.ody.domain.model.EtaOpenInfo
import com.mulberry.ody.domain.model.FCMType
import com.mulberry.ody.domain.model.MeetingDateTime
import com.mulberry.ody.domain.model.MessageType
import com.mulberry.ody.domain.model.NotificationType
import com.mulberry.ody.domain.usecase.OpenEtaDashboardUseCase
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
    lateinit var odyDataStore: OdyDataStore

    @Inject
    lateinit var fcmNotification: FCMNotification

    @Inject
    lateinit var openEtaDashboardUseCase: OpenEtaDashboardUseCase

    override fun onMessageReceived(message: RemoteMessage) {
        val type = message.data["type"] ?: return
        val fcmType = FCMType.from(type)
        val nickname = message.data["nickname"] ?: ""
        val meetingId = message.data["meetingId"]?.toLongOrNull() ?: return
        val meetingName = message.data["meetingName"] ?: ""

        if (fcmType is NotificationType) {
            fcmNotification.showNotification(fcmType, nickname, meetingId, meetingName)
        }

        val meetingTime = message.data["meetingTime"]?.toLocalDateTime() ?: return

        if (fcmType == MessageType.ETA_SCHEDULING_NOTICE) {
            openEta(meetingId, meetingTime)
        }
    }

    override fun onNewToken(token: String) {
        CoroutineScope(Dispatchers.Default).launch {
            odyDataStore.setFCMToken(token)
        }
    }

    private fun openEta(
        meetingId: Long,
        meetingTime: LocalDateTime,
    ) {
        CoroutineScope(Dispatchers.Default).launch {
            val etaOpenInfo = EtaOpenInfo(meetingId, MeetingDateTime(meetingTime))
            openEtaDashboardUseCase(etaOpenInfo)
        }
    }
}

private fun String.toLocalDateTime(): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return LocalDateTime.parse(this, formatter)
}
