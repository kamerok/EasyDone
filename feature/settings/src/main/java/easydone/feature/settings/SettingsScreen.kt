package easydone.feature.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import easydone.core.domain.RemoteDataSource
import easydone.core.strings.R
import easydone.coreui.design.EasyDoneAppBar
import kotlinx.coroutines.launch


@Composable
fun SettingScreen(
    remoteDataSource: RemoteDataSource,
    navigator: SettingsNavigator
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            EasyDoneAppBar { Text(stringResource(R.string.settings_title)) }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                val scope = rememberCoroutineScope()
                Button(
                    onClick = {
                        scope.launch {
                            remoteDataSource.disconnect()
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

