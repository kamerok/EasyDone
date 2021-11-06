package easydone.feature.edittask

internal data class State(
    val type: String,
    val title: String,
    val description: String,
    val isUrgent: Boolean,
    val isImportant: Boolean
)
