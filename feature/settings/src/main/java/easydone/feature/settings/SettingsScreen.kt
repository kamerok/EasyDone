package easydone.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.systemBarsPadding
import easydone.core.network.AuthInfoHolder
import easydone.coreui.design.AppTheme
import easydone.coreui.design.EasyDoneAppBar
import kotlinx.coroutines.launch


@Composable
internal fun SettingScreen(
    authInfoHolder: AuthInfoHolder,
    navigator: SettingsNavigator
) {
    AppTheme {
        ProvideWindowInsets {
            FullscreenContent {
                Column(modifier = Modifier.fillMaxSize()) {
                    EasyDoneAppBar { Text(stringResource(R.string.settings_title)) }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val scope = rememberCoroutineScope()
                        Button(
                            onClick = {
                                scope.launch {
                                    authInfoHolder.clear()
                                    navigator.navigateToSetup()
                                }
                            }
                        ) {
                            Text("Logout")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FullscreenContent(
    content: @Composable () -> Unit
) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier
            .fillMaxSize()
            //to draw under paddings
            .background(MaterialTheme.colors.background)
            .systemBarsPadding()
    ) {
        content()
    }
}