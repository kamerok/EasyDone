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

    private val stateChannel: BroadcastChannel<State> = ConflatedBroadcastChannel(State.Synced)

    fun getState(): Flow<State> = flow {
        stateChannel.consumeEach { emit(it) }
    }

    fun initiateSync() {
        GlobalScope.launch(Dispatchers.IO) {
//            val changes = database.getChanges()
            stateChannel.send(State.SyncInProgress)
            try {
                /*network.syncTasks(
                    toUpdate = changes.filter { it.first == Action.UPDATE }.map { it.second },
                    toCreate = changes.filter { it.first == Action.CREATE }.map { it.second }
                )*/
                database.putData(network.getAllTasks())
                stateChannel.send(State.Synced)
            } catch (e: Exception) {
                stateChannel.send(State.HasChanges)
            }
        }
    }

    sealed class State {

        object Synced : State()

        object SyncInProgress : State()

        object HasChanges : State()

    }

}