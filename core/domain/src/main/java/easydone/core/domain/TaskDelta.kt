package easydone.core.domain

import easydone.core.domain.model.Markers
import easydone.core.domain.model.Task
import java.time.LocalDate


data class TaskDelta(
    val id: String,
    val type: Task.Type?,
    val title: String?,
    val description: String?,
    val dueDate: LocalDate?,
    val dueDateChanged: Boolean,
    val markers: Markers?,
    val isDone: Boolean?
)
