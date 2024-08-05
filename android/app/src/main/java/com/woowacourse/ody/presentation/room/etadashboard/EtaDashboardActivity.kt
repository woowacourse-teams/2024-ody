package com.woowacourse.ody.presentation.room.etadashboard

import androidx.activity.viewModels
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.ActivityEtaDashboardBinding
import com.woowacourse.ody.presentation.common.binding.BindingActivity

class EtaDashboardActivity : BindingActivity<ActivityEtaDashboardBinding>(R.layout.activity_eta_dashboard) {
    private val viewModel: EtaDashboardViewModel by viewModels<EtaDashboardViewModel> {
        EtaDashboardViewModelFactory(meetingId = 1, matesEtaRepository = application.matesEtaRepository)
    }

    override fun initializeBinding() {
        viewModel.matesEta.observe(this) {
        }
    }
}
