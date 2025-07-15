package com.mulberry.ody.presentation.feature.invitecode

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mulberry.ody.R
import com.mulberry.ody.presentation.common.NoRippleInteractionSource
import com.mulberry.ody.presentation.component.OdyTextField
import com.mulberry.ody.presentation.component.OdyTopAppBar
import com.mulberry.ody.presentation.theme.OdyTheme
import com.mulberry.ody.presentation.theme.White
import kotlinx.coroutines.launch

@Composable
fun InviteCodeScreen(
    onClickBack: () -> Unit,
    viewModel: InviteCodeViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val showSnackbar: (Int) -> Unit = { id ->
        coroutineScope.launch {
            snackbarHostState.showSnackbar(context.getString(id))
        }
    }
    var inviteCode by rememberSaveable { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = OdyTheme.colors.primary,
        topBar = {
            OdyTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            tint = Color.Unspecified,
                            contentDescription = null,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        InviteCodeContent(
            inviteCode = inviteCode,
            onInviteCodeChange = { inviteCode = it },
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun InviteCodeContent(
    inviteCode: String,
    onInviteCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(112.dp))
        Row {
            Text(
                text = stringResource(id = R.string.invite_code_input_front),
                style = OdyTheme.typography.pretendardBold24.copy(color = OdyTheme.colors.secondary),
            )
            Text(
                text = stringResource(id = R.string.invite_code_input_back),
                style = OdyTheme.typography.pretendardBold24.copy(color = OdyTheme.colors.quinary),
            )
        }
        Spacer(modifier = Modifier.height(52.dp))
        OdyTextField(
            value = inviteCode,
            onValueChange = onInviteCodeChange,
            placeholder = stringResource(id = R.string.invite_code_input_placeholder),
            trailingIcon = {
                if (inviteCode.isNotBlank()) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_cancel),
                        tint = Color.Unspecified,
                        contentDescription = null,
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
        )
        Spacer(modifier = Modifier.height(32.dp))
        InviteCodeConfirmButton(enabled = inviteCode.isNotBlank())
    }
}

@Composable
private fun InviteCodeConfirmButton(
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (enabled) OdyTheme.colors.secondary else OdyTheme.colors.senary

    Button(
        onClick = { /*TODO*/ },
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(20.dp),
        enabled = enabled,
        contentPadding = PaddingValues(vertical = 14.dp, horizontal = 60.dp),
        interactionSource = NoRippleInteractionSource,
        modifier = modifier,
    ) {
        Text(
            text = stringResource(id = R.string.confirm_button),
            style = OdyTheme.typography.pretendardBold18.copy(color = White),
        )
    }
}

@Composable
@Preview(showSystemUi = true)
private fun InviteCodeContentPreview() {
    OdyTheme {
        InviteCodeContent(
            inviteCode = "",
            onInviteCodeChange = {},
        )
    }
}
