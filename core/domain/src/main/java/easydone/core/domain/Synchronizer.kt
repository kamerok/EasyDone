package easydone.core.domain

import easydone.core.database.ChangeEntry
import easydone.core.database.EntityField.DESCRIPTION
import easydone.core.database.EntityField.DUE_DATE
import easydone.core.database.EntityField.IS_DONE
import easydone.core.database.EntityField.TITLE
import easydone.core.database.EntityField.TYPE
import easydone.core.database.MyDatabase
import easydone.core.model.Task
import easydone.core.model.Task.Type.INBOX
import easydone.core.model.Task.Type.WAITING
import easydone.core.network.Network
import easydone.core.network.TaskDelta
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
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import timber.log.Timber
import timber.log.error
import java.util.Date


class Synchronizer(
    private val network: Network,
    private val database: MyDatabase
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
                network.syncTaskDelta(change.toDelta())
                database.deleteChange(change.changeId)
            }
            val networkTasks = network.getAllTasks()
            database.transaction {
                clear()
                putData(networkTasks)
                val tasksWithDate = getTasksWithDate()
                tasksWithDate.forEach { task ->
                    val taskDate = Instant.ofEpochMilli(task.dueDate!!.time)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    when {
                        !taskDate.isAfter(LocalDate.now()) -> runBlocking {
                            updateTask(task.copy(dueDate = null, type = INBOX))
                        }
                        taskDate.isAfter(LocalDate.now()) -> runBlocking {
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
    dueDate = fields[DUE_DATE] as Date?,
    dueDateChanged = fields.containsKey(DUE_DATE),
    isDone = fields[IS_DONE] as Boolean?
)
