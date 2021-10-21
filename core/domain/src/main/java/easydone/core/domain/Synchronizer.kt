package easydone.core.domain

import easydone.core.domain.database.ChangeEntry
import easydone.core.domain.database.Database
import easydone.core.domain.database.EntityField.DESCRIPTION
import easydone.core.domain.database.EntityField.DUE_DATE
import easydone.core.domain.database.EntityField.IS_DONE
import easydone.core.domain.database.EntityField.MARKERS
import easydone.core.domain.database.EntityField.TITLE
import easydone.core.domain.database.EntityField.TYPE
import easydone.core.domain.model.Markers
import easydone.core.domain.model.Task
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
    private val database: Database
) {

    private val stateChannel: BroadcastChannel<Boolean> = ConflatedBroadcastChannel(false)
    private var syncJob: Job? = null

    init {
        database.observeChangesCount()
            .onEach { if (it > 0L) initiateSync() }
            .launchIn(GlobalScope)
    }

    fun isSyncing(): Flow<Boolean> = stateChannel.openSubscription().consumeAsFlow()

    fun observeChanges(): Flow<Long> = database.observeChangesCount()

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
            val changes = database.getChanges()
            for (change in changes) {
                remoteDataSource.syncTaskDelta(change.toDelta())
                database.deleteChange(change.changeId)
            }
            val networkTasks = remoteDataSource.getAllTasks()
            database.transaction {
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
}

private fun ChangeEntry.toDelta() = TaskDelta(
    id = entityId,
    type = fields[TYPE] as Task.Type?,
    title = fields[TITLE] as String?,
    description = fields[DESCRIPTION] as String?,
    dueDate = fields[DUE_DATE] as LocalDate?,
    dueDateChanged = fields.containsKey(DUE_DATE),
    markers = fields[MARKERS] as Markers?,
    isDone = fields[IS_DONE] as Boolean?
)
