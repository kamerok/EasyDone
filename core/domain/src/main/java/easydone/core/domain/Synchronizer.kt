package easydone.core.domain

import easydone.core.domain.model.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.error
import java.time.LocalDate


class Synchronizer(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val scope: CoroutineScope = GlobalScope
) {

    private val syncProgressState = MutableStateFlow(false)
    private var syncJob: Job? = null

    init {
        localDataSource.observeChangesCount()
            .onEach { if (it > 0L) initiateSync() }
            .launchIn(scope)
    }

    fun isSyncing(): Flow<Boolean> = syncProgressState

    fun observeChanges(): Flow<Long> = localDataSource.observeChangesCount()

    fun initiateSync() {
        val currentJob = syncJob
        if (currentJob == null || !currentJob.isActive) {
            syncJob = syncWithProgress().also {
                it.invokeOnCompletion { syncJob = null }
            }
        }
    }

    private fun syncWithProgress(): Job = scope.launch {
        syncProgressState.value = true
        try {
            sync()
        } catch (e: Exception) {
            Timber.error(e) { "sync error" }
        } finally {
            syncProgressState.value = false
        }
    }

    private suspend fun sync() {
        uploadChanges()
        refreshLocalData()
    }

    private suspend fun uploadChanges() {
        localDataSource.getChanges().forEach { change ->
            remoteDataSource.syncTaskDelta(change)
            localDataSource.deleteChange(change.id)
        }
    }

    private suspend fun refreshLocalData() {
        val remoteTasks = remoteDataSource.getAllTasks()
        val updatedTasks = updateWaitingTasks(remoteTasks, LocalDate.now())
        localDataSource.refreshData(remoteTasks, updatedTasks)
    }

    companion object {
        internal fun updateWaitingTasks(tasks: List<Task>, today: LocalDate): List<Task> =
            tasks.mapNotNull { task ->
                if (task.type is Task.Type.Waiting && !task.type.date.isAfter(today)) {
                    task.copy(type = Task.Type.Inbox)
                } else {
                    null
                }
            }
    }
}
