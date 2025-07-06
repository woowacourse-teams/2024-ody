package com.mulberry.ody.presentation.component

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulberry.ody.R
import com.mulberry.ody.presentation.common.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BaseActionHandler(
    viewModel: BaseViewModel,
    snackbarHostState: SnackbarHostState,
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    fun showSnackbar(messageId: Int) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(context.getString(messageId))
        }
    }

    fun showActionSnackbar(
        messageId: Int,
        actionLabelId: Int,
        action: () -> Unit,
    ) {
        coroutineScope.launch {
            val result =
                snackbarHostState.showSnackbar(
                    message = context.getString(messageId),
                    actionLabel = context.getString(actionLabelId),
                )
            if (result == SnackbarResult.ActionPerformed) {
                action()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.networkErrorEvent.collect {
            showActionSnackbar(
                messageId = R.string.network_error_guide,
                actionLabelId = R.string.retry_button,
                action = { viewModel.retryLastAction() },
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.errorEvent.collect {
            showSnackbar(R.string.error_guide)
        }
    }

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    if (isLoading) {
        OdyLoading(modifier = Modifier.fillMaxSize())
    }
}
