package easydone.feature.edittask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import easydone.core.domain.DomainRepository
import easydone.core.domain.model.Markers
import easydone.core.domain.model.Task
import easydone.core.domain.model.TaskTemplate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter


internal class EditTaskViewModel(
    private val id: String?,
    private val repository: DomainRepository,
    private val navigator: EditTaskNavigator
) : ViewModel() {

    private val eventChannel = Channel<Event>(Channel.UNLIMITED)
    private val actionChannel: Channel<Action> = Channel(capacity = Channel.UNLIMITED)

    val state: StateFlow<State> = flow {
        if (id != null) {
            emit(repository.getTask(id))
        } else {
            emit(
                Task(
                    id = "",
                    type = Task.Type.Inbox,
                    title = "",
                    description = "",
                    markers = Markers(isUrgent = false, isImportant = false),
                    isDone = false
                )
            )
        }
    }
        .flatMapConcat { originalTask ->
            actionChannel
                .consumeAsFlow()
                .scan(originalTask) { task, action -> reduce(originalTask, task, action) }
                .map { task ->
                    ContentState(
                        isCreate = id == null,
                        type = task.type.format(),
                        title = task.title,
                        titleError = if (task.title.isBlank()) "Should not be empty" else null,
                        description = task.description,
                        isUrgent = task.markers.isUrgent,
                        isImportant = task.markers.isImportant
                    )
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = IdleState
        )
    val events: Flow<Event> get() = eventChannel.receiveAsFlow()

    fun onTypeClick() {
        actionChannel.trySend(Action.TypeClick)
    }

    fun onTypeSelected(type: Task.Type) {
        actionChannel.trySend(Action.TypeSelected(type))
    }

    fun onTitleChange(title: String) {
        actionChannel.trySend(Action.TitleChange(title.replace(Regex("[\n\r]"), " ")))
    }

    fun onDescriptionChange(description: String) {
        actionChannel.trySend(Action.DescriptionChange(description))
    }

    fun onUrgentClick() {
        actionChannel.trySend(Action.UrgentClick)
    }

    fun onImportantClick() {
        actionChannel.trySend(Action.ImportantClick)
    }

    fun onSave() {
        actionChannel.trySend(Action.Save)
    }

    //TODO: refactor around state
    private suspend fun reduce(
        originalTask: Task,
        task: Task,
        action: Action
    ) = when (action) {
        is Action.TypeClick -> {
            eventChannel.trySend(OpenSelectType(task.type))
            task
        }
        is Action.TypeSelected -> {
            eventChannel.trySend(CloseSelectType)
            task.copy(type = action.type)
        }
        is Action.TitleChange -> task.copy(title = action.title)
        is Action.DescriptionChange -> task.copy(description = action.description)
        is Action.UrgentClick -> task.copy(
            markers = task.markers.copy(isUrgent = !task.markers.isUrgent)
        )
        is Action.ImportantClick -> task.copy(
            markers = task.markers.copy(isImportant = !task.markers.isImportant)
        )
        is Action.Save -> {
            if (task == originalTask && id != null) {
                navigator.close()
            } else {
                if (task.title.isNotBlank()) {
                    if (id != null) {
                        repository.saveTask(task)
                    } else {
                        repository.createTask(
                            TaskTemplate(
                                type = task.type,
                                title = task.title,
                                description = task.description,
                                isUrgent = task.markers.isUrgent,
                                isImportant = task.markers.isImportant
                            )
                        )
                    }
                    navigator.close()
                }
            }
            task
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
                if (period.days > 0) {
                    append("${period.days}d")
                }
            }
            " until ${it.format(DateTimeFormatter.ofPattern("d MMM y"))} ($periodString)"
        })
        is Task.Type.Maybe -> "MAYBE"
    }

    private sealed class Action {
        object TypeClick : Action()
        data class TypeSelected(val type: Task.Type) : Action()
        data class TitleChange(val title: String) : Action()
        data class DescriptionChange(val description: String) : Action()
        object UrgentClick : Action()
        object ImportantClick : Action()
        object Save : Action()
    }
}
