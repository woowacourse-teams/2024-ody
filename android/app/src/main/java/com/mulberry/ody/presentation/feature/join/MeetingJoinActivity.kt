package com.mulberry.ody.presentation.feature.join

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.presentation.feature.address.AddressSearchFragment
import com.mulberry.ody.presentation.feature.address.OnReceiveAddress
import com.mulberry.ody.presentation.feature.join.complete.JoinCompleteActivity
import com.mulberry.ody.presentation.feature.join.model.MeetingJoinNavigateAction
import com.mulberry.ody.presentation.feature.room.MeetingRoomActivity
import com.mulberry.ody.presentation.feature.room.MeetingRoomActivity.Companion.NAVIGATE_TO_DETAIL_MEETING
import com.mulberry.ody.presentation.theme.OdyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class MeetingJoinActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            enableEdgeToEdge()
            OdyTheme {
                MeetingJoinScreen(
                    onBack = ::finish,
                    showAddressSearch = ::showAddressSearchFragment,
                    inviteCode = intent.getStringExtra(INVITE_CODE_KEY) ?: "",
                    navigate = ::navigate,
                )
            }
        }
    }

    private fun navigate(navigateAction: MeetingJoinNavigateAction) {
        when (navigateAction) {
            is MeetingJoinNavigateAction.JoinNavigateToRoom -> {
                val intent = MeetingRoomActivity.getIntent(this, navigateAction.meetingId, NAVIGATE_TO_DETAIL_MEETING)
                startActivity(intent)
                finish()
            }

            MeetingJoinNavigateAction.JoinNavigateToJoinComplete -> {
                startActivity(JoinCompleteActivity.getIntent(this@MeetingJoinActivity))
            }
        }
    }

    private fun showAddressSearchFragment(onReceiveAddress: OnReceiveAddress) {
        supportFragmentManager.setFragmentResultListener(
            AddressSearchFragment.FRAGMENT_RESULT_KEY,
            this,
        ) { _, bundle ->
            val json = bundle.getString(AddressSearchFragment.ADDRESS_KEY) ?: return@setFragmentResultListener
            val address = Json.decodeFromString(Address.serializer(), json)
            onReceiveAddress(address)
        }

        val dialog = AddressSearchFragment()
        dialog.show(supportFragmentManager, ADDRESS_SEARCH_DIALOG_TAG)
    }

    companion object {
        private const val INVITE_CODE_KEY = "invite_code_key"
        private const val ADDRESS_SEARCH_DIALOG_TAG = "address_dialog"

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
