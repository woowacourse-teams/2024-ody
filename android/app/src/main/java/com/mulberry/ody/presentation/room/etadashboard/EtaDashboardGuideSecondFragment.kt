package com.mulberry.ody.presentation.room.etadashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mulberry.ody.databinding.FragmentEtaDashboardGuideSecondBinding

class EtaDashboardGuideSecondFragment : Fragment() {
    private var _binding: FragmentEtaDashboardGuideSecondBinding? = null
    private val binding: FragmentEtaDashboardGuideSecondBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEtaDashboardGuideSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
    }

    private fun initializeView() {
        binding.layoutEtaDashboardGuideClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
