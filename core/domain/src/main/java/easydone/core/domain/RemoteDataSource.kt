package easydone.core.domain

import easydone.core.domain.model.Task
import easydone.core.domain.model.TaskDelta

interface RemoteDataSource {
    suspend fun isConnected(): Boolean
    suspend fun getAllTasks(): List<Task>
    suspend fun syncTaskDelta(delta: TaskDelta)
    suspend fun disconnect()
}
