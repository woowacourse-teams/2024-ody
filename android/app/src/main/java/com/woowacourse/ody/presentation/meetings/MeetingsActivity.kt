package com.woowacourse.ody.presentation.meetings

import android.Manifest
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.ActivityMeetingsBinding
import com.woowacourse.ody.presentation.common.binding.BindingActivity
import com.woowacourse.ody.presentation.creation.MeetingCreationActivity
import com.woowacourse.ody.presentation.invitecode.InviteCodeActivity
import com.woowacourse.ody.presentation.meetings.adapter.MeetingsAdapter
import com.woowacourse.ody.presentation.meetings.listener.MeetingsListener
import com.woowacourse.ody.presentation.room.etadashboard.EtaDashboardActivity
import com.woowacourse.ody.presentation.room.log.NotificationLogActivity

class MeetingsActivity :
    BindingActivity<ActivityMeetingsBinding>(
        R.layout.activity_meetings,
    ),
    MeetingsListener {
    private val viewModel by viewModels<MeetingsViewModel> {
        MeetingsViewModelFactory(
            application.meetingRepository,
        )
    }
    private val adapter by lazy {
        MeetingsAdapter(
            viewModel,
            this,
        )
    }

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
                is MeetingsNavigateAction.NavigateToEta -> navigateToEta(it.meetingId, it.inviteCode, it.title)
                is MeetingsNavigateAction.NavigateToNotificationLog -> navigateToMeetingRoom(it.meetingId)
            }
        }
    }

    override fun onFab() {
        binding.cvMenuView.visibility = if (binding.fabMeetingsNavigator.isSelected) View.GONE else View.VISIBLE
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

    fun navigateToMeetingRoom(meetingId: Long) {
        startActivity(NotificationLogActivity.getIntent(this, meetingId))
    }

    fun navigateToEta(
        meetingId: Long,
        inviteCode: String,
        title: String,
    ) {
        startActivity(EtaDashboardActivity.getIntent(this, meetingId, inviteCode, title))
    }

    override fun guideItemDisabled() {
        showSnackBar(R.string.meetings_entrance_unavailable_guide)
    }

    private fun closeNavigateMenu() {
        binding.cvMenuView.visibility = View.GONE
        binding.fabMeetingsNavigator.isSelected = false
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val permissions =
            arrayOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        val permissionRequestCode = PERMISSIONS_REQUEST_CODE

        ActivityCompat.requestPermissions(this, permissions, permissionRequestCode)
        showBackgroundLocationPermissionDialog(this)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showBackgroundLocationPermissionDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        val listener =
            DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                            ),
                            PERMISSION_REQUEST_CODE,
                        )
                }
            }
        builder.setTitle(getString(R.string.request_background_permission_dialog_title))
        builder.setPositiveButton(getString(R.string.request_background_permission_dialog_yes), listener)
        builder.setNegativeButton(getString(R.string.request_background_permission_dialog_no), null)
        builder.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            for (index in permissions.indices) {
                when (permissions[index]) {
                    Manifest.permission.POST_NOTIFICATIONS -> {
                        showPermissionGuide(grantResults, index)
                    }

                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    -> {
                        showPermissionGuide(grantResults, index)
                    }
                }
            }
        }
    }

    private fun showPermissionGuide(
        grantResults: IntArray,
        index: Int,
    ) {
        if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
            showSnackBar(R.string.meetings_notification_permission_guide)
        } else {
            showSnackBar(R.string.meetings_notification_permission_required)
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 1
        private const val PERMISSION_REQUEST_CODE = 2

        fun getIntent(context: Context): Intent = Intent(context, MeetingsActivity::class.java)
    }
}
