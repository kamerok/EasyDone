package easydone.core.model


data class TaskTemplate(
    val type: Task.Type,
    val title: String,
    val description: String
)