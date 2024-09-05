package easydone.core.domain

import assertk.assertThat
import assertk.assertions.containsOnly
import easydone.core.domain.model.Markers
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
    fun `Delta for unknown task should create a card on remote`() = runTest {
        val delta = taskDelta()
        val remoteDataSource = remoteDataSource(isTaskKnown = false)
        val localDataSource = localDataSource(listOf(delta))
        val synchronizer = Synchronizer(remoteDataSource, localDataSource)

        synchronizer.sync()

        assertThat(remoteDataSource.getCreatedDeltas()).containsOnly(delta)
    }

    @Test
    fun `Created card should be saved locally`() {
        //ignore original server card with this id

    }

    @Test
    fun `Delta for unknown task should update create a card on remote`() = runTest {
        val delta = taskDelta()
        val remoteDataSource = remoteDataSource(isTaskKnown = true)
        val localDataSource = localDataSource(listOf(delta))
        val synchronizer = Synchronizer(remoteDataSource, localDataSource)

        synchronizer.sync()

        assertThat(remoteDataSource.getUpdatedDeltas()).containsOnly(delta)
    }

    @Test
    fun `Updated card should be saved locally`() {
        //ignore original server card with this id

    }

    @Test
    fun `Task from remote source should be saved locally`() = runTest {
        val task = task("remote_task")
        val remoteDataSource = remoteDataSource(remoteTasks = listOf(task))
        val localDataSource = localDataSource()
        val synchronizer = Synchronizer(remoteDataSource, localDataSource)

        synchronizer.sync()

        assertThat(localDataSource.refreshedTasks()).containsOnly(task)
    }

    @Test
    fun `Waiting task with reached date should be updated`() {

    }

    private fun task(id: String) = Task(
        id = id,
        type = Task.Type.Inbox,
        title = "Task $id",
        description = "",
        markers = Markers(isUrgent = false, isImportant = false),
        isDone = false
    )

    private fun taskDelta() = TaskDelta(
        id = 1,
        taskId = "taskId",
        type = Task.Type.Inbox,
        title = "New task",
        description = null,
        markers = null,
        isDone = null
    )

    private fun remoteDataSource(
        isTaskKnown: Boolean = false,
        remoteTasks: List<Task> = emptyList(),
        returnedTask: Task = task("returned_task")
    ) = object : RemoteDataSource {
        private val createdDeltas: MutableList<TaskDelta> = mutableListOf()
        private val updatedDeltas: MutableList<TaskDelta> = mutableListOf()

        override suspend fun isConnected(): Boolean = true

        override suspend fun getAllTasks(): List<Task> = remoteTasks

        override suspend fun isTaskKnownOnRemote(id: String): Boolean = isTaskKnown

        override suspend fun updateTask(delta: TaskDelta): Task {
            updatedDeltas.add(delta)
            return returnedTask
        }

        override suspend fun createTask(delta: TaskDelta): Task {
            createdDeltas.add(delta)
            return returnedTask
        }

        override suspend fun disconnect() {}

        fun getCreatedDeltas(): List<TaskDelta> = createdDeltas

        fun getUpdatedDeltas(): List<TaskDelta> = updatedDeltas
    }

    private fun localDataSource(changes: List<TaskDelta> = emptyList()) =
        object : LocalDataSource {
            private var refreshedTasks: List<Task> = emptyList()

            override suspend fun getChanges(): List<TaskDelta> = changes

            override fun observeChangesCount(): Flow<Long> = emptyFlow()

            override fun observeTasks(): Flow<List<Task>> = emptyFlow()

            override fun observeTasks(type: KClass<out Task.Type>): Flow<List<Task>> = emptyFlow()

            override fun observeTask(id: String): Flow<Task> = emptyFlow()

            override suspend fun getTask(id: String): Task {
                throw NotImplementedError()
            }

            override suspend fun createTask(taskTemplate: TaskTemplate) {}

            override suspend fun updateTask(task: Task) {}

            override suspend fun refreshData(tasks: List<Task>, updatedTasks: List<Task>) {
                refreshedTasks = tasks
            }

            override suspend fun deleteChange(id: Long) {
            }

            fun refreshedTasks(): List<Task> = refreshedTasks

            override suspend fun removeAllData() {}
        }
}