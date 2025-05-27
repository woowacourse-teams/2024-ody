package com.mulberry.ody.presentation.feature.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mulberry.ody.R
import com.mulberry.ody.presentation.theme.Cream
import com.mulberry.ody.presentation.theme.OdyTheme
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val showSnackbar: (Int) -> Unit = { id ->
        coroutineScope.launch {
            snackbarHostState.showSnackbar(context.getString(id))
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = OdyTheme.colors.primary,
    ) { innerPadding ->
        LoginContent(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        )
    }
}

@Composable
private fun LoginContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(146.dp))
        Row(modifier = Modifier.padding(start = 46.dp)) {
            Column {
                Text(
                    text = stringResource(id = R.string.login_welcome_top),
                    style = OdyTheme.typography.pretendardBold24.copy(color = OdyTheme.colors.quinary),
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(id = R.string.login_welcome_prefix),
                        style = OdyTheme.typography.pretendardBold24.copy(color = OdyTheme.colors.quinary),
                    )
                    Text(
                        text = stringResource(id = R.string.login_welcome_ody),
                        style = OdyTheme.typography.pretendardBold24.copy(color = OdyTheme.colors.secondary),
                    )
                    Text(
                        text = stringResource(id = R.string.login_welcome_postfix),
                        style = OdyTheme.typography.pretendardBold24.copy(color = OdyTheme.colors.quinary),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_happy_ody),
                contentDescription = null,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.img_kakao_login),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .padding(horizontal = 20.dp),
        )
    }
}

@Composable
@Preview
private fun LoginContentPreview() {
    OdyTheme {
        LoginContent(modifier = Modifier.background(Cream))
    }
}
