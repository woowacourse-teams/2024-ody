package com.woowacourse.ody.presentation.address

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.webkit.WebViewAssetLoader
import com.woowacourse.ody.databinding.DialogAddressSearchBinding

class AddressSearchDialog : DialogFragment() {
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
            addJavascriptInterface(AddressSearchInterface {}, JS_BRIDGE)
            webViewClient = LocalContentWebViewClient(assetLoader)
            loadUrl("https://${DOMAIN}/${PATH}/${FILE_NAME}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val DOMAIN = "com.woowacourse.ody"
        private const val JS_BRIDGE = "address_search"
        private const val FILE_NAME = "address.html"
        private const val PATH = "assets"
    }
}
