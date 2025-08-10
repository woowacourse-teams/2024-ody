package com.mulberry.ody.presentation.feature.creation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.presentation.feature.address.AddressSearchFragment
import com.mulberry.ody.presentation.feature.creation.model.MeetingCreationNavigateAction
import com.mulberry.ody.presentation.feature.join.MeetingJoinActivity
import com.mulberry.ody.presentation.theme.OdyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class MeetingCreationActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            enableEdgeToEdge()
            OdyTheme {
                MeetingCreationScreen(
                    showAddressSearch = ::showAddressSearchFragment,
                    onBack = ::finish,
                    navigate = ::navigate,
                )
            }
        }
    }

    private fun navigate(navigateAction: MeetingCreationNavigateAction) {
        when (navigateAction) {
            is MeetingCreationNavigateAction.NavigateToMeetingJoin -> {
                val intent = MeetingJoinActivity.getIntent(navigateAction.inviteCode, this@MeetingCreationActivity)
                startActivity(intent)
                finish()
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
        private const val ADDRESS_SEARCH_DIALOG_TAG = "address_dialog"

        fun getIntent(context: Context): Intent = Intent(context, MeetingCreationActivity::class.java)
    }
}

typealias OnReceiveAddress = (Address) -> Unit
