package easydone.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import easydone.core.domain.DomainRepository
import easydone.core.domain.Synchronizer
import easydone.core.domain.model.Task
import easydone.coreui.design.UiTask
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn


internal class HomeViewModel(
    private val synchronizer: Synchronizer,
    repository: DomainRepository,
    private val navigator: HomeNavigator
) : ViewModel() {

    val state: StateFlow<State> =
        combine(
            combine(
                synchronizer.isSyncing(),
                synchronizer.observeChanges()
            ) { isSyncing, changes -> isSyncing to changes },
            repository.getAllTasks()
        ) { (isSyncing, changesCount), tasks ->
            val waitingTasks = tasks.filter { it.type is Task.Type.Waiting }
            State(
                isSyncing = isSyncing,
                hasChanges = changesCount > 0,
                inboxCount = tasks.count { it.type == Task.Type.Inbox },
                todoTasks = tasks
                    .filter { it.type == Task.Type.ToDo }
                    .sortedWith(taskComparator)
                    .map { it.toUiTask() },
                nextWaitingTasks = if (waitingTasks.isNotEmpty()) {
                    val (date, nextTasks) = waitingTasks
                        .groupBy { (it.type as Task.Type.Waiting).date }
                        .entries
                        .minBy { it.key }
                    NextWaitingTasks(
                        date = date,
                        tasks = nextTasks.map { it.toUiTask() }
                    )
                } else {
                    null
                },
                waitingCount = waitingTasks.size,
                projectTasks = tasks
                    .filter { it.type == Task.Type.Project }
                    .sortedWith(taskComparator)
                    .map { it.toUiTask() },
                maybeTasks = tasks
                    .filter { it.type == Task.Type.Maybe }
                    .sortedWith(taskComparator)
                    .map { it.toUiTask() }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = State(
                isSyncing = false,
                hasChanges = false,
                inboxCount = 0,
                todoTasks = emptyList(),
                nextWaitingTasks = null,
                waitingCount = 0,
                projectTasks = emptyList(),
                maybeTasks = emptyList()
            )
        )

    init {
        synchronizer.initiateSync()
    }

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

    fun onSync() {
        synchronizer.initiateSync()
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
