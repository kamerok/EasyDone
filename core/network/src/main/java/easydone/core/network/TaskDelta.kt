package easydone.core.network

import easydone.core.model.Task


data class TaskDelta(
    val id: String,
    val type: Task.Type?,
    val title: String?,
    val description: String?,
    val isDone: Boolean?
)