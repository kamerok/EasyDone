package easydone.feature.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import easydone.core.domain.DomainRepository
import easydone.core.model.Task
import easydone.core.utils.logErrors
import easydone.core.utils.onEachMain
import easydone.coreui.taskitem.TaskAdapter
import easydone.coreui.taskitem.TaskUiModel
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.combineLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class FeedFragment : Fragment() {

    private lateinit var repository: DomainRepository
    private lateinit var navigator: FeedNavigator

    private val adapter by lazy { TaskAdapter { id -> navigator.navigateToTask(id) } }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_feed, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter
        repository.getTasks(Task.Type.INBOX)
            .combineLatest(repository.getTasks(Task.Type.TO_DO)) { inboxTasks, todoTasks ->
                (inboxTasks + todoTasks).map {
                    TaskUiModel(it.id, it.title, it.description.isNotEmpty())
                }
            }
            .onEachMain { adapter.setData(it) }
            .logErrors()
            .launchIn(GlobalScope)
    }

    data class Dependencies(
        val repository: DomainRepository,
        val navigator: FeedNavigator
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = FeedFragment().apply {
            repository = dependencies.repository
            navigator = dependencies.navigator
        }
    }

}