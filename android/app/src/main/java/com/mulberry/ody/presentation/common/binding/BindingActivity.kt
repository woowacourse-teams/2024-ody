package com.mulberry.ody.presentation.common.binding

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.snackbar.Snackbar
import com.mulberry.ody.R

abstract class BindingActivity<T : ViewDataBinding>(
    @LayoutRes private val layoutRes: Int,
) : AppCompatActivity() {
    protected lateinit var binding: T
    private var snackBar: Snackbar? = null
    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutRes)
        binding.lifecycleOwner = this
        initializeBinding()
    }

    protected abstract fun initializeBinding()

    protected fun showSnackBar(
        @StringRes messageId: Int,
        @StringRes actionMessageId: Int? = null,
        action: () -> Unit = {},
    ) {
        snackBar?.dismiss()
        snackBar = Snackbar.make(binding.root, messageId, Snackbar.LENGTH_SHORT)
        actionMessageId?.let {
            snackBar?.setAction(actionMessageId) { action() }
        }
        snackBar?.show()
    }

    protected fun showSnackBar(
        message: String,
        @StringRes actionMessageId: Int? = null,
        action: () -> Unit = {},
    ) {
        snackBar?.dismiss()
        snackBar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
        actionMessageId?.let {
            snackBar?.setAction(actionMessageId) { action() }
        }
        snackBar?.show()
    }

    protected fun showRetrySnackBar(action: () -> Unit) {
        snackBar?.dismiss()
        snackBar =
            Snackbar.make(binding.root, R.string.network_error_guide, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry_button) { action() }
        snackBar?.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        snackBar = null
        dialog = null
    }
}
