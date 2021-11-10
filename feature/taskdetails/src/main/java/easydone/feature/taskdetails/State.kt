package easydone.feature.taskdetails


internal data class State(
    val typeText: String,
    val title: String,
    val description: String,
    val isUrgent: Boolean,
    val isImportant: Boolean
)
