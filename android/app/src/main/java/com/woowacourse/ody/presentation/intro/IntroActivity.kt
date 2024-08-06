package com.woowacourse.ody.presentation.intro

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.ActivityIntroBinding
import com.woowacourse.ody.presentation.common.binding.BindingActivity
import com.woowacourse.ody.presentation.creation.MeetingCreationActivity
import com.woowacourse.ody.presentation.invitecode.InviteCodeActivity

class IntroActivity : BindingActivity<ActivityIntroBinding>(R.layout.activity_intro) {
    private val viewModel: IntroViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeObserve()
        initializeBinding()
        requestPermissions()
    }

    override fun initializeBinding() {
        binding.listener = viewModel
    }

    private fun initializeObserve() {
        viewModel.navigateAction.observe(this) { navigateAction ->
            when (navigateAction) {
                is IntroNavigateAction.NavigateToMeetingInfo ->
                    navigateToMeetingInfoActivity()

                is IntroNavigateAction.NavigateToInviteCode ->
                    navigateToInviteCodeActivity()
            }
        }
    }

    private fun navigateToMeetingInfoActivity() {
        startActivity(MeetingCreationActivity.getIntent(this))
    }

    private fun navigateToInviteCodeActivity() {
        startActivity(InviteCodeActivity.getIntent(this))
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

        permissionBackgroundLocation(this)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun permissionBackgroundLocation(context: Context) {
        val builder = AlertDialog.Builder(context)
        val listener =
            DialogInterface.OnClickListener { _, p1 ->
                when (p1) {
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
        builder.setTitle("백그라운드 위치 권한을 위해 항상 허용으로 설정해주세요.")
        builder.setPositiveButton("네", listener)
        builder.setNegativeButton("아니오", null)
        builder.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            for (i in permissions.indices) {
                when (permissions[i]) {
                    Manifest.permission.POST_NOTIFICATIONS -> {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            showSnackBar(R.string.intro_notification_permission_guide)
                        } else {
                            showSnackBar(R.string.intro_notification_permission_required)
                        }
                    }

                    Manifest.permission.ACCESS_COARSE_LOCATION -> {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            showSnackBar(R.string.intro_location_permission_guide)
                        } else {
                            showSnackBar(R.string.intro_location_permission_required)
                        }
                    }

                    Manifest.permission.ACCESS_FINE_LOCATION -> {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            showSnackBar(R.string.intro_location_permission_guide)
                        } else {
                            showSnackBar(R.string.intro_location_permission_required)
                        }
                    }

                    Manifest.permission.ACCESS_BACKGROUND_LOCATION -> {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            showSnackBar(R.string.intro_location_permission_guide)
                        } else {
                            showSnackBar(R.string.intro_location_permission_required)
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 1
        private const val PERMISSION_REQUEST_CODE = 2

        fun getIntent(context: Context): Intent = Intent(context, IntroActivity::class.java)
    }
}
