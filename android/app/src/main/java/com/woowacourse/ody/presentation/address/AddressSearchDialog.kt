package com.woowacourse.ody.presentation.address

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.webkit.WebViewAssetLoader
import com.woowacourse.ody.OdyApplication
import com.woowacourse.ody.databinding.DialogAddressSearchBinding
import com.woowacourse.ody.presentation.address.listener.AddressReceiveListener
import com.woowacourse.ody.presentation.address.model.toGeoLocationUiModel
import com.woowacourse.ody.presentation.address.web.AddressSearchInterface
import com.woowacourse.ody.presentation.address.web.LocalContentWebViewClient

class AddressSearchDialog : DialogFragment(), AddressReceiveListener {
    private var _binding: DialogAddressSearchBinding? = null
    private val binding get() = _binding!!
    private val application: OdyApplication by lazy {
        requireContext().applicationContext as OdyApplication
    }

    private val viewModel: AddressSearchViewModel by viewModels {
        AddressSearchViewModelFactory(application.kakaoGeoLocationRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogAddressSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
        initializeObservingData()
        showAddressSearchWebView()
    }

    private fun initializeView() {
        binding.bgAddressSearch.setOnClickListener { dismiss() }
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    private fun initializeObservingData() {
        viewModel.geoLocation.observe(viewLifecycleOwner) {
            val geoLocationUiModel = it.toGeoLocationUiModel()
            setFragmentResult(REQUEST_KEY, bundleOf(GEO_LOCATION_UI_MODEL_KEY to geoLocationUiModel))
            dismiss()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun showAddressSearchWebView() {
        val assetLoader =
            WebViewAssetLoader.Builder()
                .addPathHandler("/$PATH/", WebViewAssetLoader.AssetsPathHandler(requireContext()))
                .setDomain(DOMAIN)
                .build()

        with(binding.wvAddressSearch) {
            settings.javaScriptEnabled = true
            addJavascriptInterface(AddressSearchInterface(this@AddressSearchDialog), JS_BRIDGE)
            webViewClient = LocalContentWebViewClient(assetLoader)
            loadUrl("https://$DOMAIN/$PATH/${FILE_NAME}")
        }
    }

    override fun onReceive(address: String) {
        viewModel.fetchGeoLocation(address)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_KEY = "address_search_request_key"
        const val GEO_LOCATION_UI_MODEL_KEY = "geo_location_ui_model_key"

        private const val DOMAIN = "com.woowacourse.ody"
        private const val JS_BRIDGE = "address_search"
        private const val FILE_NAME = "address.html"
        private const val PATH = "assets"
    }
}
