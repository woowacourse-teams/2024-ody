package com.mulberry.ody.presentation.room.etadashboard

import android.os.Bundle
import android.view.View
import com.mulberry.ody.R
import com.mulberry.ody.databinding.FragmentEtaDashboardGuideSecondBinding
import com.mulberry.ody.presentation.common.binding.BindingFragment
import com.mulberry.ody.presentation.common.listener.setOnSingleClickListener

class EtaDashboardGuideSecondFragment :
    BindingFragment<FragmentEtaDashboardGuideSecondBinding>(R.layout.fragment_eta_dashboard_guide_second) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
    }

    private fun initializeView() {
        binding.title = getString(R.string.eta_dashboard_guide_title)
        binding.layoutEtaDashboardGuideClose.setOnSingleClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}
