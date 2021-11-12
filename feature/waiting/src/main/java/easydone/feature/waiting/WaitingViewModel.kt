package easydone.feature.waiting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import easydone.core.domain.DomainRepository
import easydone.core.domain.model.Task
import easydone.coreui.design.UiTask
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


internal class WaitingViewModel(
    repository: DomainRepository,
    private val navigator: WaitingNavigator
) : ViewModel() {

    val state: StateFlow<State> =
        repository.getTasks(Task.Type.Waiting::class)
            .map { tasks ->
                State(
                    tasks
                        .groupBy { (it.type as Task.Type.Waiting).date }
                        .mapValues { (_, dateTasks) -> dateTasks.map { it.toUiTask() } }
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = State(emptyMap())
            )

    fun onTaskClick(task: UiTask) {
        navigator.openTask(task.id)
    }

    private fun Task.toUiTask() = UiTask(
        id = id,
        title = title,
        hasDescription = description.isNotBlank(),
        isUrgent = markers.isUrgent,
        isImportant = markers.isImportant
    )
}
