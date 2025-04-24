package com.mulberry.ody.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.mulberry.ody.R
import com.mulberry.ody.presentation.theme.OdyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OdyTopAppBar(
    title: String = "",
    navigationIcon: (@Composable () -> Unit) = {},
    actions: (@Composable () -> Unit) = {},
) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = OdyTheme.colors.primary,
            ),
        title = {
            Text(
                text = title,
                style = OdyTheme.typography.pretendardBold28.copy(color = OdyTheme.colors.quinary),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = { navigationIcon() },
        actions = { actions() },
    )
}

@Composable
@Preview
private fun OdyTopAppBarPreview() {
    OdyTheme {
        OdyTopAppBar(
            title = "오디",
            navigationIcon = {
                IconButton(onClick = {}) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = null,
                    )
                }
            },
            actions = {
                IconButton(onClick = {}) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_setting),
                        contentDescription = null,
                    )
                }
            },
        )
    }
}
