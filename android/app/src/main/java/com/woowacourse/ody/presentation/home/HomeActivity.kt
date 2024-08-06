package com.woowacourse.ody.presentation.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.ActivityHomeBinding
import com.woowacourse.ody.presentation.common.binding.BindingActivity
import com.woowacourse.ody.presentation.creation.MeetingCreationActivity
import com.woowacourse.ody.presentation.home.adapter.MeetingCatalogsAdapter
import com.woowacourse.ody.presentation.home.listener.HomeListener
import com.woowacourse.ody.presentation.invitecode.InviteCodeActivity
import com.woowacourse.ody.presentation.room.log.NotificationLogActivity
import timber.log.Timber

class HomeActivity :
    BindingActivity<ActivityHomeBinding>(
        R.layout.activity_home,
    ),
    HomeListener {
    private val viewModel by viewModels<HomeViewModel> {
        HomeViewModelFactory(
            application.meetingRepository,
        )
    }
    private val adapter by lazy {
        MeetingCatalogsAdapter(
            viewModel,
        )
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (isGranted.not()) {
                showSnackBar(R.string.intro_notification_permission_required)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        initializeObserve()
        requestNotificationPermission()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchMeetingCatalogs()
    }

    override fun initializeBinding() {
        binding.rvMeetingList.adapter = adapter
        binding.listener = this
    }

    private fun initializeObserve() {
        viewModel.meetingCatalogs.observe(this) {
            adapter.submitList(it)
        }
        viewModel.isMeetingCatalogsEmpty.observe(this) {
            binding.isCatalogsEmpty = it
        }
    }

    override fun onFab() {
        Timber.d(binding.fabHomeNavigator.isSelected.toString())
        binding.cvMenuView.visibility = if (binding.fabHomeNavigator.isSelected) View.GONE else View.VISIBLE
        binding.fabHomeNavigator.isSelected = !binding.fabHomeNavigator.isSelected
    }

    override fun onJoinMeeting() {
        startActivity(InviteCodeActivity.getIntent(this))
    }

    override fun onCreateMeeting() {
        startActivity(MeetingCreationActivity.getIntent(this))
    }

    override fun navigateToMeetingRoom(meetingId: Long) {
        startActivity(NotificationLogActivity.getIntent(this, meetingId))
    }

    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                showSnackBar(R.string.intro_notification_permission_guide)
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, HomeActivity::class.java)
    }
}
