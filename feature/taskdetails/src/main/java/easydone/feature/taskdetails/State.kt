package easydone.feature.taskdetails

import easydone.core.domain.model.Task


internal data class State(
    val typeText: String,
    val title: String,
    val description: String,
    val isUrgent: Boolean,
    val isImportant: Boolean
)

internal sealed class Event

internal data class SelectType(val currentType: Task.Type) : Event()
internal data class OpenEdit(val id: String) : Event()
internal data object CloseMove : Event()
internal data object CloseArchive : Event()
