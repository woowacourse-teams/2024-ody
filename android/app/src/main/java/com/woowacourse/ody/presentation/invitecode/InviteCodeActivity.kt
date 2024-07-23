package com.woowacourse.ody.presentation.invitecode

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.woowacourse.ody.databinding.ActivityInviteCodeBinding

class InviteCodeActivity : AppCompatActivity() {
    private val binding: ActivityInviteCodeBinding by lazy {
        ActivityInviteCodeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}
