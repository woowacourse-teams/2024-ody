package com.woowacourse.ody.presentation.invitecode

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.woowacourse.ody.data.remote.DefaultMeetingRepository
import com.woowacourse.ody.databinding.ActivityInviteCodeBinding

class InviteCodeActivity : AppCompatActivity() {
    private val binding: ActivityInviteCodeBinding by lazy {
        ActivityInviteCodeBinding.inflate(layoutInflater)
    }
    private val viewModel: InviteCodeViewModel by viewModels<InviteCodeViewModel> {
        InviteCodeViewModelFactory(DefaultMeetingRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.vm = viewModel
        binding.lifecycleOwner = this
    }
}
