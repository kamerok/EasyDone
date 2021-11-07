package easydone.feature.edittask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.statusBarsPadding
import easydone.core.domain.DomainRepository
import easydone.coreui.design.AppTheme
import easydone.coreui.design.IconImportant
import easydone.coreui.design.IconUrgent


class EditTaskFragment(
    private val repository: DomainRepository,
    private val navigator: EditTaskNavigator
) : Fragment() {

    private val id: String by lazy { arguments?.getString(TASK_ID) ?: error("ID must be provided") }

    private val viewModel: EditTaskViewModel by viewModels(factoryProducer = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                EditTaskViewModel(id, repository, navigator) as T
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            EditTaskScreen(viewModel)
        }
    }

    companion object {
        private const val TASK_ID = "task_id"

        fun createArgs(taskId: String): Bundle = bundleOf(TASK_ID to taskId)
    }

}

@Composable
private fun EditTaskScreen(viewModel: EditTaskViewModel) {
    AppTheme {
        ProvideWindowInsets {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsWithImePadding()
            ) {
                val state = viewModel.state.collectAsState()
                ScreenContent(
                    state = state.value,
                    onTypeClick = viewModel::onTypeClick,
                    onTitleChange = viewModel::onTitleChange,
                    onDescriptionChange = viewModel::onDescriptionChange,
                    onUrgentClick = viewModel::onUrgentClick,
                    onImportantClick = viewModel::onImportantClick,
                    onSave = viewModel::onSave
                )
            }
        }
    }
}

@Composable
private fun ScreenContent(
    state: State,
    onTypeClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onUrgentClick: () -> Unit,
    onImportantClick: () -> Unit,
    onSave: () -> Unit
) {
    Column {
        AppBar()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Fields(
                state = state,
                onTypeClick = onTypeClick,
                onTitleChange = onTitleChange,
                onDescriptionChange = onDescriptionChange,
                onUrgentClick = onUrgentClick,
                onImportantClick = onImportantClick
            )
            Spacer(modifier = Modifier.height(16.dp))
            SaveButton(onSave)
        }
    }
}

@Composable
private fun AppBar() {
    val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    TopAppBar(
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.background,
        title = { Text(stringResource(R.string.edit_task_screen_title)) },
        navigationIcon = {
            IconButton(onClick = { dispatcher?.onBackPressed() }) {
                Icon(Icons.Default.ArrowBack, "")
            }
        },
    )
}

@Composable
private fun Fields(
    state: State,
    onTypeClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onUrgentClick: () -> Unit,
    onImportantClick: () -> Unit
) {
    Column {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(Modifier.clickable(onClick = onTypeClick)) {
                Text(state.type)
                Icon(Icons.Default.ArrowDropDown, "")
            }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.title,
                onValueChange = onTitleChange,
                label = { Text(stringResource(R.string.edit_task_title)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )
            OutlinedTextField(
                value = state.description,
                onValueChange = onDescriptionChange,
                label = { Text(stringResource(R.string.edit_task_description)) },
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Chip(
                    isSelected = state.isUrgent,
                    icon = { IconUrgent() },
                    label = { Text(stringResource(R.string.urgent)) },
                    onClick = onUrgentClick
                )
                Chip(
                    isSelected = state.isImportant,
                    icon = { IconImportant() },
                    label = { Text(stringResource(R.string.important)) },
                    onClick = onImportantClick
                )
            }
        }
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

@Preview(
    name = "Screen",
    widthDp = 393,
    heightDp = 851,
    showBackground = true
)
@Composable
private fun ContentPreview() {
    ScreenContent(
        State("Type", "", "", false, false), {}, {}, {}, {}, {}, {}
    )
}
