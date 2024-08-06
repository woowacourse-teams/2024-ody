package com.woowacourse.ody.presentation

import android.view.View
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.ActivityMeetingListBinding
import com.woowacourse.ody.presentation.common.binding.BindingActivity
import com.woowacourse.ody.presentation.creation.MeetingCreationActivity
import com.woowacourse.ody.presentation.invitecode.InviteCodeActivity

class FloatingButtonActivity :
    BindingActivity<ActivityMeetingListBinding>(R.layout.activity_meeting_list),
    FloatingButtonListener {
    override fun initializeBinding() {
        binding.listener = this
    }

    override fun onFab() {
        binding.cvMenuView.visibility = if (binding.fabHomeNavigator.isSelected) View.GONE else View.VISIBLE
        binding.fabHomeNavigator.isSelected = !binding.fabHomeNavigator.isSelected
    }

    override fun onJoinMeeting() {
        startActivity(InviteCodeActivity.getIntent(this))
    }

    override fun onCreationMeeting() {
        startActivity(MeetingCreationActivity.getIntent(this))
    }
}
