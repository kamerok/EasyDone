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
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.combineLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map


class FeedFragment : Fragment() {

    private lateinit var repository: DomainRepository
    private lateinit var navigator: FeedNavigator

    private val adapter by lazy {
        FeedAdapter { id ->
            navigator.navigateToTask(
                id
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_feed, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter
        getInboxItems()
            .combineLatest(
                getTodoItems(),
                getWaitingItems()
            ) { inboxItems, todoItems, waitingItems ->
                inboxItems + todoItems + waitingItems
            }
            .onEachMain { adapter.items = it }
            .logErrors()
            .launchIn(GlobalScope)
    }

    private fun getInboxItems() = repository.getTasks(Task.Type.INBOX)
        .map { tasks -> listOf(FeedHeader("INBOX")) + tasks.map { it.toUi() } }

    private fun getTodoItems() = repository.getTasks(Task.Type.TO_DO)
        .map { tasks -> listOf(FeedHeader("TODO")) + tasks.map { it.toUi() } }

    private fun getWaitingItems() = repository.getTasks(Task.Type.WAITING)
        .map { tasks -> listOf(FeedHeader("WAITING")) + tasks.map { it.toUi() } }

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