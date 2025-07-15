package com.mulberry.ody.presentation.feature.invitecode

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mulberry.ody.presentation.theme.OdyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InviteCodeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            enableEdgeToEdge()
            OdyTheme {
                InviteCodeScreen(
                    onClickBack = { finish() }
                )
            }
        }
    }

//    private fun initializeObserve() {
//        collectWhenStarted(viewModel.invalidCodeEvent) { errorMmessage ->
//            viewModel.clearInviteCode()
//            showSnackBar(errorMmessage)
//        }
//        collectWhenStarted(viewModel.navigateAction) {
//            navigateToJoinView()
//            finish()
//        }
//        collectWhenStarted(viewModel.networkErrorEvent) {
//            showRetrySnackBar {
//                viewModel.retryLastAction()
//            }
//        }
//        collectWhenStarted(viewModel.isLoading) { isLoading ->
//            if (isLoading) {
//                showLoadingDialog()
//                return@collectWhenStarted
//            }
//            hideLoadingDialog()
//        }
//    }
//
//    private fun navigateToJoinView() {
//        val inviteCode = viewModel.inviteCode.value
//        startActivity(MeetingJoinActivity.getIntent(inviteCode, this))
//    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, InviteCodeActivity::class.java)
    }
}
