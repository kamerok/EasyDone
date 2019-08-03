package easydone.feature.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import easydone.core.domain.DomainRepository
import easydone.core.model.Task
import easydone.core.utils.daysBetween
import easydone.core.utils.logErrors
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.coroutines.flow.combineLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


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
            .onEach { adapter.items = it }
            .logErrors()
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun getInboxItems() = repository.getTasks(Task.Type.INBOX)
        .map { tasks -> listOf(FeedHeader("INBOX")) + tasks.map { it.toUi() } }

    private fun getTodoItems() = repository.getTasks(Task.Type.TO_DO)
        .map { tasks -> listOf(FeedHeader("TODO")) + tasks.map { it.toUi() } }

    private fun getWaitingItems() = repository.getTasks(Task.Type.WAITING)
        .map { tasks ->
            listOf(FeedHeader("WAITING")) +
                    tasks
                        .sortedBy { it.dueDate }
                        .groupBy { it.dueDate }
                        .map { (date, items) ->
                            val title = "${DATE_FORMAT.format(date)} (${date?.daysBetween(Date())} left)"
                            listOf(FeedHeader(title)) + items.map { it.toUi() }
                        }
                        .flatten()
        }

    data class Dependencies(
        val repository: DomainRepository,
        val navigator: FeedNavigator
    )

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("dd MMMM", Locale.US)

        fun create(dependencies: Dependencies): Fragment = FeedFragment().apply {
            repository = dependencies.repository
            navigator = dependencies.navigator
        }
    }

}