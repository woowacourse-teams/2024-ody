package com.woowacourse.ody.presentation.meetinglist

import android.os.Bundle
import androidx.activity.viewModels
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.ActivityMeetingListBinding
import com.woowacourse.ody.presentation.common.binding.BindingActivity
import com.woowacourse.ody.presentation.meetinglist.adapter.MeetingsAdapter

class MeetingsActivity :
    BindingActivity<ActivityMeetingListBinding>(
        R.layout.activity_meeting_list,
    ) {
    private val viewModel by viewModels<MeetingsViewModel> {
        MeetingsViewModelFactory(
            application.meetingRepository,
        )
    }
    private val adapter by lazy {
        MeetingsAdapter(
            viewModel,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        initializeObserve()
    }

    override fun initializeBinding() {
        binding.rvMeetingList.adapter = adapter
    }

    private fun initializeObserve() {
        viewModel.meetingCatalogs.observe(this) {
            adapter.submitList(it)
        }
    }
}
