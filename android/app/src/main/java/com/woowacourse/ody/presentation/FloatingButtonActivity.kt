package com.woowacourse.ody.presentation

import android.view.View
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.ActivityMeetingListBinding
import com.woowacourse.ody.presentation.common.binding.BindingActivity
import com.woowacourse.ody.presentation.creation.MeetingCreationActivity
import com.woowacourse.ody.presentation.join.MeetingJoinActivity

class FloatingButtonActivity :
    BindingActivity<ActivityMeetingListBinding>(R.layout.activity_meeting_list),
    FloatingButtonListener {
    override fun initializeBinding() {
        binding.listener = this
    }

    override fun onFab() {
        if (binding.fabHomeNavigator.isSelected) {
            binding.cvMenuView.visibility = View.GONE
        } else {
            binding.cvMenuView.visibility = View.VISIBLE
        }

        binding.fabHomeNavigator.isSelected = !binding.fabHomeNavigator.isSelected
    }

    override fun onJoinMeeting() {
        startActivity(MeetingJoinActivity.getIntent("", this))
    }

    override fun onCreationMeeting() {
        startActivity(MeetingCreationActivity.getIntent(this))
    }
}
