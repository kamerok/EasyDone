package easydone.feature.edittask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import easydone.core.domain.DomainRepository


class EditTaskFragment(
    private val repository: DomainRepository,
    private val navigator: EditTaskNavigator
) : Fragment() {

    private val args: EditTaskViewModel.Args by lazy {
        arguments?.getSerializable(ARGS) as? EditTaskViewModel.Args
            ?: EditTaskViewModel.Args.Create()
    }

    private val viewModel: EditTaskViewModel by viewModels(factoryProducer = {
        viewModelFactory {
            initializer {
                EditTaskViewModel(args, repository, navigator)
            }
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
        private const val ARGS = "args"

        fun editArgs(taskId: String): Bundle = bundleOf(ARGS to EditTaskViewModel.Args.Edit(taskId))

        fun shareArgs(text: String): Bundle = bundleOf(ARGS to EditTaskViewModel.Args.Create(text))
    }

}
