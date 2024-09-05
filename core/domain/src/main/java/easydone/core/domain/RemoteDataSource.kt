package easydone.core.domain

import easydone.core.domain.model.Task
import easydone.core.domain.model.TaskDelta

interface RemoteDataSource {
    suspend fun isConnected(): Boolean
    suspend fun getAllTasks(): List<Task>
    suspend fun isTaskKnownOnRemote(id: String): Boolean
    suspend fun updateTask(delta: TaskDelta): Task
    suspend fun createTask(delta: TaskDelta): Task
    suspend fun disconnect()
}
