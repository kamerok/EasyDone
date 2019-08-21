package easydone.core.network

import easydone.core.model.Task
import java.util.Date


data class TaskDelta(
    val id: String,
    val type: Task.Type?,
    val title: String?,
    val description: String?,
    val dueDate: Date?,
    val dueDateChanged: Boolean,
    val isDone: Boolean?
)
