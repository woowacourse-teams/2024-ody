package com.woowacourse.ody.presentation.common.binding

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.snackbar.Snackbar
import com.woowacourse.ody.OdyApplication

abstract class BindingActivity<T : ViewDataBinding>(
    @LayoutRes private val layoutRes: Int,
) : AppCompatActivity() {
    protected lateinit var binding: T
    private var snackBar: Snackbar? = null
    val application by lazy { applicationContext as OdyApplication }

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

    override fun onDestroy() {
        super.onDestroy()
        snackBar = null
    }
}
