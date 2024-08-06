package com.woowacourse.ody.presentation.room.etadashboard

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.ActivityEtaDashboardBinding
import com.woowacourse.ody.presentation.common.binding.BindingActivity

class EtaDashboardActivity :
    BindingActivity<ActivityEtaDashboardBinding>(R.layout.activity_eta_dashboard) {
    private val viewModel: EtaDashboardViewModel by viewModels<EtaDashboardViewModel> {
        EtaDashboardViewModelFactory(
            meetingId = intent.getLongExtra(MEETING_ID_KEY, MEETING_ID_DEFAULT_VALUE),
            matesEtaRepository = application.matesEtaRepository
        )
    }

    override fun initializeBinding() {
        viewModel.matesEta.observe(this) {
            // todo
        }
    }

    companion object {
        private const val MEETING_ID_KEY = "meeting_id"
        private const val MEETING_ID_DEFAULT_VALUE = -1L

        fun getIntent(
            context: Context,
            meetingId: Long,
        ): Intent {
            return Intent(context, EtaDashboardActivity::class.java).apply {
                putExtra(MEETING_ID_KEY, meetingId)
            }
        }
    }
}
