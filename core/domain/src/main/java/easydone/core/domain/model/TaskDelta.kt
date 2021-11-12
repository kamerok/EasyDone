package easydone.core.domain.model


data class TaskDelta(
    val id: Long,
    val taskId: String,
    val type: Task.Type?,
    val title: String?,
    val description: String?,
    val markers: Markers?,
    val isDone: Boolean?
)
