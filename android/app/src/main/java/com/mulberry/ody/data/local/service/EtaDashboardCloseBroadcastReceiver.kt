package com.mulberry.ody.data.local.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mulberry.ody.data.local.service.EtaDashboardService.Companion.MEETING_ID_DEFAULT_VALUE
import com.mulberry.ody.data.local.service.EtaDashboardService.Companion.MEETING_ID_KEY
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EtaDashboardCloseBroadcastReceiver : BroadcastReceiver() {
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val meetingId = intent.getLongExtra(MEETING_ID_KEY, MEETING_ID_DEFAULT_VALUE)
        if (meetingId == MEETING_ID_DEFAULT_VALUE) return

        val matesEtaRepository =
            EtaDashboardCloseBroadcastReceiverEntryPoint.matesEtaRepository(context)
        coroutineScope.launch {
            matesEtaRepository.deleteEtaReservation(meetingId)
        }

        val serviceIntent = EtaDashboardService.getIntent(context, meetingId, isOpen = false)
        context.startForegroundService(serviceIntent)
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface EtaDashboardCloseBroadcastReceiverEntryPoint {
        fun matesEtaRepository(): MatesEtaRepository

        companion object {
            fun matesEtaRepository(context: Context): MatesEtaRepository {
                return EntryPointAccessors
                    .fromApplication(
                        context,
                        EtaDashboardCloseBroadcastReceiverEntryPoint::class.java,
                    )
                    .matesEtaRepository()
            }
        }
    }
}
