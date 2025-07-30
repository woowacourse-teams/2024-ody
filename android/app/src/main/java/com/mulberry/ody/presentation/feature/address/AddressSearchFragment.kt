package com.mulberry.ody.presentation.feature.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.presentation.theme.OdyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class AddressSearchFragment : DialogFragment() {
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
                        onClickBack = { dismiss() },
                        onClickAddress = ::sendAddress,
                    )
                }
            }
        }
    }

    private fun sendAddress(address: Address) {
        val json = Json.encodeToString(Address.serializer(), address)
        parentFragmentManager.setFragmentResult(
            FRAGMENT_RESULT_KEY,
            bundleOf(ADDRESS_KEY to json),
        )
        dismiss()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    companion object {
        const val FRAGMENT_RESULT_KEY = "address_result_key"
        const val ADDRESS_KEY = "selected_address_json"
    }
}
