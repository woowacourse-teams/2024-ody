package com.woowacourse.ody.presentation.notificationlog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.woowacourse.ody.data.local.fake.FakeMeetingRepository
import com.woowacourse.ody.data.local.fake.FakeNotificationLogRepository
import com.woowacourse.ody.databinding.ActivityNotificationLogBinding
import com.woowacourse.ody.presentation.notificationlog.adapter.NotificationLogListAdapter

class NotificationLogActivity : AppCompatActivity() {
    private val viewModel: NotificationLogViewModel by viewModels {
        NotificationLogViewModelFactory(
            // DefaultNotificationLogRepository,
            FakeNotificationLogRepository,
            FakeMeetingRepository,
        )
    }

    private val binding: ActivityNotificationLogBinding by lazy {
        ActivityNotificationLogBinding.inflate(layoutInflater)
    }

    private val adapter: NotificationLogListAdapter by lazy {
        NotificationLogListAdapter(
            viewModel,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        initializeObserve()
        viewModel.initialize()
    }

    private fun initializeBinding() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
        binding.rvNotificationLog.adapter = adapter
        binding.rvNotificationLog.layoutManager = LinearLayoutManager(this)
        setContentView(binding.root)
    }

    private fun initializeObserve() {
        viewModel.notificationLogs.observe(this) {
            adapter.submitList(it)
        }
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, NotificationLogActivity::class.java)
    }
}
