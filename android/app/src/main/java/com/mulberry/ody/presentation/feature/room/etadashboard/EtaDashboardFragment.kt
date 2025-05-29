package com.mulberry.ody.presentation.feature.room.etadashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.mulberry.ody.R
import com.mulberry.ody.data.local.db.OdyDatastore
import com.mulberry.ody.presentation.theme.OdyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EtaDashboardFragment : Fragment() {
    @Inject
    lateinit var odyDatastore: OdyDatastore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed,
            )
            setContent {
                OdyTheme {
                    EtaDashboardScreen(onClickBack = ::onBack)
                }
            }
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializeGuide()
    }

    private fun initializeGuide() {
        lifecycleScope.launch {
            val isFirstSeenEtaDashboard = odyDatastore.getIsFirstSeenEtaDashboard().first()
            if (isFirstSeenEtaDashboard) {
                startEtaDashboardGuide()
                odyDatastore.setIsFirstSeenEtaDashboard(false)
            }
        }
    }

    private fun startEtaDashboardGuide() {
//        childFragmentManager.commit {
//            add(R.id.fcv_eta_dashboard, EtaDashboardGuideFirstFragment())
//            setReorderingAllowed(true)
//            addToBackStack(null)
//        }
    }

    private fun onBack() {
        parentFragmentManager.popBackStack()
    }
}
