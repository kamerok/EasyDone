package easydone.feature.inbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowWidthSizeClass.Companion.COMPACT
import easydone.core.domain.DomainRepository
import easydone.coreui.design.EasyDoneAppBar
import easydone.coreui.design.FoldPreviews
import easydone.coreui.design.TaskCard
import easydone.coreui.design.UiTask
import java.util.UUID


@Composable
fun InboxRoute(
    repository: DomainRepository,
    navigator: InboxNavigator
) {
    val viewModel: InboxViewModel = viewModel {
        InboxViewModel(repository, navigator)
    }
    val state by viewModel.state.collectAsState()
    InboxScreen(
        state = state,
        onTaskClick = navigator::openTask
    )
}

@Composable
internal fun InboxScreen(
    state: State,
    onTaskClick: (String) -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
            val columns by remember(windowSizeClass) {
                derivedStateOf {
                    if (windowSizeClass.windowWidthSizeClass == COMPACT) 1 else 2
                }
            }
            EasyDoneAppBar(modifier = Modifier.statusBarsPadding()) {
                Text("Inbox")
            }
            LazyVerticalStaggeredGrid(
                contentPadding = PaddingValues(16.dp),
                verticalItemSpacing = 16.dp,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                columns = StaggeredGridCells.Fixed(columns),
            ) {
                items(state.tasks) { task ->
                    TaskCard(
                        task = task,
                        onClick = { onTaskClick(task.id) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item(span = StaggeredGridItemSpan.FullLine) {
                    Spacer(
                        modifier = Modifier
                            .height(0.dp)
                            .navigationBarsPadding()
                    )
                }
            }
        }
    }
}


@FoldPreviews
@Composable
private fun InboxPreview() {
    InboxScreen(
        state = State((0..10).map {
            UiTask(UUID.randomUUID().toString(), "Task $it", true, true, true)
        }),
        onTaskClick = {}
    )
}
