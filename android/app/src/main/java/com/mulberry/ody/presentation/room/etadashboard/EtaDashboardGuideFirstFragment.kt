package com.mulberry.ody.presentation.room.etadashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import com.mulberry.ody.R
import com.mulberry.ody.databinding.FragmentEtaDashboardGuideFirstBinding
import com.mulberry.ody.presentation.common.binding.BindingFragment

class EtaDashboardGuideFirstFragment :
    BindingFragment<FragmentEtaDashboardGuideFirstBinding>(R.layout.fragment_eta_dashboard_guide_first) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
    }

    private fun initializeView() {
        binding.title = getString(R.string.eta_dashboard_guide_title)
        binding.layoutEtaDashboardGuideNext.setOnClickListener {
            startEtaDashboardNextGuide()
        }
    }

    private fun startEtaDashboardNextGuide() {
        parentFragmentManager.commit {
            add(R.id.fcv_eta_dashboard, EtaDashboardGuideSecondFragment())
            setReorderingAllowed(true)
            addToBackStack(null)
            parentFragmentManager.popBackStack()
        }
    }
}
