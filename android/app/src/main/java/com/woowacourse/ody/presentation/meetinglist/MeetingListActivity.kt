package com.woowacourse.ody.presentation.meetinglist

import android.os.Bundle
import androidx.activity.viewModels
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.ActivityMeetingListBinding
import com.woowacourse.ody.presentation.common.binding.BindingActivity
import com.woowacourse.ody.presentation.meetinglist.adapter.MeetingListAdapter
import com.woowacourse.ody.presentation.meetinglist.listener.MeetingItemListener

class MeetingListActivity :
    BindingActivity<ActivityMeetingListBinding>(R.layout.activity_meeting_list),
    MeetingItemListener {
    private val viewModel by viewModels<MeetingListViewModel>()
    private val adapter by lazy {
        MeetingListAdapter(
            this,
            viewModel,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeObserve()
        initializeBinding()
    }

    override fun initializeBinding() {
        binding.rvMeetingList.adapter = adapter
    }

    private fun initializeObserve() {
        viewModel.meetingList.observe(this) {
            adapter.submitList(it)
        }
    }

    override fun navigateToMeetingRoom() {
        TODO("Not yet implemented")
    }
}
