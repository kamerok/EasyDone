package easydone.feature.feed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import easydone.core.domain.DomainRepository
import easydone.core.model.Task
import easydone.core.utils.daysBetween
import easydone.core.utils.logErrors
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class FeedFragment(
    private val repository: DomainRepository,
    private val navigator: FeedNavigator
) : Fragment(R.layout.fragment_feed) {

    private val adapter by lazy {
        FeedAdapter { id ->
            navigator.navigateToTask(
                id
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter
        combine(
            getInboxItems(),
            getTodoItems(),
            getWaitingItems()
        ) { inboxItems, todoItems, waitingItems -> inboxItems + todoItems + waitingItems }
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
            if (tasks.isEmpty()) {
                emptyList()
            } else {
                listOf(FeedHeader("WAITING")) +
                        tasks
                            .asSequence()
                            //todo: filtering null logic should be somewhere else
                            .filter { it.dueDate != null }
                            .sortedBy { it.dueDate }
                            .groupBy { it.dueDate }
                            .map { (date, items) ->
                                val title =
                                    "${DATE_FORMAT.format(date)} (${date?.daysBetween(Date())} left)"
                                listOf(FeedHeader(title)) + items.map { it.toUi() }
                            }
                            .flatten()
                            .toList()
            }
        }

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("dd MMMM", Locale.US)
    }

}
