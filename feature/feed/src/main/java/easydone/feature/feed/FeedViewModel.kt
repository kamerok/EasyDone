package easydone.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import easydone.core.domain.DomainRepository
import easydone.core.model.Task
import easydone.core.utils.logErrors
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.threeten.bp.LocalDate
import org.threeten.bp.Period
import org.threeten.bp.format.DateTimeFormatter


class FeedViewModel(
    private val repository: DomainRepository,
    private val navigator: FeedNavigator
) : ViewModel() {

    private val stateChannel: BroadcastChannel<List<Any>> = ConflatedBroadcastChannel(emptyList())

    init {
        combine(
            getInboxItems(),
            getTodoItems(),
            getWaitingItems()
        ) { inboxItems, todoItems, waitingItems -> inboxItems + todoItems + waitingItems }
            .onEach { stateChannel.offer(it) }
            .logErrors()
            .launchIn(viewModelScope)
    }

    fun getData(): Flow<List<Any>> = stateChannel.asFlow()

    fun onTaskClick(id: String) {
        navigator.navigateToTask(id)
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
                                val days = Period.between(LocalDate.now(), date!!).days
                                val title = "${date.format(DATE_FORMAT)} ($days left)"
                                listOf(FeedHeader(title)) + items.map { it.toUi() }
                            }
                            .flatten()
                            .toList()
            }
        }

    companion object {
        private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMMM")
    }

}