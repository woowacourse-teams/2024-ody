package com.woowacourse.ody.presentation.room.etadashboard

import android.os.Bundle
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.ActivityEtaDashboardBinding
import com.woowacourse.ody.presentation.common.binding.BindingActivity
import com.woowacourse.ody.presentation.room.etadashboard.adapter.MateEtasAdapter
import com.woowacourse.ody.presentation.room.etadashboard.listener.MissingToolTipListener

class EtaDashboardActivity : BindingActivity<ActivityEtaDashboardBinding>(R.layout.activity_eta_dashboard), MissingToolTipListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.rvDashboard.adapter = MateEtasAdapter(this)
    }

    override fun initializeBinding() {
        // TODO
    }

    override fun onClickMissingToolTipListener() {
        // TODO 팝업 띄우기
    }
}
