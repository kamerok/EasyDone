package easydone.core.domain

import easydone.core.domain.model.Task

interface Network {
    suspend fun getAllTasks(): List<Task>
    suspend fun syncTaskDelta(delta: TaskDelta)
}
