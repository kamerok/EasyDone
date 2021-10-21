package easydone.core.network

import easydone.core.model.Task

interface Network {
    suspend fun getAllTasks(): List<Task>
    suspend fun syncTaskDelta(delta: TaskDelta)
}
