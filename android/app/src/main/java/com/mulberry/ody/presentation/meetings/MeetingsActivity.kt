package com.mulberry.ody.presentation.meetings

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.mulberry.ody.R
import com.mulberry.ody.databinding.ActivityMeetingsBinding
import com.mulberry.ody.presentation.common.PermissionHelper
import com.mulberry.ody.presentation.common.analytics.logButtonClicked
import com.mulberry.ody.presentation.common.binding.BindingActivity
import com.mulberry.ody.presentation.creation.MeetingCreationActivity
import com.mulberry.ody.presentation.invitecode.InviteCodeActivity
import com.mulberry.ody.presentation.login.LoginActivity
import com.mulberry.ody.presentation.meetings.adapter.MeetingsAdapter
import com.mulberry.ody.presentation.meetings.listener.MeetingsListener
import com.mulberry.ody.presentation.room.MeetingRoomActivity
import com.mulberry.ody.presentation.setting.SettingActivity
import kotlinx.coroutines.launch

class MeetingsActivity :
    BindingActivity<ActivityMeetingsBinding>(
        R.layout.activity_meetings,
    ),
    MeetingsListener {
    private val viewModel by viewModels<MeetingsViewModel> {
        MeetingsViewModelFactory(
            analyticsHelper,
            application.meetingRepository,
        )
    }
    private val adapter by lazy {
        MeetingsAdapter(
            viewModel,
            this,
        )
    }
    private val permissionHelper: PermissionHelper by lazy { (application.permissionHelper) }
    private var lastBackPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeObserve()
        initializeBinding()
        requestPermissions()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchMeetingCatalogs()
    }

    override fun initializeBinding() {
        binding.rvMeetingList.adapter = adapter
        binding.listener = this
        binding.lifecycleOwner = this
        binding.vm = viewModel
    }

    private fun initializeObserve() {
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback())
        viewModel.meetingCatalogs.observe(this) {
            adapter.submitList(it)
        }
        viewModel.navigateAction.observe(this) {
            when (it) {
                is MeetingsNavigateAction.NavigateToEtaDashboard -> navigateToEtaDashboard(it.meetingId)
                is MeetingsNavigateAction.NavigateToNotificationLog -> navigateToNotificationLog(it.meetingId)
                is MeetingsNavigateAction.NavigateToLogin -> navigateToLogin()
            }
        }
        viewModel.networkErrorEvent.observe(this) {
            showRetrySnackBar { viewModel.retryLastAction() }
        }
        viewModel.errorEvent.observe(this) {
            showSnackBar(R.string.error_guide)
        }
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                showLoadingDialog()
                return@observe
            }
            hideLoadingDialog()
        }
    }

    override fun onFab() {
        binding.cvMenuView.visibility =
            if (binding.fabMeetingsNavigator.isSelected) View.GONE else View.VISIBLE
        binding.fabMeetingsNavigator.isSelected = !binding.fabMeetingsNavigator.isSelected
    }

    override fun onJoinMeeting() {
        startActivity(InviteCodeActivity.getIntent(this))
        closeNavigateMenu()
    }

    override fun onCreateMeeting() {
        startActivity(MeetingCreationActivity.getIntent(this))
        closeNavigateMenu()
    }

    private fun navigateToNotificationLog(meetingId: Long) {
        val intent =
            MeetingRoomActivity.getIntent(
                this,
                meetingId,
                MeetingRoomActivity.NAVIGATE_TO_NOTIFICATION_LOG,
            )
        startActivity(intent)
    }

    private fun navigateToEtaDashboard(meetingId: Long) {
        lifecycleScope.launch {
            analyticsHelper.logButtonClicked(
                eventName = "eta_button_from_meetings",
                location = TAG,
            )
        }
        val intent =
            MeetingRoomActivity.getIntent(
                this,
                meetingId,
                MeetingRoomActivity.NAVIGATE_TO_ETA_DASHBOARD,
            )
        startActivity(intent)
    }

    private fun navigateToLogin() {
        val intent = LoginActivity.getIntent(this)
        startActivity(intent)
    }

    override fun guideItemDisabled() {
        showSnackBar(R.string.meetings_entrance_unavailable_guide)
    }

    override fun onClickSetting() {
        startActivity(SettingActivity.getIntent(this))
    }

    private fun closeNavigateMenu() {
        binding.cvMenuView.visibility = View.GONE
        binding.fabMeetingsNavigator.isSelected = false
    }

    private fun requestPermissions() {
        if (permissionHelper.hasNotificationPermission() &&
            permissionHelper.hasFineLocationPermission() &&
            permissionHelper.hasCoarseLocationPermission() &&
            permissionHelper.hasBackgroundLocationPermission()
        ) {
            return
        }

        permissionHelper.requestCoarseAndFineLocationPermission(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isEmpty()) return

        when (requestCode) {
            PermissionHelper.NOTIFICATION_REQUEST_CODE -> {
                checkPermissionAndProceed(
                    grantResults[0],
                    R.string.meetings_notification_permission_required,
                )
            }

            PermissionHelper.COARSE_AND_FINE_LOCATION_REQUEST_CODE ->
                checkPermissionAndProceed(
                    grantResults[0],
                    R.string.meetings_location_permission_required,
                ) { permissionHelper.requestBackgroundLocationPermission(this) }

            PermissionHelper.BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE ->
                checkPermissionAndProceed(
                    grantResults[0],
                    R.string.meetings_location_permission_required,
                ) { permissionHelper.requestNotificationPermission(this) }
        }
    }

    private fun onBackPressedCallback(): OnBackPressedCallback {
        return object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (lastBackPressedTime > System.currentTimeMillis() - BACK_PRESSED_DELAY) {
                    finish()
                } else {
                    Toast.makeText(
                        this@MeetingsActivity,
                        R.string.meetings_back_pressed_guide,
                        Toast.LENGTH_SHORT,
                    ).show()
                    lastBackPressedTime = System.currentTimeMillis()
                }
            }
        }
    }

    private fun checkPermissionAndProceed(
        grantResult: Int,
        requiredMessage: Int,
        requestNextPermission: () -> Unit = {},
    ) {
        if (grantResult != PackageManager.PERMISSION_GRANTED) {
            showSnackBar(requiredMessage)
        }
        requestNextPermission()
    }

    companion object {
        private const val TAG = "MeetingsActivity"
        private const val BACK_PRESSED_DELAY = 2000

        fun getIntent(context: Context): Intent = Intent(context, MeetingsActivity::class.java)
    }
}
