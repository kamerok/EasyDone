package easydone.feature.taskdetails

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.jeziellago.compose.markdowntext.MarkdownText
import easydone.core.domain.DomainRepository
import easydone.core.domain.model.Task
import easydone.core.strings.R
import easydone.coreui.design.AppTheme
import easydone.coreui.design.EasyDoneAppBar
import easydone.coreui.design.IconImportant
import easydone.coreui.design.IconUrgent
import easydone.coreui.design.important
import easydone.feature.selecttype.TypeSelector
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun TaskDetailsRoute(
    id: String,
    repository: DomainRepository,
    navigator: TaskDetailsNavigator
) {
    val viewModel: TaskDetailsViewModel = viewModel {
        TaskDetailsViewModel(id, repository, navigator)
    }
    val state by viewModel.state.collectAsState()
    TaskDetailsScreen(
        state = state,
        events = viewModel.events,
        onTypeSelected = viewModel::onTypeSelected,
        onEdit = viewModel::onEdit,
        onMove = viewModel::onMove,
        onArchive = viewModel::onArchive
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TaskDetailsScreen(
    state: State,
    events: Flow<Event>,
    onTypeSelected: (Task.Type) -> Unit,
    onEdit: () -> Unit,
    onMove: () -> Unit,
    onArchive: () -> Unit,
) {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var selectorType: Task.Type by remember { mutableStateOf(Task.Type.Inbox) }
    LaunchedEffect(events) {
        events
            .onEach {
                when (it) {
                    is SelectType -> {
                        selectorType = it.currentType
                        openBottomSheet = true
                    }
                }
            }
            .launchIn(this)
    }

    BackHandler(enabled = openBottomSheet) {
        scope.launch { openBottomSheet = false }
    }

    TaskDetailsContent(
        state = state,
        onEdit = onEdit,
        onMove = onMove,
        onArchive = onArchive
    )

    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            tonalElevation = 0.dp,
            content = {
                TypeSelector(
                    type = selectorType,
                    onTypeSelected = onTypeSelected,
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        )
    }
}

@Composable
private fun TaskDetailsContent(
    state: State,
    onEdit: () -> Unit,
    onMove: () -> Unit,
    onArchive: () -> Unit,
) {
    FullscreenContent {
        Column(modifier = Modifier.systemBarsPadding()) {
            EasyDoneAppBar(
                title = {
                    Text(stringResource(R.string.task_details_screen_title))
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, "")
                    }
                }
            )
            VerticallySplitContent(
                topContent = {
                    TaskContent(state)
                },
                bottomContent = {
                    BottomActions(
                        onMove = onMove,
                        onArchive = onArchive
                    )
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun FullscreenContent(
    content: @Composable () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        content()
    }
}

@Composable
private fun VerticallySplitContent(
    topContent: @Composable () -> Unit,
    bottomContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(
                top = 8.dp,
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        topContent()
        Spacer(modifier = Modifier.height(16.dp))
        bottomContent()
    }
}

@Composable
private fun TaskContent(state: State) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = state.typeText,
                fontWeight = FontWeight.Normal,
            )
            Text(
                text = state.title,
                style = MaterialTheme.typography.headlineSmall
            )
        }
        if (state.description.isNotEmpty()) {
            MarkdownText(
                markdown = state.description,
                style = LocalTextStyle.current.copy(color = LocalContentColor.current)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            if (state.isUrgent) {
                Chip(
                    icon = { IconUrgent() },
                    label = { Text(stringResource(R.string.urgent)) }
                )
            }
            if (state.isImportant) {
                Chip(
                    icon = { IconImportant() },
                    label = { Text(stringResource(R.string.important)) }
                )
            }
        }
    }
}

@Composable
private fun Chip(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(CornerSize(100)),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        Row(
            Modifier.padding(top = 4.dp, bottom = 4.dp, start = 8.dp, end = 16.dp)
        ) {
            icon()
            label()
        }
    }
}

@Composable
private fun BottomActions(
    onMove: () -> Unit,
    onArchive: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = onMove,
            modifier = Modifier.weight(1f, true)
        ) {
            Text(stringResource(R.string.task_details_action_move).uppercase())
        }
        Button(
            onClick = onArchive,
            colors = ButtonDefaults.buttonColors(
                containerColor = important
            ),
            modifier = Modifier.weight(1f, true)
        ) {
            Text(stringResource(R.string.task_details_action_archive).uppercase())
        }
    }
}

@Preview(
    name = "Screen",
    widthDp = 393,
    heightDp = 851,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ContentPreview() {
    AppTheme {
        FullscreenContent {
            VerticallySplitContent(
                topContent = {
                    TaskContent(
                        State(
                            typeText = "Type",
                            title = "Title",
                            description = "Desc",
                            isUrgent = true,
                            isImportant = true
                        )
                    )
                },
                bottomContent = { BottomActions({}, {}) }
            )
        }
    }
}

@Preview(
    name = "Screen Light",
    widthDp = 393,
    heightDp = 851,
)
@Composable
private fun ContentPreviewLight() {
    AppTheme {
        FullscreenContent {
            VerticallySplitContent(
                topContent = {
                    TaskContent(
                        State(
                            typeText = "Type",
                            title = "Title",
                            description = "Desc",
                            isUrgent = true,
                            isImportant = true
                        )
                    )
                },
                bottomContent = { BottomActions({}, {}) }
            )
        }
    }
}
