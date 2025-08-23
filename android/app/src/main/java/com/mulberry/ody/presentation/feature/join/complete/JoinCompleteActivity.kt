package com.mulberry.ody.presentation.feature.join.complete

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mulberry.ody.presentation.theme.OdyTheme

class JoinCompleteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            enableEdgeToEdge()
            OdyTheme {
                JoinCompleteScreen(
                    onTimeout = ::finish,
                    timeoutMillis = TIMEOUT_MILLIS,
                )
            }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 1500L

        fun getIntent(context: Context): Intent = Intent(context, JoinCompleteActivity::class.java)
    }
}
