package easydone.core.domain

import easydone.core.database.ChangeEntry
import easydone.core.database.EntityField
import easydone.core.database.MyDatabase
import easydone.core.model.Task
import easydone.core.network.Network
import easydone.core.network.TaskDelta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


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

    fun isSyncing(): Flow<Boolean> = flow {
        stateChannel.consumeEach { emit(it) }
    }

    fun observeChanges(): Flow<Long> = database.observeChangesCount()

    fun initiateSync() {
        GlobalScope.launch(Dispatchers.IO) {
            val currentJob = syncJob
            if (currentJob == null || !currentJob.isActive) {
                syncJob = launch { sync() }
                    .also { it.invokeOnCompletion { syncJob = null } }
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
            database.putData(network.getAllTasks())
        } catch (e: Exception) {
            //TODO: handle sync error
        } finally {
            stateChannel.send(false)
        }
    }
}

private fun ChangeEntry.toDelta() = TaskDelta(
    id = entityId,
    type = fields[EntityField.TYPE] as Task.Type?,
    title = fields[EntityField.TITLE] as String?,
    description = fields[EntityField.DESCRIPTION] as String?,
    isDone = fields[EntityField.IS_DONE] as Boolean?
)