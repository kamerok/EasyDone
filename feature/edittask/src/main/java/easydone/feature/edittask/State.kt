package easydone.feature.edittask

import easydone.core.domain.model.Task

internal sealed class State

internal data object IdleState : State()

internal data class ContentState(
    val isCreate: Boolean,
    val type: Task.Type,
    val title: String,
    val titleError: String?,
    val description: String,
    val isUrgent: Boolean,
    val isImportant: Boolean
) : State()

internal sealed class Event

internal data class OpenSelectType(val currentType: Task.Type) : Event()

internal data object CloseSelectType : Event()
