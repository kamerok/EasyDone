package easydone.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.systemBarsPadding
import com.kamer.home.R
import easydone.coreui.design.AppTheme
import easydone.coreui.design.EasyDoneAppBar
import easydone.coreui.design.IconImportant
import easydone.coreui.design.IconText
import easydone.coreui.design.IconUrgent
import java.time.LocalDate


@Composable
internal fun HomeScreen() {
    AppTheme {
        ProvideWindowInsets {
            FullscreenContent {
                Column {
                    EasyDoneAppBar(navigationIcon = null) {
                        Text(stringResource(R.string.app_name))
                    }
                    fun task() = UiTask("id", "Task", false, false, false)
                    val state = State(
                        inboxCount = 5,
                        todoTasks = (0..100).map { task() },
                        nextWaitingTask = task() to LocalDate.now().plusDays(10),
                        waitingCount = 10,
                        maybeTasks = (0..100).map { task() }
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(32.dp),
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        if (state.inboxCount > 0 || state.todoTasks.isNotEmpty()) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                if (state.inboxCount > 0) {
                                    InboxMessage(
                                        count = state.inboxCount,
                                        onSort = {}
                                    )
                                }
                                if (state.todoTasks.isNotEmpty()) {
                                    Title("ToDo")
                                    state.todoTasks.forEach { task ->
                                        TaskCard(
                                            task = task,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { }
                                        )
                                    }
                                }
                            }
                        }
                        if (state.nextWaitingTask != null) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Title("Up Next")
                                TaskCard(
                                    task = state.nextWaitingTask.first,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { }
                                )
                                if (state.waitingCount > 1) {
                                    MoreButton(
                                        count = state.waitingCount - 1,
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally),
                                        onClick = {}
                                    )
                                }
                            }
                        }
                        if (state.maybeTasks.isNotEmpty()) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Title("Maybe")
                                state.maybeTasks.forEach { task ->
                                    TaskCard(
                                        task = task,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { }
                                    )
                                }
                            }
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

@Composable
private fun TaskCard(
    task: UiTask,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = task.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (task.hasDescription || task.isUrgent || task.isImportant) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (task.hasDescription) {
                        IconText()
                    }
                    if (task.isImportant) {
                        IconImportant()
                    }
                    if (task.isUrgent) {
                        IconUrgent()
                    }
                }
            }
        }
    }
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

@Preview(showBackground = true)
@Composable
private fun ScreenPreview() {
    HomeScreen()
}

@Preview
@Composable
private fun TaskCardPreview() {
    TaskCard(
        task = UiTask(
            id = "id",
            title = "Title",
            hasDescription = true,
            isUrgent = true,
            isImportant = true
        )
    )
}

@Preview
@Composable
private fun ShortTaskCardPreview() {
    TaskCard(
        task = UiTask(
            id = "id",
            title = "Title",
            hasDescription = false,
            isUrgent = false,
            isImportant = false
        )
    )
}

@Preview(widthDp = 300)
@Composable
private fun LongTaskCardPreview() {
    TaskCard(
        task = UiTask(
            id = "id",
            title = "Title title title title title title title title title title" +
                    " title title title title title title title title title title" +
                    " title title title title title",
            hasDescription = true,
            isUrgent = true,
            isImportant = true
        )
    )
}
