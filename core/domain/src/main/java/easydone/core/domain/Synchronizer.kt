package easydone.core.domain

import easydone.core.domain.model.Task.Type.INBOX
import easydone.core.domain.model.Task.Type.WAITING
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import timber.log.error
import java.time.LocalDate


class Synchronizer(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) {

    private val stateChannel: BroadcastChannel<Boolean> = ConflatedBroadcastChannel(false)
    private var syncJob: Job? = null

    init {
        localDataSource.observeChangesCount()
            .onEach { if (it > 0L) initiateSync() }
            .launchIn(GlobalScope)
    }

    fun isSyncing(): Flow<Boolean> = stateChannel.openSubscription().consumeAsFlow()

    fun observeChanges(): Flow<Long> = localDataSource.observeChangesCount()

    fun initiateSync() {
        val currentJob = syncJob
        if (currentJob == null || !currentJob.isActive) {
            syncJob = GlobalScope.launch { sync() }.also {
                it.invokeOnCompletion { syncJob = null }
            }
        }
    }

    private suspend fun sync() {
        stateChannel.send(true)
        try {
            uploadChanges()
            val networkTasks = remoteDataSource.getAllTasks()
            localDataSource.transaction {
                clear()
                putData(networkTasks)
                val tasksWithDate = getTasksWithDate()
                tasksWithDate.forEach { task ->
                    when {
                        !task.dueDate!!.isAfter(LocalDate.now()) -> runBlocking {
                            updateTask(task.copy(dueDate = null, type = INBOX))
                        }
                        task.dueDate!!.isAfter(LocalDate.now()) -> runBlocking {
                            updateTask(task.copy(type = WAITING))
                        }
                    }
                }
                getTasks(WAITING).forEach { task ->
                    if (task.dueDate == null) {
                        updateTask(task.copy(type = INBOX))
                    }
                }

            }
        } catch (e: Exception) {
            Timber.error(e) { "sync error" }
        } finally {
            stateChannel.send(false)
        }
    }

    private suspend fun uploadChanges() {
        localDataSource.getChanges().forEach { change ->
            remoteDataSource.syncTaskDelta(change)
            localDataSource.deleteChange(change.id)
        }
    }
}
