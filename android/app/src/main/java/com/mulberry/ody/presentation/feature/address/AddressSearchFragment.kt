package com.mulberry.ody.presentation.feature.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.presentation.feature.address.listener.AddressSearchListener
import com.mulberry.ody.presentation.theme.OdyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressSearchFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed,
            )
            setContent {
                OdyTheme {
                    AddressSearchScreen(
                        onClickBack = ::onBack,
                        onClickAddress = ::sendAddress,
                    )
                }
            }
        }
    }

    private fun sendAddress(address: Address) {
        (parentFragment as? AddressSearchListener)?.onReceive(address)
        (activity as? AddressSearchListener)?.onReceive(address)
        onBack()
    }

    private fun onBack() {
        parentFragmentManager.popBackStack()
    }
}
