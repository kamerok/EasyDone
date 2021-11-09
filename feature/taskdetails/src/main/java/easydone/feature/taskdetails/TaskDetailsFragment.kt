package easydone.feature.taskdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.systemBarsPadding
import easydone.core.domain.DomainRepository
import easydone.coreui.design.AppTheme
import easydone.coreui.design.EasyDoneAppBar


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
        setContent { EditTaskScreen(viewModel) }
    }

    companion object {
        private const val TASK_ID = "task_id"

        fun createArgs(taskId: String): Bundle = bundleOf(TASK_ID to taskId)
    }

}

@Composable
private fun EditTaskScreen(
    viewModel: TaskDetailsViewModel
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
            ScreenContent(viewModel.state.collectAsState().value)
        }
    )
}

@Composable
private fun BasicLayout(
    toolbar: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    AppTheme {
        ProvideWindowInsets {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                Column {
                    toolbar()
                    content()
                }
            }
        }
    }
}

@Composable
private fun ScreenContent(state: State) {
    Text(state.toString())
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
        State(
            type = "Type",
            title = "Title",
            description = "Desc",
            isUrgent = true,
            isImportant = true
        )
    )
}
