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
import androidx.webkit.WebViewAssetLoader
import com.woowacourse.ody.databinding.DialogAddressSearchBinding

class AddressSearchDialog : DialogFragment(), AddressListener {
    private var _binding: DialogAddressSearchBinding? = null
    private val binding get() = _binding!!

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
    }

    private fun initializeView() {
        binding.bgAddressSearch.setOnClickListener { dismiss() }
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        showAddressSearchWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun showAddressSearchWebView() {
        val assetLoader =
            WebViewAssetLoader.Builder()
                .addPathHandler("/${PATH}/", WebViewAssetLoader.AssetsPathHandler(requireContext()))
                .setDomain(DOMAIN)
                .build()

        with(binding.wvAddressSearch) {
            settings.javaScriptEnabled = true
            addJavascriptInterface(AddressSearchInterface(this@AddressSearchDialog), JS_BRIDGE)
            webViewClient = LocalContentWebViewClient(assetLoader)
            loadUrl("https://${DOMAIN}/${PATH}/${FILE_NAME}")
        }
    }

    override fun onReceive(address: String) {
        setFragmentResult(REQUEST_KEY, bundleOf(ADDRESS_KEY to address))
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_KEY = "address_search_request_key"
        const val ADDRESS_KEY = "address_key"

        private const val DOMAIN = "com.woowacourse.ody"
        private const val JS_BRIDGE = "address_search"
        private const val FILE_NAME = "address.html"
        private const val PATH = "assets"
    }
}
