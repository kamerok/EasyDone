package easydone.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.systemBarsPadding
import com.kamer.home.R
import easydone.coreui.design.AppTheme
import easydone.coreui.design.EasyDoneAppBar


@Composable
internal fun HomeScreen() {
    AppTheme {
        ProvideWindowInsets {
            Column(Modifier.systemBarsPadding()) {
                EasyDoneAppBar(navigationIcon = null) {
                    Text(stringResource(R.string.app_name))
                }

            }
        }
    }
}
