package easydone.core.domain

import easydone.core.database.Database
import easydone.core.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class Synchronizer(
    private val network: Network,
    private val database: Database
) {

    fun initiateSync() {
        GlobalScope.launch(Dispatchers.IO) {
            network.syncTasks(
                toUpdate = database.getTasksToUpdate(),
                toCreate = database.getTasksToCreate()
            )
            database.putData(network.getAllTasks())
        }
    }

}