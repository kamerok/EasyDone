package easydone.feature.login

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.util.Consumer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import easydone.coreui.design.AppTheme
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
internal fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginClick: () -> Unit,
    onTokenReceived: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.events) {
        viewModel.events
            .onEach { event ->
                when (event) {
                    Event.StartTrelloLogin -> onLoginClick()
                }
            }
            .launchIn(this)
    }

    val context = LocalContext.current
    DisposableEffect(context) {
        val activity = context.getActivity()
        val listener = Consumer<Intent> { intent ->
            if (intent.data?.host == "auth") {
                val token = intent.data?.fragment?.substringAfter('=') ?: ""
                onTokenReceived(token)
            }
        }
        activity?.addOnNewIntentListener(listener)
        onDispose { activity?.removeOnNewIntentListener(listener) }
    }

    LoginScreen(state = state)
}

private fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

@Composable
private fun LoginScreen(state: UiState) {
    AppTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            when (state) {
                is UiState.IdleState -> IdleScreen(state.onLoginWithTrello)
                is UiState.LoadingState -> LoadingScreen()
                is UiState.ErrorState -> ErrorScreen(state)
            }
        }
    }
}

@Composable
private fun IdleScreen(onLoginClick: () -> Unit) {
    Button(
        onClick = onLoginClick,
        modifier = Modifier.wrapContentSize()
    ) {
        Text("Login with Trello")
    }
}

@Composable
private fun LoadingScreen() {
    CircularProgressIndicator()
}

@Composable
private fun ErrorScreen(state: UiState.ErrorState) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = state.message)
        Button(onClick = state.onRetry) {
            Text(text = "Retry")
        }
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun IdleScreenPreview() {
    LoginScreen(UiState.IdleState(onLoginWithTrello = {}))
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun LoadingScreenPreview() {
    LoginScreen(UiState.LoadingState)
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun ErrorScreenPreview() {
    LoginScreen(UiState.ErrorState("Error message", {}))
}