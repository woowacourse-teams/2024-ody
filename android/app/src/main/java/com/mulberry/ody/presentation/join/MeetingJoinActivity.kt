package com.mulberry.ody.presentation.join

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.mulberry.ody.R
import com.mulberry.ody.databinding.ActivityMeetingJoinBinding
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.presentation.address.AddressSearchFragment
import com.mulberry.ody.presentation.address.listener.AddressSearchListener
import com.mulberry.ody.presentation.common.PermissionHelper
import com.mulberry.ody.presentation.common.binding.BindingActivity
import com.mulberry.ody.presentation.common.listener.BackListener
import com.mulberry.ody.presentation.common.listener.NextListener
import com.mulberry.ody.presentation.join.complete.JoinCompleteActivity
import com.mulberry.ody.presentation.room.MeetingRoomActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

@AndroidEntryPoint
class MeetingJoinActivity :
    BindingActivity<ActivityMeetingJoinBinding>(R.layout.activity_meeting_join),
    NextListener,
    BackListener,
    AddressSearchListener {
    private val viewModel: MeetingJoinViewModel by viewModels<MeetingJoinViewModel>()
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBack()
            }
        }

    @Inject
    lateinit var permissionHelper: PermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeCurrentLocation()
        initializeBinding()
        initializeObserve()
    }

    override fun initializeBinding() {
        binding.vm = viewModel
        binding.nextListener = this
        binding.backListener = this
        binding.addressSearchListener = this
    }

    @SuppressLint("MissingPermission")
    private fun initializeCurrentLocation() {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)

        if (hasLocationPermissions() || isLocationEnabled()) {
            val currentLocationRequest =
                CurrentLocationRequest.Builder()
                    .setDurationMillis(30_000L)
                    .setMaxUpdateAgeMillis(60_000L)
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .build()

            lifecycleScope.launch {
                val location =
                    suspendCancellableCoroutine { continuation ->
                        fusedLocationProviderClient.getCurrentLocation(currentLocationRequest, null)
                            .addOnSuccessListener { location ->
                                continuation.resume(location) {
                                    Timber.d("${location.latitude} ${location.longitude}")
                                }
                            }.addOnFailureListener { exception ->
                                continuation.resumeWithException(exception)
                            }
                    }
                viewModel.getDefaultLocation(location.longitude.toString(), location.latitude.toString())
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun hasLocationPermissions(): Boolean {
        return permissionHelper.hasFineLocationPermission() &&
            permissionHelper.hasCoarseLocationPermission() &&
            permissionHelper.hasBackgroundLocationPermission()
    }

    private fun initializeObserve() {
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        viewModel.invalidDepartureEvent.observe(this) {
            showSnackBar(R.string.invalid_address)
        }
        viewModel.navigateAction.observe(this) {
            when (it) {
                is MeetingJoinNavigateAction.JoinNavigateToRoom -> {
                    navigateToNotificationRoom(it.meetingId)
                }

                MeetingJoinNavigateAction.JoinNavigateToJoinComplete -> {
                    startActivity(JoinCompleteActivity.getIntent(this))
                }
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

    private fun navigateToNotificationRoom(meetingId: Long) {
        val intent =
            MeetingRoomActivity.getIntent(
                this,
                meetingId,
                MeetingRoomActivity.NAVIGATE_TO_NOTIFICATION_LOG,
            )
        startActivity(intent)
        finish()
    }

    override fun onNext() {
        viewModel.joinMeeting(getInviteCode())
        viewModel.onClickMeetingJoin()
    }

    override fun onBack() = finish()

    private fun getInviteCode(): String = intent.getStringExtra(INVITE_CODE_KEY) ?: ""

    override fun onSearch() {
        supportFragmentManager.commit {
            add(R.id.fcv_join, AddressSearchFragment())
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }

    override fun onReceive(address: Address) {
        viewModel.departureAddress.value = address
    }

    companion object {
        private const val INVITE_CODE_KEY = "invite_code_key"

        fun getIntent(
            inviteCode: String,
            context: Context,
        ): Intent {
            return Intent(context, MeetingJoinActivity::class.java).apply {
                putExtra(INVITE_CODE_KEY, inviteCode)
            }
        }
    }
}
