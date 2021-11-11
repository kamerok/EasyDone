package easydone.feature.inbox

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.systemBarsPadding
import easydone.coreui.design.AppTheme
import easydone.coreui.design.EasyDoneAppBar


@Composable
internal fun InboxScreen(viewModel: InboxViewModel) {
    AppTheme {
        ProvideWindowInsets {
            FullscreenContent {
                EasyDoneAppBar {
                    Text("Inbox")
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
