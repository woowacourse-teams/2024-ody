package com.woowacourse.ody.presentation.meetinglist

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.ActivityMeetingListBinding
import com.woowacourse.ody.presentation.common.binding.BindingActivity
import com.woowacourse.ody.presentation.meetinglist.adapter.MeetingListAdapter

class MeetingListActivity :
    BindingActivity<ActivityMeetingListBinding>(
        R.layout.activity_meeting_list,
    ) {
    private val viewModel by viewModels<MeetingListViewModel>()
    private val adapter by lazy {
        MeetingListAdapter(
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
        binding.rvMeetingList.layoutManager = LinearLayoutManager(this)
    }

    private fun initializeObserve() {
        viewModel.meetingList.observe(this) {
            adapter.submitList(it)
        }
    }
}
