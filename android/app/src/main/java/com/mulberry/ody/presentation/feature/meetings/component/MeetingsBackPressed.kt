package com.mulberry.ody.presentation.feature.meetings.component

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.mulberry.ody.R

@Composable
fun MeetingsBackPressed() {
    val context = LocalContext.current
    var backPressedTime by rememberSaveable { mutableLongStateOf(0L) }

    BackHandler {
        if (backPressedTime > System.currentTimeMillis() - 2000L) {
            (context as Activity).finish()
        } else {
            Toast.makeText(context, R.string.meetings_back_pressed_guide, Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}
