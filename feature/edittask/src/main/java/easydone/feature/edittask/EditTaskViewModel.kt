package easydone.feature.edittask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import easydone.core.domain.DomainRepository
import easydone.core.domain.model.Task
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter


internal class EditTaskViewModel(
    private val id: String,
    private val repository: DomainRepository
) : ViewModel() {

    private val actionChannel: Channel<Action> = Channel(capacity = Channel.UNLIMITED)

    val state: StateFlow<State> = flow { emit(repository.getTask(id)) }
        .flatMapConcat { originalTask ->
            actionChannel
                .consumeAsFlow()
                .scan(originalTask) { task, action -> reduce(originalTask, task, action) }
                .map { task ->
                    State(
                        type = task.type.format(task.dueDate),
                        title = task.title,
                        description = task.description,
                        isUrgent = task.markers.isUrgent,
                        isImportant = task.markers.isImportant
                    )
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = State(
                type = Task.Type.INBOX.format(),
                title = "",
                description = "",
                isUrgent = false,
                isImportant = false
            )
        )

    fun onTypeClick() {
        //TODO
    }

    fun onTitleChange(title: String) {
        actionChannel.trySend(Action.TitleChange(title))
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

    private suspend fun reduce(
        originalTask: Task,
        task: Task,
        action: Action
    ) = when (action) {
        is Action.TitleChange -> task.copy(title = action.title)
        is Action.DescriptionChange -> task.copy(description = action.description)
        is Action.UrgentClick -> task.copy(
            markers = task.markers.copy(isUrgent = !task.markers.isUrgent)
        )
        is Action.ImportantClick -> task.copy(
            markers = task.markers.copy(isImportant = !task.markers.isImportant)
        )
        is Action.Save -> {
            if (task == originalTask) {
                //TODO: close
            } else {
                //TODO: check error
                repository.saveTask(task)
                //TODO: close
            }
            task
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
        })
        Task.Type.MAYBE -> "MAYBE"
    }

    private sealed class Action {
        data class TitleChange(val title: String) : Action()
        data class DescriptionChange(val description: String) : Action()
        object UrgentClick : Action()
        object ImportantClick : Action()
        object Save : Action()
    }
}
