package com.woowacourse.ody.presentation.meetings

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.ActivityMeetingsBinding
import com.woowacourse.ody.presentation.common.PermissionHelper
import com.woowacourse.ody.presentation.common.analytics.logButtonClicked
import com.woowacourse.ody.presentation.common.binding.BindingActivity
import com.woowacourse.ody.presentation.creation.MeetingCreationActivity
import com.woowacourse.ody.presentation.invitecode.InviteCodeActivity
import com.woowacourse.ody.presentation.meetings.adapter.MeetingsAdapter
import com.woowacourse.ody.presentation.meetings.listener.MeetingsListener
import com.woowacourse.ody.presentation.room.etadashboard.EtaDashboardActivity
import com.woowacourse.ody.presentation.room.log.NotificationLogActivity

class MeetingsActivity :
    BindingActivity<ActivityMeetingsBinding>(R.layout.activity_meetings),
    MeetingsListener {
    private val viewModel by viewModels<MeetingsViewModel> {
        MeetingsViewModelFactory(
            application.firebaseAnalytics,
            application.meetingRepository,
        )
    }
    private val adapter by lazy {
        MeetingsAdapter(
            viewModel,
            this,
        )
    }
    private val firebaseAnalytics by lazy { application.firebaseAnalytics }
    private val permissionHelper: PermissionHelper by lazy { (application.permissionHelper) }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
    }

    private fun initializeObserve() {
        viewModel.meetingCatalogs.observe(this) {
            adapter.submitList(it)
        }
        viewModel.isMeetingCatalogsEmpty.observe(this) {
            binding.isCatalogsEmpty = it
        }
        viewModel.navigateAction.observe(this) {
            when (it) {
                is MeetingsNavigateAction.NavigateToEta ->
                    navigateToEta(
                        it.meetingId,
                        it.inviteCode,
                        it.title,
                    )

                is MeetingsNavigateAction.NavigateToNotificationLog -> navigateToMeetingRoom(it.meetingId)
            }
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

    override fun guideItemDisabled() {
        showSnackBar(R.string.meetings_entrance_unavailable_guide)
    }

    private fun closeNavigateMenu() {
        binding.cvMenuView.visibility = View.GONE
        binding.fabMeetingsNavigator.isSelected = false
    }

    private fun navigateToMeetingRoom(meetingId: Long) {
        startActivity(NotificationLogActivity.getIntent(this, meetingId))
    }

    private fun navigateToEta(
        meetingId: Long,
        inviteCode: String,
        title: String,
    ) {
        firebaseAnalytics.logButtonClicked(
            eventName = "eta_button_from_meetings",
            location = TAG,
        )
        startActivity(EtaDashboardActivity.getIntent(this, meetingId, inviteCode, title))
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermissions() {
        if (permissionHelper.hasNotificationPermission() &&
            permissionHelper.hasFineLocationPermission() &&
            permissionHelper.hasCoarseLocationPermission() &&
            permissionHelper.hasBackgroundLocationPermission()
        ) {
            return
        }

        permissionHelper.requestNotificationPermission(this)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
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
                ) { permissionHelper.requestCoarseAndFineLocationPermission(this) }
            }

            PermissionHelper.COARSE_AND_FINE_LOCATION_REQUEST_CODE ->
                checkPermissionAndProceed(
                    grantResults[0],
                    R.string.meetings_location_permission_required,
                ) { showBackgroundLocationPermissionDialog(this) }

            PermissionHelper.BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE ->
                checkPermissionAndProceed(
                    grantResults[0],
                    R.string.meetings_location_permission_required,
                )
        }
    }

    private fun checkPermissionAndProceed(
        grantResult: Int,
        requiredMessage: Int,
        onSuccess: () -> Unit = {},
    ) {
        if (grantResult != PackageManager.PERMISSION_GRANTED) {
            showSnackBar(requiredMessage)
        } else {
            onSuccess()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showBackgroundLocationPermissionDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        val listener =
            DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE ->
                        permissionHelper.requestBackgroundLocationPermission(this)
                }
            }
        builder.setTitle(getString(R.string.request_background_permission_dialog_title))
        builder.setPositiveButton(
            getString(R.string.request_background_permission_dialog_yes),
            listener,
        )
        builder.setNegativeButton(getString(R.string.request_background_permission_dialog_no), null)
        builder.show()
    }

    companion object {
        private const val TAG = "MeetingsActivity"

        fun getIntent(context: Context): Intent = Intent(context, MeetingsActivity::class.java)
    }
}
