package easydone.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.systemBarsPadding
import com.kamer.home.R
import easydone.coreui.design.AppTheme
import easydone.coreui.design.EasyDoneAppBar
import easydone.coreui.design.TaskCard
import easydone.coreui.design.UiTask
import java.time.LocalDate
import java.time.Period


@Composable
internal fun HomeScreen(viewModel: HomeViewModel) {
    AppTheme {
        ProvideWindowInsets {
            FullscreenContent {
                Box {
                    Column {
                        EasyDoneAppBar(
                            navigationIcon = null,
                            title = { Text(stringResource(R.string.app_name)) },
                            menu = {
                                DropdownMenuItem(onClick = viewModel::onSettings) {
                                    Text(text = "Settings")
                                }
                            }
                        )

                        val state by viewModel.state.collectAsState()
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            inboxSection(
                                inboxCount = state.inboxCount,
                                onSort = viewModel::onSort
                            )
                            todoSection(
                                tasks = state.todoTasks,
                                onTaskClick = viewModel::onTaskClick
                            )
                            waitingSection(
                                nextWaitingTask = state.nextWaitingTask,
                                waitingCount = state.waitingCount,
                                onTaskClick = viewModel::onTaskClick,
                                onMore = viewModel::onWaitingMore
                            )
                            maybeSection(
                                tasks = state.maybeTasks,
                                onTaskClick = viewModel::onTaskClick
                            )
                            fabSpaceItem()
                        }
                    }
                    FloatingActionButton(
                        backgroundColor = MaterialTheme.colors.primary,
                        onClick = viewModel::onAdd,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    ) { Icon(Icons.Default.Add, "") }
                }
            }
        }
    }
}

private fun LazyListScope.inboxSection(
    inboxCount: Int,
    onSort: () -> Unit
) {
    if (inboxCount > 0) {
        item {
            InboxMessage(
                count = inboxCount,
                onSort = onSort
            )
        }
    }
}

private fun LazyListScope.todoSection(
    tasks: List<UiTask>,
    onTaskClick: (UiTask) -> Unit
) {
    if (tasks.isNotEmpty()) {
        titleItem("ToDo")
        taskItems(
            tasks = tasks,
            onClick = onTaskClick
        )
        sectionSpaceItem()
    }
}

private fun LazyListScope.waitingSection(
    nextWaitingTask: Pair<UiTask, LocalDate>?,
    waitingCount: Int,
    onTaskClick: (UiTask) -> Unit,
    onMore: () -> Unit
) {
    if (nextWaitingTask != null) {
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val period = remember(nextWaitingTask) {
                    //TODO: reuse format logic
                    val period = Period.between(LocalDate.now(), nextWaitingTask.second)
                    buildString {
                        if (period.years > 0) {
                            append("${period.years}y ")
                        }
                        if (period.months > 0) {
                            append("${period.months}m ")
                        }
                        if (period.days > 0) {
                            append("${period.days}d")
                        }
                    }
                }
                Title("Up Next in $period")
                TaskCard(
                    task = nextWaitingTask.first,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTaskClick(nextWaitingTask.first) }
                )
                if (waitingCount > 1) {
                    MoreButton(
                        count = waitingCount - 1,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        onClick = onMore
                    )
                }
            }
        }
        sectionSpaceItem()
    }
}

private fun LazyListScope.maybeSection(
    tasks: List<UiTask>,
    onTaskClick: (UiTask) -> Unit
) {
    if (tasks.isNotEmpty()) {
        titleItem("Maybe")
        taskItems(
            tasks = tasks,
            onClick = onTaskClick
        )
    }
}

private fun LazyListScope.titleItem(text: String) {
    item { Title(text) }
}

private fun LazyListScope.taskItems(
    tasks: List<UiTask>,
    onClick: (UiTask) -> Unit
) {
    items(tasks) { task ->
        TaskCard(
            task = task,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(task) }
        )
    }
}

private fun LazyListScope.sectionSpaceItem() {
    item { Spacer(modifier = Modifier.height(16.dp)) }
}

private fun LazyListScope.fabSpaceItem() {
    item { Spacer(modifier = Modifier.height(56.dp)) }
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

@Composable
private fun InboxMessage(
    count: Int,
    onSort: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp)
    ) {
        //TODO: extract res
        Text(
            text = "Inbox is not empty: $count",
            style = MaterialTheme.typography.h5
        )
        Text(
            text = "SORT",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.clickable(onClick = onSort)
        )
    }
}

@Composable
private fun Title(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.h5
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MoreButton(
    count: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colors.primary),
        color = Color.Transparent,
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                top = 8.dp,
                bottom = 8.dp,
                start = 16.dp,
                end = 8.dp
            )
        ) {
            Text(
                text = "VIEW $count MORE",
                style = MaterialTheme.typography.button,
                color = MaterialTheme.colors.primary
            )
            Icon(
                Icons.Default.ChevronRight,
                "",
                tint = MaterialTheme.colors.primary
            )
        }
    }
}
