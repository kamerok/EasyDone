package easydone.feature.taskdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import easydone.core.domain.DomainRepository


class TaskDetailsFragment(
    private val repository: DomainRepository,
    private val navigator: TaskDetailsNavigator
) : Fragment() {

    private val taskId: String by lazy { arguments?.getString(TASK_ID) ?: error("ID must be provided") }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent { TaskDetailsRoute(taskId, repository, navigator) }
    }

    companion object {
        private const val TASK_ID = "task_id"

        fun createArgs(taskId: String): Bundle = bundleOf(TASK_ID to taskId)
    }

}
