package com.mulberry.ody.presentation.common.binding

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.mulberry.ody.R
import com.mulberry.ody.presentation.common.LoadingDialog
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.FirebaseAnalyticsHelper
import kotlinx.coroutines.launch

abstract class BindingFragment<T : ViewDataBinding>(
    @LayoutRes private val layoutRes: Int,
) : Fragment() {
    private var _binding: T? = null
    protected val binding: T
        get() = requireNotNull(_binding)
    private val analyticsHelper: AnalyticsHelper by lazy {
        FirebaseAnalyticsHelper(requireContext())
    }
    private var snackBar: Snackbar? = null
    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            val bundle = bundleOf(FirebaseAnalytics.Param.SCREEN_NAME to javaClass.simpleName)
            analyticsHelper.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    protected fun showSnackBar(
        @StringRes messageId: Int,
        action: Snackbar.() -> Unit = {},
    ) {
        snackBar?.dismiss()
        snackBar = Snackbar.make(binding.root, messageId, Snackbar.LENGTH_SHORT).apply { action() }
        snackBar?.show()
    }

    protected fun showSnackBar(
        message: String,
        action: Snackbar.() -> Unit = {},
    ) {
        snackBar?.dismiss()
        snackBar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).apply { action() }
        snackBar?.show()
    }

    protected fun showLoadingDialog() {
        dialog?.dismiss()
        dialog = LoadingDialog(requireContext())
        dialog?.show()
    }

    protected fun hideLoadingDialog() {
        dialog?.dismiss()
    }

    protected fun showRetrySnackBar(action: () -> Unit) {
        snackBar?.dismiss()
        snackBar =
            Snackbar.make(binding.root, R.string.network_error_guide, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry_button) { action() }
        snackBar?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        snackBar = null
        dialog = null
        lifecycleScope.launch {
            analyticsHelper.logEvent(javaClass.simpleName + " destroyed", bundleOf())
        }
    }
}
