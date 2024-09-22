package com.mulberry.ody.presentation.address

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.webkit.WebViewAssetLoader
import com.google.android.material.snackbar.Snackbar
import com.mulberry.ody.R
import com.mulberry.ody.databinding.DialogAddressSearchBinding
import com.mulberry.ody.presentation.address.listener.AddressReceiveListener
import com.mulberry.ody.presentation.address.listener.AddressSearchListener
import com.mulberry.ody.presentation.address.web.AddressSearchInterface
import com.mulberry.ody.presentation.address.web.LocalContentWebViewClient
import com.mulberry.ody.presentation.common.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressSearchDialog : DialogFragment(), AddressReceiveListener {
    private var _binding: DialogAddressSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddressSearchViewModel by viewModels<AddressSearchViewModel>()
    private val loadingDialog: Dialog by lazy { LoadingDialog(requireContext()) }

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
        initializeObserve()
        showAddressSearchWebView()
    }

    private fun initializeView() {
        binding.bgAddressSearch.setOnClickListener { dismiss() }
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    private fun initializeObserve() {
        viewModel.geoLocation.observe(viewLifecycleOwner) {
            (parentFragment as? AddressSearchListener)?.onReceive(it)
            (activity as? AddressSearchListener)?.onReceive(it)
            dismiss()
        }
        viewModel.networkErrorEvent.observe(viewLifecycleOwner) {
            showRetrySnackBar()
        }
        viewModel.errorEvent.observe(viewLifecycleOwner) {
            showErrorSnackBar()
        }
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                loadingDialog.show()
                return@observe
            }
            loadingDialog.dismiss()
        }
    }

    private fun showRetrySnackBar() {
        Snackbar.make(requireView(), R.string.network_error_guide, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry_button) {
                viewModel.retryLastAction()
            }.show()
    }

    private fun showErrorSnackBar() = Snackbar.make(binding.root, R.string.error_guide, Snackbar.LENGTH_SHORT).show()

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
        private const val DOMAIN = "com.mulberry.ody"
        private const val JS_BRIDGE = "address_search"
        private const val FILE_NAME = "address.html"
        private const val PATH = "assets"
    }
}
