package easydone.feature.taskdetails

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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.jeziellago.compose.markdowntext.MarkdownText
import easydone.core.domain.model.Task
import easydone.core.strings.R
import easydone.coreui.design.AppTheme
import easydone.coreui.design.EasyDoneAppBar
import easydone.coreui.design.IconImportant
import easydone.coreui.design.IconUrgent
import easydone.feature.selecttype.TypeSelector
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun TaskDetailsScreen(
    viewModel: TaskDetailsViewModel
) {
    AppTheme {
        val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
        val scope = rememberCoroutineScope()

        var selectorType: Task.Type by remember { mutableStateOf(Task.Type.Inbox) }
        LaunchedEffect(viewModel) {
            viewModel.events
                .onEach {
                    when (it) {
                        is SelectType -> {
                            selectorType = it.currentType
                            sheetState.show()
                        }
                    }
                }
                .launchIn(this)
        }

        BackHandler(enabled = sheetState.isVisible) {
            scope.launch { sheetState.hide() }
        }

        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetContent = {
                TypeSelector(
                    type = selectorType,
                    onTypeSelected = viewModel::onTypeSelected,
                    modifier = Modifier.navigationBarsPadding()
                )
            },
            content = { TaskDetailsContent(viewModel) }
        )
    }
}

@Composable
private fun TaskDetailsContent(viewModel: TaskDetailsViewModel) {
    FullscreenContent {
        Column(modifier = Modifier.systemBarsPadding()) {
            EasyDoneAppBar(
                title = {
                    Text(stringResource(R.string.task_details_screen_title))
                },
                actions = {
                    IconButton(onClick = viewModel::onEdit) {
                        Icon(Icons.Default.Edit, "")
                    }
                }
            )
            VerticallySplitContent(
                topContent = {
                    val state by viewModel.state.collectAsState()
                    TaskContent(state)
                },
                bottomContent = {
                    BottomActions(
                        onMove = viewModel::onMove,
                        onArchive = viewModel::onArchive
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
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
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
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = state.typeText,
                    style = MaterialTheme.typography.caption,
                )
            }
            Text(
                text = state.title,
                style = MaterialTheme.typography.h5
            )
        }
        if (state.description.isNotEmpty()) {
            MarkdownText(markdown = state.description)
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
                backgroundColor = MaterialTheme.colors.error
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
    showBackground = true
)
@Composable
private fun ContentPreview() {
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
