package com.mulberry.ody.data.local.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReinstallBroadcastReceiver : BroadcastReceiver() {
    @Inject
    lateinit var matesEtaRepository: MatesEtaRepository
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        coroutineScope.launch {
            matesEtaRepository.reserveAllEtaReservation()
        }
    }
}
