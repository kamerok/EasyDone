package easydone.feature.todo

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
import kotlinx.android.synthetic.main.fragment_todo.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn


class TodoFragment : Fragment() {

    private lateinit var repository: DomainRepository
    private lateinit var navigator: TodoNavigator

    private val adapter by lazy { TaskAdapter { navigator.navigateToTask(it) } }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_todo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter

        repository.getTasks(Task.Type.TO_DO)
            .onEachMain { tasks ->
                val uiTasks = tasks.map {
                    TaskUiModel(it.id, it.title, it.description.isNotEmpty())
                }
                adapter.setData(uiTasks)
            }
            .logErrors()
            .launchIn(GlobalScope)
    }

    data class Dependencies(
        val repository: DomainRepository,
        val navigator: TodoNavigator
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = TodoFragment().apply {
            repository = dependencies.repository
            navigator = dependencies.navigator
        }
    }

}