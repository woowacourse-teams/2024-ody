package com.mulberry.ody.data.local.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mulberry.ody.data.local.service.EtaDashboardAlarm.Companion.ETA_RESERVATION_ID_DEFAULT_VALUE
import com.mulberry.ody.data.local.service.EtaDashboardAlarm.Companion.ETA_RESERVATION_ID_KEY
import com.mulberry.ody.data.local.service.EtaDashboardService.Companion.MEETING_ID_DEFAULT_VALUE
import com.mulberry.ody.data.local.service.EtaDashboardService.Companion.MEETING_ID_KEY
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class EtaDashboardCloseBroadcastReceiver : BroadcastReceiver() {
    @Inject
    lateinit var matesEtaRepository: MatesEtaRepository
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val meetingId = intent.getLongExtra(MEETING_ID_KEY, MEETING_ID_DEFAULT_VALUE)
        if (meetingId == MEETING_ID_DEFAULT_VALUE) return


        val etaReservationId = intent.getLongExtra(ETA_RESERVATION_ID_KEY, ETA_RESERVATION_ID_DEFAULT_VALUE)
        if (etaReservationId == ETA_RESERVATION_ID_DEFAULT_VALUE) return

        coroutineScope.launch {
            matesEtaRepository.deleteEtaReservation(etaReservationId)
        }

        val serviceIntent = EtaDashboardService.getIntent(context, meetingId, isOpen = false)
        context.startForegroundService(serviceIntent)
    }
}
