package easydone.core.sandbox

import easydone.core.domain.RemoteDataSource
import easydone.core.domain.model.Task
import easydone.core.domain.model.TaskDelta


class SandboxRemoteDataSource : RemoteDataSource {

    override suspend fun isConnected(): Boolean = true

    override suspend fun getAllTasks(): List<Task> = emptyList()

    override suspend fun syncTaskDelta(delta: TaskDelta) {}

    override suspend fun disconnect() {}
}
