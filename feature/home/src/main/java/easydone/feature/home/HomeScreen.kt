package easydone.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
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
import androidx.lifecycle.viewmodel.compose.viewModel
import easydone.core.domain.DomainRepository
import easydone.core.domain.SyncScheduler
import easydone.core.strings.R
import easydone.coreui.design.AppTheme
import easydone.coreui.design.EasyDoneAppBar
import easydone.coreui.design.TaskCard
import easydone.coreui.design.UiTask
import java.time.LocalDate
import java.time.Period

@Composable
internal fun HomeRoute(
    syncScheduler: SyncScheduler,
    domainRepository: DomainRepository,
    navigator: HomeNavigator
) {
    val viewModel: HomeViewModel = viewModel {
        HomeViewModel(syncScheduler, domainRepository, navigator)
    }
    val state by viewModel.state.collectAsState()
    HomeScreen(
        state,
        viewModel::onSync,
        viewModel::onSettings,
        viewModel::onAdd,
        viewModel::onSort,
        viewModel::onTaskClick,
        viewModel::onWaitingMore
    )
}

@Composable
internal fun HomeScreen(
    state: State,
    onSync: () -> Unit,
    onSettings: () -> Unit,
    onAdd: () -> Unit,
    onSortInbox: () -> Unit,
    onTask: (UiTask) -> Unit,
    onWaitingMore: () -> Unit,
) {
    AppTheme {
        Scaffold(
            topBar = {
                EasyDoneAppBar(
                    navigationIcon = null,
                    title = { Text(stringResource(R.string.app_name)) },
                    actions = {
                        SyncButton(
                            isInProgress = state.isSyncing,
                            isIndicatorEnabled = state.hasChanges,
                            onClick = onSync
                        )
                    },
                    menu = {
                        DropdownMenuItem(onClick = onSettings) {
                            Text(text = "Settings")
                        }
                    },
                    modifier = Modifier.statusBarsPadding()
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    backgroundColor = MaterialTheme.colors.primary,
                    onClick = onAdd
                ) { Icon(Icons.Default.Add, "") }
            }
        ) { padding ->
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(padding)
            ) {
                inboxSection(
                    inboxCount = state.inboxCount,
                    onSort = onSortInbox
                )
                todoSection(
                    tasks = state.todoTasks,
                    onTaskClick = onTask
                )
                projectsSection(
                    tasks = state.projectTasks,
                    onTaskClick = onTask
                )
                waitingSection(
                    nextWaitingTasks = state.nextWaitingTasks,
                    waitingCount = state.waitingCount,
                    onTaskClick = onTask,
                    onMore = onWaitingMore
                )
                maybeSection(
                    tasks = state.maybeTasks,
                    onTaskClick = onTask
                )
                fabSpaceItem()
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
    nextWaitingTasks: NextWaitingTasks?,
    waitingCount: Int,
    onTaskClick: (UiTask) -> Unit,
    onMore: () -> Unit
) {
    if (nextWaitingTasks != null) {
        item {
            val period = remember(nextWaitingTasks) {
                //TODO: reuse format logic
                val period = Period.between(LocalDate.now(), nextWaitingTasks.date)
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
        }
        items(nextWaitingTasks.tasks, key = { it.id }) { task ->
            TaskCard(
                task = task,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTaskClick(task) }
            )
        }
        item {
            if (waitingCount > nextWaitingTasks.tasks.size) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    MoreButton(
                        count = waitingCount - nextWaitingTasks.tasks.size,
                        modifier = Modifier
                            .align(Alignment.Center),
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

private fun LazyListScope.projectsSection(
    tasks: List<UiTask>,
    onTaskClick: (UiTask) -> Unit
) {
    if (tasks.isNotEmpty()) {
        titleItem("Projects")
        taskItems(
            tasks = tasks,
            onClick = onTaskClick
        )
        sectionSpaceItem()
    }
}

private fun LazyListScope.titleItem(text: String) {
    item { Title(text) }
}

private fun LazyListScope.taskItems(
    tasks: List<UiTask>,
    onClick: (UiTask) -> Unit
) {
    items(tasks, key = { it.id }) { task ->
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
