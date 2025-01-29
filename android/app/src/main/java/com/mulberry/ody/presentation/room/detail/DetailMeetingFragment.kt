package com.mulberry.ody.presentation.room.detail

import android.os.Bundle
import android.view.View
import com.mulberry.ody.R
import com.mulberry.ody.databinding.FragmentNotificationLogBinding
import com.mulberry.ody.presentation.common.binding.BindingFragment

class DetailMeetingFragment :
    BindingFragment<FragmentNotificationLogBinding>(R.layout.fragment_detail_meeting) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializeBinding()
        initializeObserve()
    }

    fun initializeBinding() {
    }

    private fun initializeObserve() {
    }
}
