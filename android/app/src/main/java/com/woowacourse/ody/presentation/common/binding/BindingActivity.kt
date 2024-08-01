package com.woowacourse.ody.presentation.common.binding

import android.os.Bundle
import androidx.annotation.LayoutRes
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
    private val application by lazy { applicationContext as OdyApplication }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutRes)
        binding.lifecycleOwner = this
    }

    override fun onDestroy() {
        super.onDestroy()
        snackBar = null
    }
}
