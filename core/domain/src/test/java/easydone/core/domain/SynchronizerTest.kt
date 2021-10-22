package easydone.core.domain

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import easydone.core.domain.model.Task
import easydone.core.domain.model.TaskDelta
import easydone.core.domain.model.TaskTemplate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class SynchronizerTest {

    @Test
    fun `False progress on start`() = runBlockingTest {
        val synchronizer = Synchronizer(ImmediateRemoteDataSource(), ImmediateLocalDataSource, this)

        assertThat(synchronizer.isSyncing().first()).isFalse()
    }

    @Test
    fun `True progress on executing`() = runBlockingTest {
        val remoteDataSource = ImmediateRemoteDataSource()
        val synchronizer = Synchronizer(remoteDataSource, ImmediateLocalDataSource, this)

        remoteDataSource.afterCall = {
            launch { assertThat(synchronizer.isSyncing().first()).isTrue() }
        }
        synchronizer.initiateSync()
    }

    @Test
    fun `False progress on success`() = runBlockingTest {
        val synchronizer = Synchronizer(ImmediateRemoteDataSource(), ImmediateLocalDataSource, this)

        synchronizer.initiateSync()

        assertThat(synchronizer.isSyncing().first()).isFalse()
    }

    @Test
    fun `False progress on error`() = runBlockingTest {
        val synchronizer = Synchronizer(ErrorRemoteDataSource, ImmediateLocalDataSource, this)

        synchronizer.initiateSync()

        assertThat(synchronizer.isSyncing().first()).isFalse()
    }

    object ErrorRemoteDataSource : RemoteDataSource {
        override suspend fun getAllTasks(): List<Task> {
            throw Exception("sample error")
        }

        override suspend fun syncTaskDelta(delta: TaskDelta) {
            throw Exception("sample error")
        }
    }

    class ImmediateRemoteDataSource(var afterCall: () -> Unit = {}) : RemoteDataSource {
        override suspend fun getAllTasks(): List<Task> = emptyList<Task>().also { afterCall() }

        override suspend fun syncTaskDelta(delta: TaskDelta) {
            afterCall()
        }
    }

    object ImmediateLocalDataSource : LocalDataSource {
        override suspend fun getChanges(): List<TaskDelta> = emptyList()

        override fun observeChangesCount(): Flow<Long> = flowOf()

        override fun observeTasks(type: Task.Type): Flow<List<Task>> = flowOf()

        override suspend fun getTask(id: String): Task =
            throw Exception("not implemented")

        override suspend fun createTask(taskTemplate: TaskTemplate) {}

        override suspend fun updateTask(task: Task) {}

        override suspend fun refreshData(tasks: List<Task>, updatedTasks: List<Task>) {}

        override suspend fun deleteChange(id: Long) {}
    }

}
