package easydone.core.domain.model

import java.time.LocalDate


data class TaskDelta(
    val id: Long,
    val taskId: String,
    val type: Task.Type?,
    val title: String?,
    val description: String?,
    val dueDate: LocalDate?,
    val dueDateChanged: Boolean,
    val markers: Markers?,
    val isDone: Boolean?
)
