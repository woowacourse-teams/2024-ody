package com.woowacourse.ody.presentation.common.binding

import android.content.Context
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
import com.woowacourse.ody.OdyApplication
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycleScope.launch {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
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

    fun showSnackBar(
        @StringRes messageId: Int,
        action: Snackbar.() -> Unit = {},
    ) {
        snackBar?.dismiss()
        snackBar = Snackbar.make(binding.root, messageId, Snackbar.LENGTH_SHORT).apply { action() }
        snackBar?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        snackBar = null
        lifecycleScope.launch {
            analyticsHelper.logEvent(javaClass.simpleName + " destroyed", bundleOf())
        }
    }
}
