package easydone.feature.edittask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import easydone.core.domain.DomainRepository


class EditTaskFragment(
    private val repository: DomainRepository,
    private val navigator: EditTaskNavigator
) : Fragment() {

    private val id: String? by lazy { arguments?.getString(TASK_ID) }
    private val sharedText: String? by lazy { arguments?.getString(SHARED_TEXT) }

    private val viewModel: EditTaskViewModel by viewModels(factoryProducer = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                EditTaskViewModel(id, sharedText, repository, navigator) as T
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
        private const val SHARED_TEXT = "shared_text"

        fun editArgs(taskId: String): Bundle = bundleOf(TASK_ID to taskId)

        fun shareArgs(text: String): Bundle = bundleOf(SHARED_TEXT to text)
    }

}
