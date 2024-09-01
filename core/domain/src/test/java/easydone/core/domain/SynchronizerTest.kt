package easydone.core.domain

import assertk.assertThat
import assertk.assertions.containsOnly
import easydone.core.domain.model.Task
import easydone.core.domain.model.TaskDelta
import easydone.core.domain.model.TaskTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.reflect.KClass

class SynchronizerTest {

    @Test
    fun `Test saving task`() = runTest {
        val delta = taskDelta()
        val remoteDataSource = remoteDataSource(isTaskKnown = false)
        val localDataSource = localDataSource(listOf(delta))
        val synchronizer = Synchronizer(remoteDataSource, localDataSource)

        synchronizer.sync()

        assertThat(remoteDataSource.getCreatedDeltas()).containsOnly(delta)
    }

    @Test
    fun `Test updating task`() = runTest {
        val delta = taskDelta()
        val remoteDataSource = remoteDataSource(isTaskKnown = true)
        val localDataSource = localDataSource(listOf(delta))
        val synchronizer = Synchronizer(remoteDataSource, localDataSource)

        synchronizer.sync()

        assertThat(remoteDataSource.getUpdatedDeltas()).containsOnly(delta)
    }

    private fun taskDelta() = TaskDelta(
        id = 1,
        taskId = "taskId",
        type = Task.Type.Inbox,
        title = "New task",
        description = null,
        markers = null,
        isDone = null
    )

    private fun remoteDataSource(isTaskKnown: Boolean) = object : RemoteDataSource {
        private val createdDeltas: MutableList<TaskDelta> = mutableListOf()
        private val updatedDeltas: MutableList<TaskDelta> = mutableListOf()

        override suspend fun isConnected(): Boolean = true

        override suspend fun getAllTasks(): List<Task> = emptyList()

        override suspend fun isTaskKnownOnRemote(id: String): Boolean = isTaskKnown

        override suspend fun updateTask(delta: TaskDelta) {
            updatedDeltas.add(delta)
        }

        override suspend fun createTask(delta: TaskDelta) {
            createdDeltas.add(delta)
        }

        override suspend fun disconnect() {}

        fun getCreatedDeltas(): List<TaskDelta> = createdDeltas

        fun getUpdatedDeltas(): List<TaskDelta> = updatedDeltas
    }

    private fun localDataSource(changes: List<TaskDelta> = emptyList()) =
        object : LocalDataSource {
            override suspend fun getChanges(): List<TaskDelta> = changes

            override fun observeChangesCount(): Flow<Long> = emptyFlow()

            override fun observeTasks(): Flow<List<Task>> = emptyFlow()

            override fun observeTasks(type: KClass<out Task.Type>): Flow<List<Task>> = emptyFlow()

            override fun observeTask(id: String): Flow<Task> = emptyFlow()

            override suspend fun getTask(id: String): Task {
                TODO("Not yet implemented")
            }

            override suspend fun createTask(taskTemplate: TaskTemplate) {}

            override suspend fun updateTask(task: Task) {}

            override suspend fun refreshData(tasks: List<Task>, updatedTasks: List<Task>) {}

            override suspend fun deleteChange(id: Long) {
            }
        }
}