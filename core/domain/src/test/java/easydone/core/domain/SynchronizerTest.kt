package easydone.core.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import easydone.core.domain.model.Task
import easydone.core.domain.model.TaskDelta
import easydone.core.domain.model.TaskTemplate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import kotlin.reflect.KClass

@ExperimentalCoroutinesApi
class SynchronizerTest {

    @Test
    fun `False progress on start`() = runBlockingTest {
        val synchronizer = Synchronizer(ImmediateRemoteDataSource(), ImmediateLocalDataSource, this)

        assertThat(synchronizer.isSyncing().first()).isFalse()
    }

    @Test
    fun `True progress on executing`() = runBlockingTest {
        val remoteDataSource = ImmediateRemoteDataSource(pauseExecution = true)
        val synchronizer = Synchronizer(remoteDataSource, ImmediateLocalDataSource, this)

        synchronizer.initiateSync()

        assertThat(synchronizer.isSyncing().first()).isTrue()
        remoteDataSource.completeWaitingCoroutines()
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

    @Test
    fun `Single invocation on restart`() = runBlockingTest {
        val remoteDataSource = ImmediateRemoteDataSource(pauseExecution = true)
        val synchronizer = Synchronizer(remoteDataSource, ImmediateLocalDataSource, this)

        synchronizer.initiateSync()
        synchronizer.initiateSync()

        assertThat(remoteDataSource.invokeCount).isEqualTo(1)
        remoteDataSource.completeWaitingCoroutines()
    }

    object ErrorRemoteDataSource : RemoteDataSource {
        override suspend fun getAllTasks(): List<Task> {
            throw Exception("sample error")
        }

        override suspend fun syncTaskDelta(delta: TaskDelta) {
            throw Exception("sample error")
        }
    }

    class ImmediateRemoteDataSource(
        private val pauseExecution: Boolean = false
    ) : RemoteDataSource {
        var invokeCount: Int = 0
            private set
        private val waiter = Waiter()

        fun completeWaitingCoroutines() = waiter.notify()

        override suspend fun getAllTasks(): List<Task> {
            invokeCount++
            if (pauseExecution) {
                waiter.wait()
            }
            return emptyList()
        }

        override suspend fun syncTaskDelta(delta: TaskDelta) {}
    }

    object ImmediateLocalDataSource : LocalDataSource {
        override suspend fun getChanges(): List<TaskDelta> = emptyList()

        override fun observeChangesCount(): Flow<Long> = flowOf()

        override fun observeTasks(type: KClass<out Task.Type>): Flow<List<Task>> = flowOf()

        override fun observeTask(id: String): Flow<Task> = flowOf()

        override suspend fun getTask(id: String): Task =
            throw Exception("not implemented")

        override suspend fun createTask(taskTemplate: TaskTemplate) {}

        override suspend fun updateTask(task: Task) {}

        override suspend fun refreshData(tasks: List<Task>, updatedTasks: List<Task>) {}

        override suspend fun deleteChange(id: Long) {}
    }

    @JvmInline
    value class Waiter(private val channel: Channel<Unit> = Channel(0)) {

        suspend fun wait() {
            channel.receive()
        }

        fun notify() {
            channel.trySend(Unit)
        }
    }

}
