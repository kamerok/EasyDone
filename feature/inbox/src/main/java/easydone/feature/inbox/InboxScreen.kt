package easydone.feature.inbox

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.systemBarsPadding
import easydone.coreui.design.AppTheme
import easydone.coreui.design.EasyDoneAppBar
import easydone.coreui.design.TaskCard


@Composable
internal fun InboxScreen(viewModel: InboxViewModel) {
    AppTheme {
        ProvideWindowInsets {
            FullscreenContent {
                Column {
                    EasyDoneAppBar { Text("Inbox") }
                    val state = viewModel.state.collectAsState().value
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.tasks) { task ->
                            TaskCard(
                                task = task,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.onTaskClick(task) }
                            )
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
