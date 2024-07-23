package com.woowacourse.ody.presentation.notificationlog

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.woowacourse.ody.data.remote.DefaultNotificationLogRepository
import com.woowacourse.ody.databinding.ActivityNotificationLogBinding

class NotificationLogActivity : AppCompatActivity() {
    private val viewModel: NotificationLogViewModel by viewModels {
        NotificationLogViewModelFactory(
            DefaultNotificationLogRepository,
        )
    }

    private val binding: ActivityNotificationLogBinding by lazy {
        ActivityNotificationLogBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        setContentView(binding.root)
    }

    private fun initializeBinding() {
        binding.vm = viewModel
    }
}
