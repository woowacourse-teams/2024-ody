package com.mulberry.ody.data.local.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mulberry.ody.data.local.service.EtaDashboardService.Companion.MEETING_ID_DEFAULT_VALUE
import com.mulberry.ody.data.local.service.EtaDashboardService.Companion.MEETING_ID_KEY
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EtaDashboardOpenBroadcastReceiver : BroadcastReceiver() {
    @Inject
    lateinit var matesEtaRepository: MatesEtaRepository

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val meetingId = intent.getLongExtra(MEETING_ID_KEY, MEETING_ID_DEFAULT_VALUE)
        if (meetingId == MEETING_ID_DEFAULT_VALUE) return

        val serviceIntent = EtaDashboardService.getIntent(context, meetingId)
        context.startForegroundService(serviceIntent)
    }
}
