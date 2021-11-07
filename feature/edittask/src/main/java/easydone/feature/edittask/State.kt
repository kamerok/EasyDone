package easydone.feature.edittask

internal sealed class State

internal object IdleState : State()

internal data class ContentState(
    val type: String,
    val title: String,
    val titleError: String?,
    val description: String,
    val isUrgent: Boolean,
    val isImportant: Boolean
) : State()
