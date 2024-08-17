package com.woowacourse.ody.presentation.common.binding

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.snackbar.Snackbar
import com.woowacourse.ody.OdyApplication
import com.woowacourse.ody.R

abstract class BindingActivity<T : ViewDataBinding>(
    @LayoutRes private val layoutRes: Int,
) : AppCompatActivity() {
    protected lateinit var binding: T
    private var snackBar: Snackbar? = null
    val application by lazy { applicationContext as OdyApplication }
    val analyticsHelper by lazy { application.analyticsHelper }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutRes)
        binding.lifecycleOwner = this
        initializeBinding()
    }

    protected abstract fun initializeBinding()

    protected fun showSnackBar(
        @StringRes messageId: Int,
        action: Snackbar.() -> Unit = {},
    ) {
        snackBar?.dismiss()
        snackBar = Snackbar.make(binding.root, messageId, Snackbar.LENGTH_SHORT).apply { action() }
        snackBar?.show()
    }

    protected fun showRetrySnackBar(action: () -> Unit) {
        snackBar?.dismiss()
        snackBar =
            Snackbar.make(binding.root, R.string.network_error_guide, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry_button) { action() }
        snackBar?.show()
    }

    protected fun showToast(@StringRes messageId: Int) {
        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        snackBar = null
    }
}
