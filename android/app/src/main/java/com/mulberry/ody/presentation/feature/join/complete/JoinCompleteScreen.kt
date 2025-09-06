package com.mulberry.ody.presentation.feature.join.complete

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulberry.ody.R
import com.mulberry.ody.presentation.theme.OdyTheme
import kotlinx.coroutines.delay

@Composable
fun JoinCompleteScreen(
    onTimeout: () -> Unit,
    timeoutMillis: Long,
) {
    LaunchedEffect(Unit) {
        delay(timeoutMillis)
        onTimeout()
    }

    JoinCompleteContent()
}

@Composable
private fun JoinCompleteContent(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(OdyTheme.colors.primary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_together_ody),
            contentDescription = null,
            tint = Color.Unspecified,
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(id = R.string.join_completion_information),
            style = OdyTheme.typography.pretendardBold28.copy(color = OdyTheme.colors.quinary),
        )
    }
}

@Composable
@Preview
private fun JoinCompleteContentPreview() {
    OdyTheme {
        JoinCompleteContent()
    }
}
