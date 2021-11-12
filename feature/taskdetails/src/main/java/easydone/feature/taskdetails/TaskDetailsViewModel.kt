package easydone.feature.taskdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import easydone.core.domain.DomainRepository
import easydone.core.domain.model.Task
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
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
                typeText = task.type.format(),
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
    val events: Flow<Event> get() = eventChannel.receiveAsFlow()

    fun onEdit() {
        navigator.editTask(id)
    }

    fun onMove() {
        task?.let { task ->
            eventChannel.trySend(SelectType(task.type))
        }
    }

    fun onTypeSelected(type: Task.Type) {
        task?.let { task ->
            viewModelScope.launch {
                repository.saveTask(task.copy(type = type))
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
    private fun Task.Type.format() = when (this) {
        is Task.Type.Inbox -> "INBOX"
        is Task.Type.ToDo -> "TO-DO"
        is Task.Type.Waiting -> "WAITING".plus(date.let {
            val period = Period.between(LocalDate.now(), it)
            val periodString = buildString {
                if (period.years > 0) {
                    append("${period.years}y ")
                }
                if (period.months > 0) {
                    append("${period.months}m ")
                }
                append("${period.days}d")
            }
            " until ${it.format(DateTimeFormatter.ofPattern("d MMM y"))} ($periodString)"
        })
        is Task.Type.Maybe -> "MAYBE"
    }

}
