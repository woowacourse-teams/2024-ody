package com.mulberry.ody.presentation.common.binding

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
import com.mulberry.ody.OdyApplication
import kotlinx.coroutines.launch

abstract class BindingFragment<T : ViewDataBinding>(
    @LayoutRes private val layoutRes: Int,
) : Fragment() {
    private var _binding: T? = null
    protected val binding: T
        get() = requireNotNull(_binding)
    private var snackBar: Snackbar? = null
    val application by lazy { requireContext().applicationContext as OdyApplication }
    val analyticsHelper by lazy { application.analyticsHelper }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            val bundle = bundleOf(FirebaseAnalytics.Param.SCREEN_NAME to javaClass.simpleName)
            analyticsHelper.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }

    fun showSnackBar(
        @StringRes messageId: Int,
        action: Snackbar.() -> Unit = {},
    ) {
        snackBar?.dismiss()
        snackBar = Snackbar.make(binding.root, messageId, Snackbar.LENGTH_SHORT).apply { action() }
        snackBar?.show()
    }

    fun showSnackBar(
        message: String,
        action: Snackbar.() -> Unit = {},
    ) {
        snackBar?.dismiss()
        snackBar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).apply { action() }
        snackBar?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        snackBar = null
        viewLifecycleOwner.lifecycleScope.launch {
            analyticsHelper.logEvent(javaClass.simpleName + " destroyed", bundleOf())
        }
    }
}
