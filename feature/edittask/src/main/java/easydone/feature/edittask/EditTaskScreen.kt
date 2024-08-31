package easydone.feature.edittask

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import easydone.core.domain.DomainRepository
import easydone.core.domain.model.Task
import easydone.core.strings.R
import easydone.coreui.design.EasyDoneAppBar
import easydone.coreui.design.IconImportant
import easydone.coreui.design.IconUrgent
import easydone.feature.selecttype.TypeSelector
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.Serializable
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter


@Composable
fun EditTaskRoute(
    args: EditTaskArgs,
    repository: DomainRepository,
    navigator: EditTaskNavigator
) {
    val viewModel: EditTaskViewModel = viewModel {
        EditTaskViewModel(args, repository, navigator)
    }
    val state by viewModel.state.collectAsState()
    EditTaskScreen(
        events = viewModel.events,
        state = state,
        onTypeClick = viewModel::onTypeClick,
        onTypeSelected = viewModel::onTypeSelected,
        onUrgentClick = viewModel::onUrgentClick,
        onImportantClick = viewModel::onImportantClick,
        onSave = viewModel::onSave,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditTaskScreen(
    events: Flow<Event>,
    state: State,
    onTypeClick: () -> Unit,
    onTypeSelected: (Task.Type) -> Unit,
    onUrgentClick: () -> Unit,
    onImportantClick: () -> Unit,
    onSave: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
) {
    FullscreenContent {
        var openBottomSheet by rememberSaveable { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        BackHandler(enabled = openBottomSheet) {
            scope.launch { openBottomSheet = false }
        }

        var selectorType: Task.Type by remember { mutableStateOf(Task.Type.Inbox) }

        LaunchedEffect(events) {
            events
                .onEach {
                    when (it) {
                        is OpenSelectType -> {
                            selectorType = it.currentType
                            scope.launch { openBottomSheet = true }
                        }

                        is CloseSelectType -> scope.launch { openBottomSheet = false }
                    }
                }
                .launchIn(this)
        }

        EditTaskContent(
            state = state,
            onTypeClick = onTypeClick,
            onUrgentClick = onUrgentClick,
            onImportantClick = onImportantClick,
            onSave = onSave,
            onTitleChange = onTitleChange,
            onDescriptionChange = onDescriptionChange
        )

        if (openBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { openBottomSheet = false },
                tonalElevation = 0.dp,
                content = {
                    TypeSelector(
                        type = selectorType,
                        onTypeSelected = onTypeSelected
                    )
                }
            )
        }
    }
}

@Composable
private fun EditTaskContent(
    state: State,
    onTypeClick: () -> Unit,
    onUrgentClick: () -> Unit,
    onImportantClick: () -> Unit,
    onSave: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
) {
    if (state is ContentState) {
        val focusRequester = remember { FocusRequester() }
        if (state.isCreate) {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
        ScreenContent(
            isCreate = state.isCreate,
            topContent = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    TaskType(
                        label = state.type.format(),
                        onClick = onTypeClick
                    )
                    TaskTitle(
                        text = state.title,
                        error = state.titleError,
                        onChange = onTitleChange,
                        modifier = Modifier.focusRequester(focusRequester)
                    )
                    TaskDescription(
                        text = state.description,
                        onDescriptionChange = onDescriptionChange
                    )
                    TaskMarkers(
                        isUrgent = state.isUrgent,
                        isImportant = state.isImportant,
                        onUrgentClick = onUrgentClick,
                        onImportantClick = onImportantClick
                    )
                }
            },
            bottomContent = {
                SaveButton(onSave)
            }
        )
    }
}

//TODO: extract resources, reuse format logic
private fun Task.Type.format() = when (this) {
    is Task.Type.Inbox -> "INBOX"
    is Task.Type.ToDo -> "TO-DO"
    is Task.Type.Waiting -> "WAITING".plus(date.let {
        val period = Period.between(LocalDate.now(), it)
        val periodString = buildString {
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
        " until ${it.format(DateTimeFormatter.ofPattern("d MMM y"))} ($periodString)"
    })

    is Task.Type.Project -> "PROJECT"
    is Task.Type.Maybe -> "MAYBE"
}

@Composable
private fun FullscreenContent(
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            //to draw under paddings
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .imePadding()
    ) {
        content()
    }
}

@Composable
private fun ScreenContent(
    isCreate: Boolean,
    topContent: @Composable () -> Unit,
    bottomContent: @Composable () -> Unit,
) {
    Column {
        EasyDoneAppBar {
            Text(
                stringResource(
                    if (isCreate) {
                        R.string.edit_task_screen_title_add
                    } else {
                        R.string.edit_task_screen_title_edit
                    }
                )
            )
        }
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
}

@Composable
private fun TaskType(
    label: String,
    onClick: () -> Unit
) {
    Row(Modifier.clickable(onClick = onClick)) {
        Text(label)
        Icon(Icons.Default.ArrowDropDown, "")
    }
}

@Composable
private fun TaskTitle(
    text: String,
    error: String?,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        val isError = !error.isNullOrEmpty()
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            onValueChange = onChange,
            isError = isError,
            label = { Text(stringResource(R.string.edit_task_title)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Sentences
            )
        )
        if (isError) {
            Text(
                text = checkNotNull(error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
private fun TaskDescription(
    text: String,
    onDescriptionChange: (String) -> Unit
) {
    OutlinedTextField(
        value = text,
        onValueChange = onDescriptionChange,
        label = { Text(stringResource(R.string.edit_task_description)) },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun TaskMarkers(
    isUrgent: Boolean,
    isImportant: Boolean,
    onUrgentClick: () -> Unit,
    onImportantClick: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Chip(
            isSelected = isUrgent,
            icon = { IconUrgent() },
            label = { Text(stringResource(R.string.urgent)) },
            onClick = onUrgentClick
        )
        Chip(
            isSelected = isImportant,
            icon = { IconImportant() },
            label = { Text(stringResource(R.string.important)) },
            onClick = onImportantClick
        )
    }
}

@Composable
private fun Chip(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(CornerSize(100)),
        border = if (isSelected) null else BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = modifier
    ) {
        Row(
            Modifier
                .clickable(onClick = onClick)
                .padding(top = 4.dp, bottom = 4.dp, start = 8.dp, end = 16.dp)
        ) {
            icon()
            label()
        }
    }
}

@Composable
private fun SaveButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.edit_task_save).uppercase())
    }
}

sealed class EditTaskArgs : Serializable {
    data class Create(
        val text: String = "",
        val type: Task.Type = Task.Type.Inbox
    ) : EditTaskArgs()

    data class Edit(val id: String) : EditTaskArgs()
}
