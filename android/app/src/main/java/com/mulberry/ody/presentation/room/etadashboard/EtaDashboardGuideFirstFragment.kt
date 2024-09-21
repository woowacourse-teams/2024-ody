package com.mulberry.ody.presentation.room.etadashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mulberry.ody.R
import com.mulberry.ody.databinding.FragmentEtaDashboardGuideFirstBinding

class EtaDashboardGuideFirstFragment : Fragment() {
    private var _binding: FragmentEtaDashboardGuideFirstBinding? = null
    private val binding: FragmentEtaDashboardGuideFirstBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEtaDashboardGuideFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
    }

    private fun initializeView() {
        binding.layoutEtaDashboardGuideNext.setOnClickListener {
            startEtaDashboardNextGuide()
            parentFragmentManager.popBackStack()
        }
    }

    private fun startEtaDashboardNextGuide() {
        parentFragmentManager.commit {
            add(R.id.fcv_eta_dashboard, EtaDashboardGuideSecondFragment())
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
