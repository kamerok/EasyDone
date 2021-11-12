package easydone.feature.inbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import easydone.core.domain.DomainRepository
import easydone.core.domain.model.Task
import easydone.coreui.design.UiTask
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn


internal class InboxViewModel(
    repository: DomainRepository,
    private val navigator: InboxNavigator
) : ViewModel() {

    val state: StateFlow<State> =
        repository.getTasks(Task.Type.Inbox::class)
            .onEach { if (it.isEmpty()) navigator.close() }
            .map { State(it.map { it.toUiTask() }) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = State(emptyList())
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
