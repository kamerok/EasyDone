package easydone.feature.login

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import androidx.lifecycle.viewmodel.compose.viewModel
import easydone.service.trello.api.TrelloApi
import easydone.service.trello.api.model.Board
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun LoginRoute(
    apiKey: String,
    api: TrelloApi,
    onSuccessLogin: (String, List<Board>) -> Unit
) {
    val viewModel: LoginViewModel = viewModel {
        LoginViewModel(api, apiKey, onSuccessLogin)
    }
    val state by viewModel.state.collectAsStateWithLifecycle()

    ReceiveLoginToken(viewModel::onTokenReceived)

    val context = LocalContext.current
    LaunchedEffect(viewModel.events) {
        viewModel.events
            .onEach { event ->
                when (event) {
                    Event.StartTrelloLogin -> {
                        startBrowserLogin(context, apiKey)
                    }

                    is Event.LoginSuccess -> {
                        onSuccessLogin(event.token, event.boards)
                    }
                }
            }
            .launchIn(this)
    }

    LoginScreen(state = state)
}

@Composable
private fun ReceiveLoginToken(onTokenReceived: (String) -> Unit) {
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
}

@Composable
private fun LoginScreen(state: UiState) {
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
    LoginScreen(UiState.ErrorState(message = "Error message", onRetry = {}))
}

private fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

private fun startBrowserLogin(context: Context, apiKey: String) {
    val uri =
        Uri.parse("https://trello.com/1/authorize?expiration=never&name=EasyDone&scope=read,write&response_type=token&key=${apiKey}&callback_method=fragment&return_url=easydone://auth")
    try {
        CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
            .launchUrl(context, uri)
    } catch (e: ActivityNotFoundException) {
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}
