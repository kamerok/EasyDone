package easydone.core.network

import easydone.core.model.Task
import org.threeten.bp.LocalDate


data class TaskDelta(
    val id: String,
    val type: Task.Type?,
    val title: String?,
    val description: String?,
    val dueDate: LocalDate?,
    val dueDateChanged: Boolean,
    val isDone: Boolean?
)
