package easydone.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import easydone.core.domain.DomainRepository
import easydone.core.domain.model.Task
import easydone.coreui.design.UiTask
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn


internal class HomeViewModel(
    repository: DomainRepository,
    private val navigator: HomeNavigator
) : ViewModel() {

    val state: StateFlow<State> =
        combine(
            repository.getTasks(Task.Type.Inbox::class),
            repository.getTasks(Task.Type.ToDo::class),
            repository.getTasks(Task.Type.Waiting::class),
            repository.getTasks(Task.Type.Maybe::class)
        ) { inbox, todo, waiting, maybe ->
            State(
                inboxCount = inbox.size,
                todoTasks = todo.sortedWith(taskComparator).map { it.toUiTask() },
                nextWaitingTask = waiting.minByOrNull { (it.type as Task.Type.Waiting).date }
                    ?.let { it.toUiTask() to (it.type as Task.Type.Waiting).date },
                waitingCount = waiting.size,
                maybeTasks = maybe.sortedWith(taskComparator).map { it.toUiTask() }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = State(
                inboxCount = 0,
                todoTasks = emptyList(),
                nextWaitingTask = null,
                waitingCount = 0,
                maybeTasks = emptyList()
            )
        )

    fun onAdd() {
        navigator.navigateToCreate()
    }

    fun onSort() {
        navigator.navigateToInbox()
    }

    fun onTaskClick(task: UiTask) {
        navigator.navigateToTask(task.id)
    }

    fun onWaitingMore() {
        navigator.navigateToWaiting()
    }

    fun onSettings() {
        navigator.navigateToSettings()
    }

    private fun Task.toUiTask() = UiTask(
        id = id,
        title = title,
        hasDescription = description.isNotBlank(),
        isUrgent = markers.isUrgent,
        isImportant = markers.isImportant
    )

    companion object {
        private val taskComparator = compareBy<Task>(
            {
                when {
                    it.markers.isUrgent && it.markers.isImportant -> 0
                    it.markers.isUrgent -> 1
                    it.markers.isImportant -> 2
                    else -> 3
                }
            },
            { it.title }
        )
    }

}
