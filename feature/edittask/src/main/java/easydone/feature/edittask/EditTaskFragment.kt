package easydone.feature.edittask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import easydone.core.domain.DomainRepository
import easydone.core.domain.model.Task


class EditTaskFragment(
    private val repository: DomainRepository,
    private val navigator: EditTaskNavigator
) : Fragment() {

    private val args: EditTaskViewModel.Args by lazy {
        arguments?.let {
            BundleCompat.getSerializable(it, ARGS, EditTaskViewModel.Args::class.java)
        } ?: EditTaskViewModel.Args.Create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            EditTaskRoute(args, repository, navigator)
        }
    }

    companion object {
        private const val ARGS = "args"

        fun editArgs(taskId: String): Bundle = bundleOf(ARGS to EditTaskViewModel.Args.Edit(taskId))

        fun createArgs(
            text: String = "",
            type: Task.Type = Task.Type.Inbox
        ): Bundle = bundleOf(ARGS to EditTaskViewModel.Args.Create(text, type))
    }

}
