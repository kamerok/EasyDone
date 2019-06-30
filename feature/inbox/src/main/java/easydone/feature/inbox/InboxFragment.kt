package easydone.feature.inbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kamer.inbox.R
import easydone.core.domain.DomainRepository
import easydone.coreui.taskitem.TaskAdapter
import easydone.coreui.taskitem.TaskUiModel
import kotlinx.android.synthetic.main.fragment_inbox.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class InboxFragment : Fragment() {

    private lateinit var repository: DomainRepository
    private lateinit var navigator: InboxNavigator

    private val adapter by lazy { TaskAdapter { navigator.navigateToTask(it) } }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_inbox, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter

        GlobalScope.launch(Dispatchers.IO) {
            repository.getTasks(true).collect { tasks ->
                val uiTasks = tasks.map {
                    TaskUiModel(
                        it.id,
                        it.title
                    )
                }
                withContext(Dispatchers.Main) {
                    adapter.setData(uiTasks)
                }
            }
        }
    }

    data class Dependencies(
        val repository: DomainRepository,
        val navigator: InboxNavigator
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = InboxFragment().apply {
            repository = dependencies.repository
            navigator = dependencies.navigator
        }
    }

}