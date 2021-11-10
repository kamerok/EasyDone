package easydone.feature.taskdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import easydone.core.domain.DomainRepository
import easydone.core.domain.model.Task
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter


internal class TaskDetailsViewModel(
    private val id: String,
    private val repository: DomainRepository,
    private val navigator: TaskDetailsNavigator
) : ViewModel() {

    private var task: Task? = null
    private val eventChannel = Channel<Event>(Channel.UNLIMITED)

    val state: StateFlow<State> = repository.observeTask(id)
        .onEach { task = it }
        .map { task ->
            State(
                typeText = task.type.format(task.dueDate),
                title = task.title,
                description = task.description,
                isUrgent = task.markers.isUrgent,
                isImportant = task.markers.isImportant
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = State(
                "",
                "",
                "",
                isUrgent = false,
                isImportant = false
            )
        )
    val events: Flow<Event> = eventChannel.consumeAsFlow()

    fun onEdit() {
        navigator.editTask(id)
    }

    fun onMove() {
        task?.let { task ->
            eventChannel.trySend(Event.SelectType(task.type, task.dueDate))
        }
    }

    fun onTypeSelected(type: Task.Type, date: LocalDate? = null) {
        task?.let { task ->
            viewModelScope.launch {
                repository.saveTask(task.copy(type = type, dueDate = date))
                navigator.close()
            }
        }
    }

    fun onArchive() {
        viewModelScope.launch {
            repository.archiveTask(id)
            navigator.close()
        }
    }

    //TODO: extract resources, reuse format logic
    private fun Task.Type.format(date: LocalDate? = null) = when (this) {
        Task.Type.INBOX -> "INBOX"
        Task.Type.TO_DO -> "TO-DO"
        Task.Type.WAITING -> "WAITING".plus(date?.let {
            val period = Period.between(LocalDate.now(), it)
            val periodString = buildString {
                if (period.years > 0) {
                    append("${period.years}y ")
                }
                if (period.months > 0 || period.years > 0) {
                    append("${period.months}m ")
                }
                append("${period.days}d")
            }
            " until ${it.format(DateTimeFormatter.ofPattern("d MMM y"))} ($periodString)"
        } ?: "")
        Task.Type.MAYBE -> "MAYBE"
    }

}

sealed class Event {
    data class SelectType(val currentType: Task.Type, val date: LocalDate?) : Event()
}
