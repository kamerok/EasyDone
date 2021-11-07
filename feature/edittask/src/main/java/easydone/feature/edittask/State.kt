package easydone.feature.edittask

//TODO model data for appropriate initial state
internal data class State(
    val type: String,
    val title: String,
    val description: String,
    val isUrgent: Boolean,
    val isImportant: Boolean
)
