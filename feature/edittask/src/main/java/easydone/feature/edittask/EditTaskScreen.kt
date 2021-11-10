package easydone.feature.edittask

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.statusBarsPadding
import easydone.coreui.design.AppTheme
import easydone.coreui.design.EasyDoneAppBar
import easydone.coreui.design.IconImportant
import easydone.coreui.design.IconUrgent


@Composable
internal fun EditTaskScreen(viewModel: EditTaskViewModel) {
    AppTheme {
        ProvideWindowInsets(windowInsetsAnimationsEnabled = false) {
            FullscreenContent {
                val state = viewModel.state.collectAsState()
                val stateValue = state.value
                if (stateValue is ContentState) {
                    ScreenContent(
                        topContent = {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                TaskType(
                                    label = stateValue.type,
                                    onClick = viewModel::onTypeClick
                                )
                                TaskTitle(
                                    text = stateValue.title,
                                    error = stateValue.titleError,
                                    onChange = viewModel::onTitleChange
                                )
                                TaskDescription(
                                    text = stateValue.description,
                                    onDescriptionChange = viewModel::onDescriptionChange
                                )
                                TaskMarkers(
                                    isUrgent = stateValue.isUrgent,
                                    isImportant = stateValue.isImportant,
                                    onUrgentClick = viewModel::onUrgentClick,
                                    onImportantClick = viewModel::onImportantClick
                                )
                            }
                        },
                        bottomContent = {
                            SaveButton(viewModel::onSave)
                        }
                    )
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
            .statusBarsPadding()
            .navigationBarsWithImePadding()
    ) {
        content()
    }
}

@Composable
private fun ScreenContent(
    topContent: @Composable () -> Unit,
    bottomContent: @Composable () -> Unit,
) {
    Column {
        EasyDoneAppBar { Text(stringResource(R.string.edit_task_screen_title)) }
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
    onChange: (String) -> Unit
) {
    Column {
        val isError = !error.isNullOrEmpty()
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            onValueChange = onChange,
            isError = isError,
            label = { Text(stringResource(R.string.edit_task_title)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )
        if (isError) {
            Text(
                text = checkNotNull(error),
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
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
        color = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.background,
        shape = RoundedCornerShape(CornerSize(100)),
        border = if (isSelected) null else BorderStroke(
            1.dp,
            MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
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
