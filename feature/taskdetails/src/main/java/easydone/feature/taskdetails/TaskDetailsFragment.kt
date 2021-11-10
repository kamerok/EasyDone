package easydone.feature.taskdetails

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.systemBarsPadding
import dev.jeziellago.compose.markdowntext.MarkdownText
import easydone.core.domain.DomainRepository
import easydone.core.domain.model.Task
import easydone.coreui.design.AppTheme
import easydone.coreui.design.EasyDoneAppBar
import easydone.coreui.design.IconImportant
import easydone.coreui.design.IconUrgent
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


class TaskDetailsFragment(
    private val repository: DomainRepository,
    private val navigator: TaskDetailsNavigator
) : Fragment() {

    private val id: String by lazy { arguments?.getString(TASK_ID) ?: error("ID must be provided") }

    private val viewModel: TaskDetailsViewModel by viewModels(factoryProducer = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                TaskDetailsViewModel(id, repository, navigator) as T
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent { TaskDetailsScreen(viewModel) }
    }

    companion object {
        private const val TASK_ID = "task_id"

        fun createArgs(taskId: String): Bundle = bundleOf(TASK_ID to taskId)
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TaskDetailsScreen(
    viewModel: TaskDetailsViewModel
) {
    AppTheme {
        ProvideWindowInsets {
            val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
            val scope = rememberCoroutineScope()

            val state by viewModel.state.collectAsState()

            BackHandler(enabled = sheetState.isVisible) {
                scope.launch { sheetState.hide() }
            }

            CompositionLocalProvider(LocalElevationOverlay provides null) {
                ModalBottomSheetLayout(
                    sheetState = sheetState,
                    sheetContent = {
                        TypeSelector(
                            type = state.type,
                            date = state.date,
                            onTypeSelected = viewModel::onMove,
                            modifier = Modifier.navigationBarsPadding()
                        )
                    }
                ) {
                    BasicLayout(
                        toolbar = {
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
                        },
                        content = {
                            ScreenContent(
                                topContent = {
                                    TaskContent(state)
                                },
                                bottomContent = {
                                    BottomActions(
                                        onMove = { scope.launch { sheetState.show() } },
                                        onArchive = viewModel::onArchive
                                    )
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TypeSelector(
    type: Task.Type,
    date: LocalDate?,
    onTypeSelected: (Task.Type, LocalDate?) -> Unit,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        TypeSelectorItem(
            isSelected = type == Task.Type.INBOX,
            type = Task.Type.INBOX,
            onClick = { onTypeSelected(Task.Type.INBOX, null) }
        )
        Divider()
        TypeSelectorItem(
            isSelected = type == Task.Type.TO_DO,
            type = Task.Type.TO_DO,
            onClick = { onTypeSelected(Task.Type.TO_DO, null) }
        )
        val context = LocalContext.current
        val initialDay = date ?: LocalDate.now().plusDays(1)
        Divider()
        TypeSelectorItem(
            isSelected = type == Task.Type.WAITING,
            type = Task.Type.WAITING,
            date = date,
            onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val newDate = LocalDate.of(year, month + 1, dayOfMonth)
                        onTypeSelected(Task.Type.WAITING, newDate)
                    },
                    initialDay.year,
                    initialDay.monthValue - 1,
                    initialDay.dayOfMonth
                )
                    .apply {
                        datePicker.minDate =
                            LocalDate.now().plusDays(1)
                                .atStartOfDay(ZoneOffset.UTC).toInstant()
                                .toEpochMilli()
                    }
                    .show()
            }
        )
        Divider()
        TypeSelectorItem(
            isSelected = type == Task.Type.MAYBE,
            type = Task.Type.MAYBE,
            onClick = { onTypeSelected(Task.Type.MAYBE, null) }
        )
    }
}

@Composable
private fun TypeSelectorItem(
    isSelected: Boolean,
    type: Task.Type,
    date: LocalDate? = null,
    onClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        val typeText = when (type) {
            Task.Type.INBOX -> "Inbox"
            Task.Type.TO_DO -> "ToDo"
            Task.Type.WAITING -> "Waiting"
            Task.Type.MAYBE -> "Maybe"
        }
        if (isSelected) {
            Icon(Icons.Default.Check, "")
        }
        Text(
            text = typeText,
            style = MaterialTheme.typography.subtitle1
        )
        if (date != null) {
            val formatter = remember { DateTimeFormatter.ofPattern("d MMM y") }
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = "(${formatter.format(date)})",
                    style = MaterialTheme.typography.subtitle1
                )
            }
        }
    }
}

@Composable
private fun BasicLayout(
    toolbar: @Composable () -> Unit,
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
        Column {
            toolbar()
            content()
        }
    }
}

@Composable
private fun ScreenContent(
    topContent: @Composable () -> Unit,
    bottomContent: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
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
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 8.dp,
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp
            )
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
            colors = buttonColors(
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
    ScreenContent(
        topContent = {
            TaskContent(
                State(
                    type = Task.Type.WAITING,
                    date = LocalDate.of(2021, 1, 10),
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
