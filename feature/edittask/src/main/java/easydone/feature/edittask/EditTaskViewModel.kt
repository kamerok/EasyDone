package easydone.feature.edittask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import easydone.core.domain.DomainRepository
import easydone.core.domain.model.Markers
import easydone.core.domain.model.Task
import easydone.core.domain.model.TaskTemplate
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import java.io.Serializable


internal class EditTaskViewModel(
    private val args: Args,
    private val repository: DomainRepository,
    private val navigator: EditTaskNavigator
) : ViewModel() {

    private val eventChannel = Channel<Event>(Channel.UNLIMITED)
    private val actionChannel: Channel<Action> = Channel(capacity = Channel.UNLIMITED)

    @OptIn(FlowPreview::class)
    val state: StateFlow<State> = flow {
        if (args is Args.Edit) {
            emit(repository.getTask(args.id))
        } else {
            emit(null)
        }
    }
        .flatMapConcat { originalTask ->
            actionChannel
                .consumeAsFlow()
                .scan(
                    ContentState(
                        isCreate = originalTask == null,
                        type = originalTask?.type ?: Task.Type.Inbox,
                        title = originalTask?.title ?: "",
                        titleError = null,
                        description = originalTask?.description ?: (args as? Args.Create)?.title
                        ?: "",
                        isUrgent = originalTask?.markers?.isUrgent ?: false,
                        isImportant = originalTask?.markers?.isImportant ?: false
                    )
                ) { state, action -> reduce(originalTask, state, action) }
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

    private suspend fun reduce(
        originalTask: Task?,
        state: ContentState,
        action: Action
    ): ContentState = when (action) {
        is Action.TypeClick -> {
            eventChannel.trySend(OpenSelectType(state.type))
            state
        }
        is Action.TypeSelected -> {
            eventChannel.trySend(CloseSelectType)
            state.copy(type = action.type)
        }
        is Action.TitleChange -> state.copy(title = action.title, titleError = null)
        is Action.DescriptionChange -> state.copy(description = action.description)
        is Action.UrgentClick -> state.copy(isUrgent = !state.isUrgent)
        is Action.ImportantClick -> state.copy(isImportant = !state.isImportant)
        is Action.Save -> {
            if (state.title.isBlank()) {
                //todo extract resources
                state.copy(titleError = "Should not be empty")
            } else {
                val hasChanges =
                    state.title != originalTask?.title ?: "" ||
                            state.description != originalTask?.description ?: "" ||
                            state.type != originalTask?.type ?: Task.Type.Inbox ||
                            state.isUrgent != originalTask?.markers?.isUrgent ?: false ||
                            state.isImportant != originalTask?.markers?.isImportant ?: false
                if (!hasChanges) {
                    navigator.close()
                } else {
                    if (originalTask == null) {
                        repository.createTask(
                            TaskTemplate(
                                type = state.type,
                                title = state.title,
                                description = state.description,
                                isUrgent = state.isUrgent,
                                isImportant = state.isImportant
                            )
                        )
                    } else {
                        repository.saveTask(
                            originalTask.copy(
                                type = state.type,
                                title = state.title,
                                description = state.description,
                                markers = Markers(
                                    isUrgent = state.isUrgent,
                                    isImportant = state.isImportant
                                )
                            )
                        )
                    }
                    navigator.close()
                }
                state
            }
        }
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

    internal sealed class Args : Serializable {
        data class Create(
            val title: String = ""
        ) : Args()

        data class Edit(val id: String) : Args()
    }
}
