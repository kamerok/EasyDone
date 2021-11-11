package easydone.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import easydone.core.domain.DomainRepository
import easydone.core.domain.model.Task
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
            repository.getTasks(Task.Type.INBOX),
            repository.getTasks(Task.Type.TO_DO),
            repository.getTasks(Task.Type.WAITING),
            repository.getTasks(Task.Type.MAYBE)
        ) { inbox, todo, waiting, maybe ->
            State(
                inboxCount = inbox.size,
                todoTasks = todo.map { it.toUiTask() },
                nextWaitingTask = waiting.minByOrNull { it.dueDate!! }
                    ?.let { it.toUiTask() to it.dueDate!! },
                waitingCount = waiting.size,
                maybeTasks = maybe.map { it.toUiTask() }
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

    fun onSort() {}

    fun onTaskClick(task: UiTask) {
        navigator.navigateToTask(task.id)
    }

    fun onWaitingMore() {}

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

}
