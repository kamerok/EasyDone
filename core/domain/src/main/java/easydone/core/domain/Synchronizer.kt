package easydone.core.domain

import easydone.core.domain.model.Task
import easydone.core.domain.model.Task.Type.INBOX
import easydone.core.domain.model.Task.Type.WAITING
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
    private val localDataSource: LocalDataSource
) {

    private val syncProgressState = MutableStateFlow(false)
    private var syncJob: Job? = null

    init {
        localDataSource.observeChangesCount()
            .onEach { if (it > 0L) initiateSync() }
            .launchIn(GlobalScope)
    }

    fun isSyncing(): Flow<Boolean> = syncProgressState

    fun observeChanges(): Flow<Long> = localDataSource.observeChangesCount()

    fun initiateSync() {
        val currentJob = syncJob
        if (currentJob == null || !currentJob.isActive) {
            syncJob = GlobalScope.launch {
                syncProgressState.value = true
                try {
                    sync()
                } catch (e: Exception) {
                    Timber.error(e) { "sync error" }
                } finally {
                    syncProgressState.value = false
                }
            }.also {
                it.invokeOnCompletion { syncJob = null }
            }
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
                if (task.dueDate != null) {
                    when {
                        task.dueDate.isAfter(today) && task.type != WAITING -> task.copy(type = WAITING)
                        task.type == WAITING && task.dueDate.isBefore(today) ->
                            task.copy(type = INBOX, dueDate = null)
                        task.dueDate.isBefore(today) -> task.copy(dueDate = null)
                        else -> null
                    }
                } else {
                    if (task.type == WAITING) {
                        task.copy(type = INBOX)
                    } else {
                        null
                    }
                }
            }
    }
}
