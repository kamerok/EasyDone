package easydone.core.domain

import easydone.core.database.MyDatabase
import easydone.core.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch


class Synchronizer(
    private val network: Network,
    private val database: MyDatabase
) {

    private val stateChannel: BroadcastChannel<Boolean> = ConflatedBroadcastChannel(false)

    fun isSyncing(): Flow<Boolean> = flow {
        stateChannel.consumeEach { emit(it) }
    }

    fun initiateSync() {
        GlobalScope.launch(Dispatchers.IO) {
            stateChannel.send(true)
            try {
                val changes = database.getChanges()
                for (change in changes) {
                    network.syncChange(change.entityId, change.fields)
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

}